package com.small.sso.springboot.token.demo.controller;

import com.small.sso.core.domain.ReturnT;
import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 3:37 PM
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    public ReturnT<SmallSsoUser> index(HttpServletRequest request) {
        SmallSsoUser smallUser = (SmallSsoUser) request.getAttribute(SmallSsoConf.SSO_USER);
        return new ReturnT<SmallSsoUser>(smallUser);
    }

}
