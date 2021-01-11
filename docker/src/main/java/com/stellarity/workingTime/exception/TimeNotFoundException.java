package com.stellarity.workingTime.exception;

public class TimeNotFoundException extends RuntimeException {

    public TimeNotFoundException(Long id) {
        super("Could not find time " + id);
    }
}
