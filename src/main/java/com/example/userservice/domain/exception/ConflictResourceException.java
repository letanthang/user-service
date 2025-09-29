package com.example.userservice.domain.exception;

public class ConflictResourceException extends HttpException {
    private final String entity;

    public ConflictResourceException(String entity) {
        super(entity + " conflict resource", 409);
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }
}