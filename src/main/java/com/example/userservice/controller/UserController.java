package com.example.userservice.controller;

import com.example.userservice.domain.User;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.usecase.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                         GetUserUseCase getUserUseCase,
                         ListUsersUseCase listUsersUseCase,
                         DeleteUserUseCase deleteUserUseCase,
                         UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    public UserResponse handleAddUser(CreateUserRequest request) {
        User user = createUserUseCase.execute(request);
        return mapToResponse(user);
    }

    public Optional<UserResponse> handleGetUser(Integer id) {
        return getUserUseCase.execute(id).map(this::mapToResponse);
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
