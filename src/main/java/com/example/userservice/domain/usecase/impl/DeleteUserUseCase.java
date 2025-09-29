package com.example.userservice.domain.usecase.impl;

import com.example.userservice.domain.repository.UserRepository;
import com.example.userservice.domain.exception.InvalidParameterException;

public class DeleteUserUseCase implements com.example.userservice.domain.usecase.DeleteUserUseCase {
    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean execute(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidParameterException("Invalid user ID: ID must be a positive number");
        }
        return repository.deleteUser(id);
    }
} 