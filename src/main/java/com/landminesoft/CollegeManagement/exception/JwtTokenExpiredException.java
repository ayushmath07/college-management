package com.landminesoft.CollegeManagement.exception;

public class JwtTokenExpiredException extends RuntimeException {

    public JwtTokenExpiredException() {
        super("JWT token has expired. Please login again.");
    }

    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
