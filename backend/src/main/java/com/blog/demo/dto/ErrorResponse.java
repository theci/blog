package com.blog.demo.dto;

public class ErrorResponse {
    private String message;
    private String error;

    public ErrorResponse(String message) {
        this.message = message;
        this.error = "Bad Request";
    }

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}