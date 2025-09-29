package com.example.userservice.infrastructure.controller;

import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.usecase.impl.*;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateMeRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final GetMeUseCase getMeUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateMeUseCase updateMeUseCase;

    public UserController(GetUserUseCase getUserUseCase,
                          GetMeUseCase getMeUseCase,
                          CreateUserUseCase createUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          UpdateMeUseCase updateMeUseCase) {
        this.getUserUseCase = getUserUseCase;
        this.getMeUseCase = getMeUseCase;
        this.createUserUseCase = createUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateMeUseCase = updateMeUseCase;

    }

    public UserResponse handleAddUser(CreateUserRequest request) {
        User user = createUserUseCase.execute(request);
        return mapToResponse(user);
    }

    public Optional<UserResponse> handleGetUser(Integer id) {
        return getUserUseCase.execute(id).map(this::mapToResponse);
    }

    public Optional<UserResponse> handleGetMe(String email) {
        return getMeUseCase.execute(email).map(this::mapToResponse);
    }

    public List<UserResponse> handleListUsers() {
        return listUsersUseCase.execute().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean handleDeleteUser(Integer id) {
        return deleteUserUseCase.execute(id);
    }

    public Optional<UserResponse> handleUpdateUser(Integer id, UpdateUserRequest request) {
        return updateUserUseCase.execute(id, request).map(this::mapToResponse);
    }

    public Optional<UserResponse> handleUpdateMe(String email, UpdateMeRequest request) {
        return updateMeUseCase.execute(email, request).map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setGender(user.getGender());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setBirthdate(user.getBirthdate());
        response.setEmail(user.getEmail());
        return response;
    }
}
