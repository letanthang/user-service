package com.example.userservice.domain.repository;

import com.example.userservice.domain.entity.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    boolean deleteUser(Integer id);
    void updateUser(User user);
}
