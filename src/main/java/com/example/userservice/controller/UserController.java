package com.example.userservice.controller;

import com.example.userservice.domain.User;
import com.example.userservice.repository.UserRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    public void handleAddUser(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String name = exchange.getRequestHeaders().getFirst("name");
        if (name == null || name.isEmpty()) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        User user = new User();
        user.setName(name);
        repository.addUser(user);

        String response = "User added with ID: " + user.getId();
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void handleGetUser(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String id = exchange.getRequestHeaders().getFirst("id");
        if (id == null || id.isEmpty()) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        Optional<User> userOpt = repository.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String response = "User found: " + user.getName();
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, -1); // Not Found
        }
    }

    public void handleListUsers(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        List<User> users = repository.getAllUsers();
        String response = users.stream()
                .map(user -> String.format("ID: %s, Name: %s", user.getId(), user.getName()))
                .collect(Collectors.joining("\n"));

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
