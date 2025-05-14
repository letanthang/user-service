package com.example.userservice;

import com.example.userservice.config.Config;
import com.example.userservice.controller.UserController;
import com.example.userservice.repository.MySQLUserRepository;
import com.sun.net.httpserver.HttpServer;
import org.flywaydb.core.Flyway;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        Config.load();
        migrate();

        var repository = new MySQLUserRepository();
        var userController = new UserController(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(Config.PORT), 0);
        server.createContext("/user/add", userController::handleAddUser);
        server.createContext("/user/get", userController::handleGetUser);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + Config.PORT);
    }

    static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(Config.DB_URL, Config.DB_USER, Config.DB_PASS)
                .load();
        flyway.migrate();
    }
}
