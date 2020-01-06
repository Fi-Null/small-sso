package com.small.sso.server.service;

import com.small.sso.core.domain.ReturnT;
import com.small.sso.server.domain.UserInfo;

public interface UserService {

    public ReturnT<UserInfo> findUser(String username, String password);

}
