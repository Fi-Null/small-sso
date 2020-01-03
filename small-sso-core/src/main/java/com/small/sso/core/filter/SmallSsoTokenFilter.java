package com.small.sso.core.filter;

import com.small.sso.core.ant.AntPathMatcher;
import com.small.sso.core.ant.PathMatcher;
import com.small.sso.core.conf.SmallSsoConf;
import com.small.sso.core.domain.ReturnT;
import com.small.sso.core.domain.SmallSsoUser;
import com.small.sso.core.util.SsoTokenLoginUtil;
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
 * @createDate 1/3/20 5:58 PM
 */
public class SmallSsoTokenFilter extends HttpServlet implements Filter {

    private static Logger logger = LoggerFactory.getLogger(SmallSsoTokenFilter.class);

    private static final PathMatcher antPathMatcher = new AntPathMatcher();

    private String ssoServer;
    private String logoutPath;
    private String excludedPaths;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ssoServer = filterConfig.getInitParameter(SmallSsoConf.SSO_SERVER);
        logoutPath = filterConfig.getInitParameter(SmallSsoConf.SSO_LOGOUT_PATH);
        excludedPaths = filterConfig.getInitParameter(SmallSsoConf.SSO_EXCLUDED_PATHS);

        logger.info("SmallSsoTokenFilter init.");
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

        // logout filter
        if (logoutPath != null
                && logoutPath.trim().length() > 0
                && logoutPath.equals(servletPath)) {

            // logout
            SsoTokenLoginUtil.logout(req);

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":" + ReturnT.SUCCESS_CODE + ", \"msg\":\"\"}");

            return;
        }

        // login filter
        SmallSsoUser smallUser = SsoTokenLoginUtil.loginCheck(req);
        if (smallUser == null) {

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":" + SmallSsoConf.SSO_LOGIN_FAIL_RESULT.getCode() + ", \"msg\":\"" + SmallSsoConf.SSO_LOGIN_FAIL_RESULT.getMsg() + "\"}");
            return;
        }

        // ser sso user
        request.setAttribute(SmallSsoConf.SSO_USER, smallUser);

        // already login, allow
        chain.doFilter(request, response);
        return;
    }
}
