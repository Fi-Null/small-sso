package com.small.sso.springboot.token.demo.config;

import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.filter.SmallSsoTokenFilter;
import com.small.sso.core.util.JedisUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 3:31 PM
 */
@Configuration
public class SmallSsoConfig implements DisposableBean {


    @Value("${small.sso.server}")
    private String smallSsoServer;

    @Value("${small.sso.logout.path}")
    private String smallSsoLogoutPath;

    @Value("${small-sso.excluded.paths}")
    private String smallSsoExcludedPaths;

    @Value("${small.sso.redis.address}")
    private String smallSsoRedisAddress;


    @Bean
    public FilterRegistrationBean smallSsoFilterRegistration() {

        // small-sso, redis init
        JedisUtil.init(smallSsoRedisAddress);

        // small-sso, filter init
        FilterRegistrationBean registration = new FilterRegistrationBean();

        registration.setName("smallSsoCookieFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(new SmallSsoTokenFilter());
        registration.addInitParameter(SmallSsoConf.SSO_SERVER, smallSsoServer);
        registration.addInitParameter(SmallSsoConf.SSO_LOGOUT_PATH, smallSsoLogoutPath);
        registration.addInitParameter(SmallSsoConf.SSO_EXCLUDED_PATHS, smallSsoExcludedPaths);

        return registration;
    }

    @Override
    public void destroy() throws Exception {

        // small-sso, redis close
        JedisUtil.close();
    }
}
