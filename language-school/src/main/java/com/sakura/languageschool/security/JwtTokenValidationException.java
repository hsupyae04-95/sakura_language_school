package com.sakura.languageschool.security;

public class JwtTokenValidationException extends RuntimeException {

    public JwtTokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
