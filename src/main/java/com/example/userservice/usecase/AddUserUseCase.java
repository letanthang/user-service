package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;

public class AddUserUseCase {
    private final UserRepository repository;

    public AddUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(String id, String name) {
        repository.addUser(new User(id, name));
    }
}
