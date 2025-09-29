package com.example.userservice.domain.usecase;

import com.example.userservice.domain.entity.User;
import com.example.userservice.dto.CreateUserRequest;

public interface CreateUserUseCase {
    User execute(CreateUserRequest request);
}
