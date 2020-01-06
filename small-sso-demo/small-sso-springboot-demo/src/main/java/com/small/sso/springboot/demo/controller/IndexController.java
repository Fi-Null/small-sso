package com.small.sso.springboot.demo.controller;

import com.small.sso.core.domain.ReturnT;
import com.small.sso.core.domain.SmallSsoConf;
import com.small.sso.core.domain.SmallSsoUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String index(Model model, HttpServletRequest request) {

        SmallSsoUser smallUser = (SmallSsoUser) request.getAttribute(SmallSsoConf.SSO_USER);
        model.addAttribute("smallUser", smallUser);
        return "index";
    }

    @RequestMapping("/json")
    @ResponseBody
    public ReturnT<SmallSsoUser> json(Model model, HttpServletRequest request) {
        SmallSsoUser smallUser = (SmallSsoUser) request.getAttribute(SmallSsoConf.SSO_USER);
        return new ReturnT(smallUser);
    }

}
