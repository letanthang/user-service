package com.example.userservice.domain.exception;

public class InvalidParameterException extends HttpException {
    public InvalidParameterException(String message) {
        super(message, 400);
    }
} 