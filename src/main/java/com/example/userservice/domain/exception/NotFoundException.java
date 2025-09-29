package com.example.userservice.domain.exception;

public class NotFoundException extends HttpException {
    private final String entity;
    private final Object id;

    public NotFoundException(String entity, Object id, String message) {
        super(message, 404);
        this.entity = entity;
        this.id = id;
    }

    public NotFoundException(String entity, Object id) {
        super(entity + " with id " + id + " not found", 404);
        this.entity = entity;
        this.id = id;
    }

    public NotFoundException(String entity) {
        super(entity + " not found", 404);
        this.entity = entity;
        this.id = null;
    }

    public String getEntity() {
        return entity;
    }

    public Object getId() {
        return id;
    }
} 