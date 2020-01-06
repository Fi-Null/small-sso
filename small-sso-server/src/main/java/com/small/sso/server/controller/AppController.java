package com.small.sso.server.controller;

import com.small.sso.core.domain.ReturnT;
import com.small.sso.core.domain.SmallSsoUser;
import com.small.sso.core.util.ParseSessionIdUtil;
import com.small.sso.core.util.SsoLoginStoreUtil;
import com.small.sso.core.util.SsoTokenLoginUtil;
import com.small.sso.server.domain.UserInfo;
import com.small.sso.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 1:57 PM
 */
@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private UserService userService;

    /**
     * Login
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ReturnT<String> login(String username, String password) {

        // valid login
        ReturnT<UserInfo> result = userService.findUser(username, password);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            return new ReturnT<String>(result.getCode(), result.getMsg());
        }

        // 1、make small-sso user
        SmallSsoUser smallUser = new SmallSsoUser();
        smallUser.setUserid(String.valueOf(result.getData().getUserid()));
        smallUser.setUsername(result.getData().getUsername());
        smallUser.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
        smallUser.setExpireMinute(SsoLoginStoreUtil.getRedisExpireMinute());
        smallUser.setExpireFreshTime(System.currentTimeMillis());


        // 2、generate sessionId + storeKey
        String sessionId = ParseSessionIdUtil.makeSessionId(smallUser);

        // 3、login, store storeKey
        SsoTokenLoginUtil.login(sessionId, smallUser);

        // 4、return sessionId
        return new ReturnT<String>(sessionId);
    }


    /**
     * Logout
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public ReturnT<String> logout(String sessionId) {
        // logout, remove storeKey
        SsoTokenLoginUtil.logout(sessionId);
        return ReturnT.SUCCESS;
    }

    /**
     * logincheck
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logincheck")
    @ResponseBody
    public ReturnT<SmallSsoUser> logincheck(String sessionId) {

        // logout
        SmallSsoUser smallUser = SsoTokenLoginUtil.loginCheck(sessionId);
        if (smallUser == null) {
            return new ReturnT<SmallSsoUser>(ReturnT.FAIL_CODE, "sso not login.");
        }
        return new ReturnT<SmallSsoUser>(smallUser);
    }

}
