package com.example.userservice.domain.usecase.impl;

import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.exception.InvalidParameterException;
import com.example.userservice.domain.repository.UserRepository;

import java.util.Optional;

public class GetMeUseCase implements com.example.userservice.domain.usecase.GetMeUseCase {
    private final UserRepository repository;

    public GetMeUseCase(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> execute(String email) {

        return repository.getUserByEmail(email);
    }
}
