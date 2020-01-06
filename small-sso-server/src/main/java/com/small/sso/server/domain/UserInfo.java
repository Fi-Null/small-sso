package com.small.sso.server.domain;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/6/20 11:35 AM
 */
public class UserInfo {

    private int userid;
    private String username;
    private String password;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
