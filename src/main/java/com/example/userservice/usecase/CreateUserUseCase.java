package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.InvalidParameterException;

public class CreateUserUseCase {
    private final UserRepository repository;

    public CreateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(CreateUserRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new InvalidParameterException("Name is required");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidParameterException("Email is required");
        }

        User user = new User();
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setNickname(request.getNickname());
        user.setAvatar(request.getAvatar());
        user.setBirthdate(request.getBirthdate());
        user.setEmail(request.getEmail());
        
        repository.addUser(user);
        return user;
    }
} 