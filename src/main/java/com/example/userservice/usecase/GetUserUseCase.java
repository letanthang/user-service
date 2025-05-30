package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.InvalidParameterException;

import java.util.Optional;

public class GetUserUseCase {
    private final UserRepository repository;

    public GetUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> execute(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidParameterException("Invalid user ID: ID must be a positive number");
        }
        return repository.getUserById(id);
    }
}
