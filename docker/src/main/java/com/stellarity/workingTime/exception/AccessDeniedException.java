package com.stellarity.workingTime.exception;


public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("Access denied");
    }
}
