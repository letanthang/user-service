package com.example.userservice.controller;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    public void handleAddUser(String name) {
        User user = new User();
        user.setName(name);
        repository.addUser(user);
    }

    public Optional<User> handleGetUser(String id) {
        return repository.getUserById(id);
    }

    public List<User> handleListUsers() {
        return repository.getAllUsers();
    }
}
