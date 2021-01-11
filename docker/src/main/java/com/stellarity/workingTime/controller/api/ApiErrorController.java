package com.stellarity.workingTime.controller.api;

import com.stellarity.workingTime.exception.AccessDeniedException;
import com.stellarity.workingTime.exception.BadTimeException;
import com.stellarity.workingTime.exception.TimeNotFoundException;
import com.stellarity.workingTime.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiErrorController extends ResponseEntityExceptionHandler {

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

        logger.info("MethodArgumentNotValidException Occured");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        //Get all errors
        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        List<String> errors = new ArrayList<String>(fieldErrors);
        errors.addAll(globalErrors);
        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);

    }

    @ExceptionHandler(TimeNotFoundException.class)
    protected ResponseEntity<String> handleResourceNotFound(TimeNotFoundException ex) {
        logger.info("TimeNotFoundException Occured");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Time does not found");
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        logger.info("UserNotFoundException Occured");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User does not found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        logger.info("AccessDeniedException Occured");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied");
    }


    @ExceptionHandler(BadTimeException.class)
    protected ResponseEntity<String> handleBadTime(BadTimeException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Bad time");
    }


}
