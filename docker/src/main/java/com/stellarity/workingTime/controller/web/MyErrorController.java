package com.stellarity.workingTime.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class MyErrorController {

    private static final Logger logger = LoggerFactory.getLogger(MyErrorController.class);

    @ExceptionHandler(SQLException.class)
    public String handleSQLException(HttpServletRequest request, Exception ex) {
        logger.info("SQLException Occured:: URL=" + request.getRequestURL());
        return "database_error";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "IOException occured")
    @ExceptionHandler(IOException.class)
    public String handleIOException() {
        logger.error("IOException handler executed");
        return "error-404";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad adress")
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleException() {
        logger.error("Bad number");
        return "error-400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleADEException() {
        logger.error("access denied");
        return "access-denied";
    }
}
