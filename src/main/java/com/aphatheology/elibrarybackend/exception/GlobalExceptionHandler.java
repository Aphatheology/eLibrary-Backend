package com.aphatheology.elibrarybackend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> dataIntegrityViolantionException(final DataIntegrityViolationException e, WebRequest request) {
        String constraintErrorMessage = e.getMostSpecificCause().getMessage();
        ErrorMessage message;

        if (constraintErrorMessage.contains("unique_user_book")) {
            message = new ErrorMessage(
                    HttpStatus.CONFLICT.value(),
                    new Date(),
                    "You've given feedback on this book already. Thanks",
                    request.getDescription(false));
        } else {
            message = new ErrorMessage(
                    HttpStatus.CONFLICT.value(),
                    new Date(),
                    constraintErrorMessage,
                    request.getDescription(false));
        }

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail;
//        ErrorMessage message = null;

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty("message", "Incorrect Email or Password");
        } else if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("message", "You are not authorized to access this resource");
        } else if (exception instanceof HttpMessageNotReadableException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty("message", "Request Body cannot be empty");
        } else if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty("message", "Invalid Token");
//            message = new ErrorMessage(
//                    HttpStatus.BAD_REQUEST.value(),
//                    new Date(),
//                    "Invalid Token",
//                    request.getDescription(false));
            //It will send statusCode of 200
        } else if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty("message", "Expired token");
        } else if (exception instanceof UsernameNotFoundException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
            errorDetail.setProperty("message", "User not found");
        } else {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
            errorDetail.setProperty("message", "Internal Server Error");
//            message = new ErrorMessage(
//                    HttpStatus.BAD_REQUEST.value(),
//                    new Date(),
//                    "Invalid Token",
//                    request.getDescription(false));
        }

        return errorDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<ErrorMessage> handleBadRequestException(ResourceNotFoundException ex, WebRequest request) {

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    @ExceptionHandler(ExistingEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final ResponseEntity<ErrorMessage> handleExistingEmailException(ExistingEmailException ex, WebRequest request) {

        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

}
