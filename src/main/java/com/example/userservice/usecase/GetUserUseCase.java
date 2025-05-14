package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;

import java.util.Optional;

public class GetUserUseCase {
    private final UserRepository repository;

    public GetUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> execute(String id) {
        return repository.getUserById(id);
    }
}
