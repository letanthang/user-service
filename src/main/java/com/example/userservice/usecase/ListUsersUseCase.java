package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;

import java.util.List;

public class ListUsersUseCase {
    private final UserRepository repository;

    public ListUsersUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> execute() {
        return repository.getAllUsers();
    }
} 