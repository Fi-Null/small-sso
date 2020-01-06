package com.small.sso.core.util;

import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 5:21 PM
 */
public class SsoLoginStoreUtil {

    private static int redisExpireMinute = 1440;    // 1440 minute, 24 hour

    public static void setRedisExpireMinute(int redisExpireMinute) {
        if (redisExpireMinute < 30) {
            redisExpireMinute = 30;
        }
        SsoLoginStoreUtil.redisExpireMinute = redisExpireMinute;
    }

    public static int getRedisExpireMinute() {
        return redisExpireMinute;
    }

    /**
     * get
     *
     * @param storeKey
     * @return
     */
    public static SmallSsoUser get(String storeKey) {

        String redisKey = redisKey(storeKey);
        Object objectValue = JedisUtil.getObjectValue(redisKey);
        if (objectValue != null) {
            SmallSsoUser smallUser = (SmallSsoUser) objectValue;
            return smallUser;
        }
        return null;
    }

    /**
     * remove
     *
     * @param storeKey
     */
    public static void remove(String storeKey) {
        String redisKey = redisKey(storeKey);
        JedisUtil.del(redisKey);
    }

    /**
     * put
     *
     * @param storeKey
     * @param smallUser
     */
    public static void put(String storeKey, SmallSsoUser smallUser) {
        String redisKey = redisKey(storeKey);
        JedisUtil.setObjectValue(redisKey, smallUser, redisExpireMinute * 60);  // minute to second
    }

    private static String redisKey(String sessionId) {
        return SmallSsoConf.SSO_SESSIONID.concat("#").concat(sessionId);
    }
}
