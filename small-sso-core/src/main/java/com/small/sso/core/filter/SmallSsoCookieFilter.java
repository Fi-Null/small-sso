package com.small.sso.core.filter;

import com.small.sso.core.ant.AntPathMatcher;
import com.small.sso.core.ant.PathMatcher;
import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;
import com.small.sso.core.util.SsoCookieLoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 5:23 PM
 */
public class SmallSsoCookieFilter extends HttpServlet implements Filter {

    private static Logger logger = LoggerFactory.getLogger(SmallSsoCookieFilter.class);

    private static final PathMatcher antPathMatcher = new AntPathMatcher();

    private String ssoServer;
    private String logoutPath;
    private String excludedPaths;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ssoServer = filterConfig.getInitParameter(SmallSsoConf.SSO_SERVER);
        logoutPath = filterConfig.getInitParameter(SmallSsoConf.SSO_LOGOUT_PATH);
        excludedPaths = filterConfig.getInitParameter(SmallSsoConf.SSO_EXCLUDED_PATHS);

        logger.info("SmallSsoWebFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // make url
        String servletPath = req.getServletPath();

        // excluded path check
        if (excludedPaths != null && excludedPaths.trim().length() > 0) {
            for (String excludedPath : excludedPaths.split(",")) {
                String uriPattern = excludedPath.trim();

                // 支持ANT表达式
                if (antPathMatcher.match(uriPattern, servletPath)) {
                    // excluded path, allow
                    chain.doFilter(request, response);
                    return;
                }

            }
        }

        // logout path check
        if (logoutPath != null
                && logoutPath.trim().length() > 0
                && logoutPath.equals(servletPath)) {

            // remove cookie
            SsoCookieLoginUtil.removeSessionIdByCookie(req, res);

            // redirect logout
            String logoutPageUrl = ssoServer.concat(SmallSsoConf.SSO_LOGOUT);
            res.sendRedirect(logoutPageUrl);

            return;
        }

        // valid login user, cookie + redirect
        SmallSsoUser smallUser = SsoCookieLoginUtil.loginCheck(req, res);

        // valid login fail
        if (smallUser == null) {

            String header = req.getHeader("content-type");
            boolean isJson = header != null && header.contains("json");
            if (isJson) {

                // json msg
                res.setContentType("application/json;charset=utf-8");
                res.getWriter().println("{\"code\":" + SmallSsoConf.SSO_LOGIN_FAIL_RESULT.getCode()
                        + ", \"msg\":\""
                        + SmallSsoConf.SSO_LOGIN_FAIL_RESULT.getMsg() + "\"}");
                return;
            } else {

                // total link
                String link = req.getRequestURL().toString();

                // redirect logout
                String loginPageUrl = ssoServer.concat(SmallSsoConf.SSO_LOGIN)
                        + "?" + SmallSsoConf.REDIRECT_URL + "=" + link;

                res.sendRedirect(loginPageUrl);
                return;
            }

        }

        // ser sso user
        request.setAttribute(SmallSsoConf.SSO_USER, smallUser);


        // already login, allow
        chain.doFilter(request, response);
        return;
    }
}
