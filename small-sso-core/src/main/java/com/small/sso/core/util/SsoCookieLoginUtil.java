package com.small.sso.core.util;

import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 5:35 PM
 */
public class SsoCookieLoginUtil {

    /**
     * client login
     *
     * @param response
     * @param sessionId
     * @param ifRemember   true: cookie not expire, false: expire when browser close （server cookie）
     * @param SmallSsoUser
     */
    public static void login(HttpServletResponse response,
                             String sessionId,
                             SmallSsoUser SmallSsoUser,
                             boolean ifRemember) {

        String storeKey = ParseSessionIdUtil.parseStoreKey(sessionId);
        if (storeKey == null) {
            throw new RuntimeException("parseStoreKey Fail, sessionId:" + sessionId);
        }

        SsoLoginStoreUtil.put(storeKey, SmallSsoUser);
        CookieUtil.set(response, SmallSsoConf.SSO_SESSIONID, sessionId, ifRemember);
    }

    /**
     * client logout
     *
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request,
                              HttpServletResponse response) {

        String cookieSessionId = CookieUtil.getValue(request, SmallSsoConf.SSO_SESSIONID);
        if (cookieSessionId == null) {
            return;
        }

        String storeKey = ParseSessionIdUtil.parseStoreKey(cookieSessionId);
        if (storeKey != null) {
            SsoLoginStoreUtil.remove(storeKey);
        }

        CookieUtil.remove(request, response, SmallSsoConf.SSO_SESSIONID);
    }


    /**
     * login check
     *
     * @param request
     * @param response
     * @return
     */
    public static SmallSsoUser loginCheck(HttpServletRequest request, HttpServletResponse response) {

        String cookieSessionId = CookieUtil.getValue(request, SmallSsoConf.SSO_SESSIONID);

        // cookie user
        SmallSsoUser smallSsoUser = SsoTokenLoginUtil.loginCheck(cookieSessionId);
        if (smallSsoUser != null) {
            return smallSsoUser;
        }

        // redirect user

        // remove old cookie
        SsoCookieLoginUtil.removeSessionIdByCookie(request, response);

        // set new cookie
        String paramSessionId = request.getParameter(SmallSsoConf.SSO_SESSIONID);
        smallSsoUser = SsoTokenLoginUtil.loginCheck(paramSessionId);
        if (smallSsoUser != null) {
            CookieUtil.set(response, SmallSsoConf.SSO_SESSIONID, paramSessionId, false);    // expire when browser close （client cookie）
            return smallSsoUser;
        }

        return null;
    }


    /**
     * client logout, cookie only
     *
     * @param request
     * @param response
     */
    public static void removeSessionIdByCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, SmallSsoConf.SSO_SESSIONID);
    }

    /**
     * get sessionid by cookie
     *
     * @param request
     * @return
     */
    public static String getSessionIdByCookie(HttpServletRequest request) {
        String cookieSessionId = CookieUtil.getValue(request, SmallSsoConf.SSO_SESSIONID);
        return cookieSessionId;
    }

}
