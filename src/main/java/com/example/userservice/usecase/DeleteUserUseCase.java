package com.example.userservice.usecase;

import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.InvalidParameterException;

public class DeleteUserUseCase {
    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public boolean execute(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidParameterException("Invalid user ID: ID must be a positive number");
        }
        return repository.deleteUser(id);
    }
} 