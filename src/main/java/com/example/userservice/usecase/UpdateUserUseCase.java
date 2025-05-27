package com.example.userservice.usecase;

import com.example.userservice.domain.User;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.InvalidParameterException;

import java.util.Optional;

public class UpdateUserUseCase {
    private final UserRepository repository;

    public UpdateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> execute(Integer id, UpdateUserRequest request) {
        if (id == null || id <= 0) {
            throw new InvalidParameterException("Invalid user ID: ID must be a positive number");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new InvalidParameterException("Name is required");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidParameterException("Email is required");
        }

        // First get the existing user
        Optional<User> existingUserOpt = repository.getUserById(id);
        if (existingUserOpt.isEmpty()) {
            return Optional.empty();
        }

        User existingUser = existingUserOpt.get();
        
        // Update only the fields that are provided
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getGender() != null) {
            existingUser.setGender(request.getGender());
        }
        if (request.getNickname() != null) {
            existingUser.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            existingUser.setAvatar(request.getAvatar());
        }
        if (request.getBirthdate() != null) {
            existingUser.setBirthdate(request.getBirthdate());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }

        if (repository.updateUser(id, existingUser)) {
            return repository.getUserById(id);
        }
        return Optional.empty();
    }
} 