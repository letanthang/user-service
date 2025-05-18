package com.example.userservice.repository;

import com.example.userservice.domain.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUserById(String id);
    List<User> getAllUsers();
}
