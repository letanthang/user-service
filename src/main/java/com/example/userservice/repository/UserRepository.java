package com.example.userservice.repository;

import com.example.userservice.domain.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUserById(Integer id);
    List<User> getAllUsers();
    boolean deleteUser(Integer id);
    boolean updateUser(Integer id, User user);
}
