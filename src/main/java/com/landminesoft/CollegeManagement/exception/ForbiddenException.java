package com.landminesoft.CollegeManagement.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("You don't have permission to access this resource");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
