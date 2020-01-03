package com.small.sso.core.util;

import com.small.sso.core.conf.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 5:35 PM
 */
public class SsoTokenLoginUtil {

    /**
     * client login
     *
     * @param sessionId
     * @param smallUser
     */
    public static void login(String sessionId, SmallSsoUser smallUser) {

        String storeKey = SsoSessionIdUtil.parseStoreKey(sessionId);
        if (storeKey == null) {
            throw new RuntimeException("parseStoreKey Fail, sessionId:" + sessionId);
        }

        SsoLoginStoreUtil.put(storeKey, smallUser);
    }

    /**
     * client logout
     *
     * @param sessionId
     */
    public static void logout(String sessionId) {

        String storeKey = SsoSessionIdUtil.parseStoreKey(sessionId);
        if (storeKey == null) {
            return;
        }

        SsoLoginStoreUtil.remove(storeKey);
    }

    /**
     * client logout
     *
     * @param request
     */
    public static void logout(HttpServletRequest request) {
        String headerSessionId = request.getHeader(SmallSsoConf.SSO_SESSIONID);
        logout(headerSessionId);
    }


    /**
     * login check
     *
     * @param sessionId
     * @return
     */
    public static SmallSsoUser loginCheck(String sessionId) {

        String storeKey = SsoSessionIdUtil.parseStoreKey(sessionId);
        if (storeKey == null) {
            return null;
        }

        SmallSsoUser smallUser = SsoLoginStoreUtil.get(storeKey);
        if (smallUser != null) {
            String version = SsoSessionIdUtil.parseVersion(sessionId);
            if (smallUser.getVersion().equals(version)) {

                // After the expiration time has passed half, Auto refresh
                if ((System.currentTimeMillis() - smallUser.getExpireFreshTime()) > smallUser.getExpireMinute() / 2) {
                    smallUser.setExpireFreshTime(System.currentTimeMillis());
                    SsoLoginStoreUtil.put(storeKey, smallUser);
                }

                return smallUser;
            }
        }
        return null;
    }


    /**
     * login check
     *
     * @param request
     * @return
     */
    public static SmallSsoUser loginCheck(HttpServletRequest request) {
        String headerSessionId = request.getHeader(SmallSsoConf.SSO_SESSIONID);
        return loginCheck(headerSessionId);
    }

}
