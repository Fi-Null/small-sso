package com.small.sso.server.config;

import com.small.sso.core.util.JedisUtil;
import com.small.sso.core.util.SsoLoginStoreUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 11:33 AM
 */
public class SmallSsoConfig implements InitializingBean, DisposableBean {

    @Value("${small.sso.redis.address}")
    private String redisAddress;

    @Value("${small.sso.redis.expire.minute}")
    private int redisExpireMinute;

    @Override
    public void afterPropertiesSet() throws Exception {
        SsoLoginStoreUtil.setRedisExpireMinute(redisExpireMinute);
        JedisUtil.init(redisAddress);
    }

    @Override
    public void destroy() throws Exception {
        JedisUtil.close();
    }

}