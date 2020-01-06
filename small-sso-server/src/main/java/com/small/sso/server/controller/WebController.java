package com.small.sso.server.controller;

import com.small.sso.core.domain.ReturnT;
import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;
import com.small.sso.core.util.ParseSessionIdUtil;
import com.small.sso.core.util.SsoCookieLoginUtil;
import com.small.sso.core.util.SsoLoginStoreUtil;
import com.small.sso.server.domain.UserInfo;
import com.small.sso.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 2:05 PM
 */
@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {

        // login check
        SmallSsoUser smallUser = SsoCookieLoginUtil.loginCheck(request, response);

        if (smallUser == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("smallUser", smallUser);
            return "index";
        }
    }

    /**
     * Login page
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(SmallSsoConf.SSO_LOGIN)
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {

        // login check
        SmallSsoUser smallUser = SsoCookieLoginUtil.loginCheck(request, response);

        if (smallUser != null) {

            // success redirect
            String redirectUrl = request.getParameter(SmallSsoConf.REDIRECT_URL);
            if (redirectUrl != null && redirectUrl.trim().length() > 0) {

                String sessionId = SsoCookieLoginUtil.getSessionIdByCookie(request);
                String redirectUrlFinal = redirectUrl + "?" + SmallSsoConf.SSO_SESSIONID + "=" + sessionId;


                return "redirect:" + redirectUrlFinal;
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("errorMsg", request.getParameter("errorMsg"));
        model.addAttribute(SmallSsoConf.REDIRECT_URL, request.getParameter(SmallSsoConf.REDIRECT_URL));
        return "login";
    }

    /**
     * Login
     *
     * @param request
     * @param redirectAttributes
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(HttpServletRequest request,
                          HttpServletResponse response,
                          RedirectAttributes redirectAttributes,
                          String username,
                          String password,
                          String ifRemember) {

        boolean ifRem = (ifRemember != null && "on".equals(ifRemember)) ? true : false;

        // valid login
        ReturnT<UserInfo> result = userService.findUser(username, password);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            redirectAttributes.addAttribute("errorMsg", result.getMsg());

            redirectAttributes.addAttribute(SmallSsoConf.REDIRECT_URL, request.getParameter(SmallSsoConf.REDIRECT_URL));
            return "redirect:/login";
        }

        // 1、make small-sso user
        SmallSsoUser smallUser = new SmallSsoUser();
        smallUser.setUserid(String.valueOf(result.getData().getUserid()));
        smallUser.setUsername(result.getData().getUsername());
        smallUser.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        smallUser.setExpireMinute(SsoLoginStoreUtil.getRedisExpireMinute());
        smallUser.setExpireFreshTime(System.currentTimeMillis());


        // 2、make session id
        String sessionId = ParseSessionIdUtil.makeSessionId(smallUser);

        // 3、login, store storeKey + cookie sessionId
        SsoCookieLoginUtil.login(response, sessionId, smallUser, ifRem);

        // 4、return, redirect sessionId
        String redirectUrl = request.getParameter(SmallSsoConf.REDIRECT_URL);
        if (redirectUrl != null && redirectUrl.trim().length() > 0) {
            String redirectUrlFinal = redirectUrl + "?" + SmallSsoConf.SSO_SESSIONID + "=" + sessionId;
            return "redirect:" + redirectUrlFinal;
        } else {
            return "redirect:/";
        }

    }

    /**
     * Logout
     *
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(SmallSsoConf.SSO_LOGOUT)
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        // logout
        SsoCookieLoginUtil.logout(request, response);

        redirectAttributes.addAttribute(SmallSsoConf.REDIRECT_URL, request.getParameter(SmallSsoConf.REDIRECT_URL));
        return "redirect:/login";
    }
}
