package com.small.sso.core.exception;

/**
 * @author null
 * @version 1.0
 * @title
 * @description
 * @createDate 1/3/20 4:19 PM
 */
public class SsoException extends RuntimeException {

    public SsoException(String msg) {
        super(msg);
    }

    public SsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SsoException(Throwable cause) {
        super(cause);
    }
}
