package com.example.userservice.domain.exception;

public class InternalServerException extends HttpException {
    public InternalServerException(String message) {
        super(message, 500);
    }
} 