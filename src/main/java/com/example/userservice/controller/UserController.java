package com.example.userservice.controller;

import com.example.userservice.domain.User;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse handleAddUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setNickname(request.getNickname());
        user.setAvatar(request.getAvatar());
        user.setBirthdate(request.getBirthdate());
        
        repository.addUser(user);
        return mapToResponse(user);
    }

    public Optional<UserResponse> handleGetUser(String id) {
        return repository.getUserById(id).map(this::mapToResponse);
    }

    public List<UserResponse> handleListUsers() {
        return repository.getAllUsers().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setGender(user.getGender());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setBirthdate(user.getBirthdate());
        return response;
    }
}
