package com.example.userservice;

import com.example.userservice.config.Config;
import com.example.userservice.config.PersistenceConfig;
import com.example.userservice.controller.UserController;
import com.example.userservice.repository.MySQLUserRepository;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManagerFactory;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    private static EntityManagerFactory emf;

    public static void main(String[] args) throws Exception {
        Config.load();
        migrate();
        
        emf = PersistenceConfig.createEntityManagerFactory();
        var repository = new MySQLUserRepository(emf);
        var userController = new UserController(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(Config.PORT), 0);
        server.createContext("/users", userController::handleAddUser);
        server.createContext("/users", userController::handleGetUser);
        server.createContext("/users", userController::handleListUsers);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + Config.PORT);

        // Add shutdown hook to close EntityManagerFactory
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emf != null) {
                emf.close();
            }
        }));
    }

    static void migrate() {
        try (Connection connection = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Migration failed", e);
        }
    }
}
