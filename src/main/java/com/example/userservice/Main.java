package com.example.userservice;

import com.example.userservice.config.Config;
import com.example.userservice.config.PersistenceConfig;
import com.example.userservice.controller.UserController;
import com.example.userservice.repository.MySQLUserRepository;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.ErrorResponse;
import com.example.userservice.exception.InvalidParameterException;
import com.example.userservice.usecase.*;
import com.example.userservice.middleware.AuthMiddleware;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.exceptions.MySQLQueryInterruptedException;

import io.javalin.json.JavalinJackson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;

public class Main {
    private static EntityManagerFactory emf;

    public static void main(String[] args) throws Exception {
        Config.load();
        migrate();
        
        emf = PersistenceConfig.createEntityManagerFactory();
        var repository = new MySQLUserRepository(emf);
        
        var createUserUseCase = new CreateUserUseCase(repository);
        var getUserUseCase = new GetUserUseCase(repository);
        var listUsersUseCase = new ListUsersUseCase(repository);
        var deleteUserUseCase = new DeleteUserUseCase(repository);
        var updateUserUseCase = new UpdateUserUseCase(repository);
        
        var userController = new UserController(
            createUserUseCase,
            getUserUseCase,
            listUsersUseCase,
            deleteUserUseCase,
            updateUserUseCase
        );

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                // Allow all origins
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
            config.jsonMapper(new JavalinJackson(JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build(), true));
        });

        // Add custom error handler for HttpResponseException
        app.exception(HttpResponseException.class, (e, ctx) -> {
            ctx.status(e.getStatus());
            ctx.json(new ErrorResponse(getStatusMessage(e.getStatus()), e.getMessage()));
        });

        app.exception(ConstraintViolationException.class, (e, ctx) -> {
            Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                ctx.status(HttpStatus.CONFLICT);
                ctx.json(new ErrorResponse("CONFLICT_RESOURCE", e.getMessage()));
            } else {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
                ctx.json(new ErrorResponse("SERVER_ERRROR", e.getMessage()));
            }
        });

        
        // Apply authentication middleware
        app.before("/users/*", AuthMiddleware.authenticate);
        app.before("/users", AuthMiddleware.authenticate);
        
        // Define routes
        app.post("/users", ctx -> {
                CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);
                
                if (request.getName() == null || request.getName().isEmpty()) {
                    ctx.status(400).json(new ErrorResponse("INVALID_REQUEST", "Name is required"));
                    return;
                }

                if (request.getEmail() == null || request.getEmail().isEmpty()) {
                    ctx.status(400).json(new ErrorResponse("INVALID_REQUEST", "Email is required"));
                    return;
                }

                UserResponse response = userController.handleAddUser(request);
                ctx.status(201).json(response);
        });

        app.get("/users/{id}", ctx -> {
            try {
                Integer id = Integer.parseInt(ctx.pathParam("id"));
                userController.handleGetUser(id).ifPresentOrElse(
                    user -> ctx.json(user),
                    () -> ctx.status(404).json(new ErrorResponse("USER_NOT_FOUND", "User not found"))
                );
            } catch (NumberFormatException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_ID_FORMAT", "Invalid ID format: ID must be a number"));
            } catch (InvalidParameterException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_PARAMETER", e.getMessage()));
            }
        });

        app.get("/users", ctx -> {
            try {
                ctx.json(userController.handleListUsers());
            } catch (Exception e) {
                ctx.status(500).json(new ErrorResponse("INTERNAL_ERROR", "Failed to retrieve users: " + e.getMessage()));
            }
        });

        app.delete("/users/{id}", ctx -> {
            try {
                Integer id = Integer.parseInt(ctx.pathParam("id"));
                if (userController.handleDeleteUser(id)) {
                    ctx.status(204).result("");
                } else {
                    ctx.status(404).json(new ErrorResponse("USER_NOT_FOUND", "User not found"));
                }
            } catch (NumberFormatException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_ID_FORMAT", "Invalid ID format: ID must be a number"));
            } catch (InvalidParameterException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_PARAMETER", e.getMessage()));
            }
        });

        app.put("/users/{id}", ctx -> {
            try {
                Integer id = Integer.parseInt(ctx.pathParam("id"));
                UpdateUserRequest request = ctx.bodyAsClass(UpdateUserRequest.class);
                
                if (request.getName() == null || request.getName().isEmpty()) {
                    ctx.status(400).json(new ErrorResponse("INVALID_REQUEST", "Name is required"));
                    return;
                }

                if (request.getEmail() == null || request.getEmail().isEmpty()) {
                    ctx.status(400).json(new ErrorResponse("INVALID_REQUEST", "Email is required"));
                    return;
                }

                userController.handleUpdateUser(id, request).ifPresentOrElse(
                    user -> ctx.json(user),
                    () -> ctx.status(404).json(new ErrorResponse("USER_NOT_FOUND", "User not found"))
                );
            } catch (NumberFormatException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_ID_FORMAT", "Invalid ID format: ID must be a number"));
            } catch (InvalidParameterException e) {
                ctx.status(400).json(new ErrorResponse("INVALID_PARAMETER", e.getMessage()));
            } catch (Exception e) {
                ctx.status(400).json(new ErrorResponse("INVALID_REQUEST", "Invalid request payload: " + e.getMessage()));
            }
        });

        // Start the server
        app.start(Config.PORT);
        System.out.println("Server started on port " + Config.PORT);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emf != null) {
                emf.close();
            }
            app.stop();
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

    public static String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 409 -> "CONFLICT_RESOURCE";
            case 500 -> "INTERNAL_SERVER_ERROR";
            default -> "UNKNOWN";
        };
    }
}
