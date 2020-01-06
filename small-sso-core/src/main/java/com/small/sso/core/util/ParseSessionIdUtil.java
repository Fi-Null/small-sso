package com.small.sso.core.util;

import com.small.sso.core.domain.SmallSsoUser;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 5:17 PM
 */
public class ParseSessionIdUtil {

    /**
     * make client sessionId
     *
     * @param SmallSsoUser
     * @return
     */
    public static String makeSessionId(SmallSsoUser SmallSsoUser) {
        String sessionId = SmallSsoUser.getUserid().concat("_").concat(SmallSsoUser.getVersion());
        return sessionId;
    }

    /**
     * parse storeKey from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseStoreKey(String sessionId) {
        if (sessionId != null && sessionId.indexOf("_") > -1) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length == 2
                    && sessionIdArr[0] != null
                    && sessionIdArr[0].trim().length() > 0) {
                String userId = sessionIdArr[0].trim();
                return userId;
            }
        }
        return null;
    }

    /**
     * parse version from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseVersion(String sessionId) {
        if (sessionId != null && sessionId.indexOf("_") > -1) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length == 2
                    && sessionIdArr[1] != null
                    && sessionIdArr[1].trim().length() > 0) {
                String version = sessionIdArr[1].trim();
                return version;
            }
        }
        return null;
    }

}
