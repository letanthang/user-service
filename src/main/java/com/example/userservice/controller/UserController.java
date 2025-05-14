package com.example.userservice.controller;

import com.example.userservice.repository.UserRepository;
import com.example.userservice.usecase.AddUserUseCase;
import com.example.userservice.usecase.GetUserUseCase;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UserController {
    private final AddUserUseCase addUser;
    private final GetUserUseCase getUser;

    public UserController(UserRepository repository) {
        this.addUser = new AddUserUseCase(repository);
        this.getUser = new GetUserUseCase(repository);
    }

    public void handleAddUser(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        var query = parseQuery(exchange.getRequestURI().getQuery());
        addUser.execute(query.get("id"), query.get("name"));

        respond(exchange, 200, "User added");
    }

    public void handleGetUser(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        var query = parseQuery(exchange.getRequestURI().getQuery());
        var user = getUser.execute(query.get("id"));

        if (user.isPresent()) {
            respond(exchange, 200, "User: " + user.get().getName());
        } else {
            respond(exchange, 404, "User not found");
        }
    }

    private void respond(HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

    private Map<String, String> parseQuery(String query) {
        return java.util.Arrays.stream(query.split("&"))
                .map(kv -> kv.split("="))
                .collect(java.util.stream.Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }
}
