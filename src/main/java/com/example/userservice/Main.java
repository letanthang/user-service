package com.example.userservice;

import com.example.userservice.config.Config;
import com.example.userservice.config.PersistenceConfig;
import com.example.userservice.controller.UserController;
import com.example.userservice.repository.MySQLUserRepository;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import jakarta.persistence.EntityManagerFactory;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

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

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                // Allow all origins
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
        });

        // Define routes
        app.post("/users", ctx -> {
            String name = ctx.formParam("name");
            if (name == null || name.isEmpty()) {
                ctx.status(400).result("Name is required");
                return;
            }
            userController.handleAddUser(name);
            ctx.status(201).result("User added successfully");
        });

        app.get("/users/{id}", ctx -> {
            String id = ctx.pathParam("id");
            userController.handleGetUser(id).ifPresentOrElse(
                user -> ctx.json(user),
                () -> ctx.status(404).result("User not found")
            );
        });

        app.get("/users", ctx -> {
            ctx.json(userController.handleListUsers());
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
}
