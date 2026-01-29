package com.landminesoft.CollegeManagement.exception;

public class JwtTokenInvalidException extends RuntimeException {

    public JwtTokenInvalidException() {
        super("JWT token is invalid or malformed.");
    }

    public JwtTokenInvalidException(String message) {
        super(message);
    }
}
