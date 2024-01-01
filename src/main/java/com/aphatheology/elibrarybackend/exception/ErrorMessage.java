package com.aphatheology.elibrarybackend.exception;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private List<String> errors;
    private String description;

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    public ErrorMessage(int statusCode, Date timestamp, String message) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public ErrorMessage(int statusCode, Date timestamp, List<String> errors, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public ErrorMessage(int statusCode, Date timestamp, String message, List<String> errors, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.errors = errors;
        this.description = description;
    }



    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
