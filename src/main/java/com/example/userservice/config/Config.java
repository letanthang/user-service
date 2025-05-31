package com.example.userservice.config;

public class Config {
    public static String DB_URL;
    public static String DB_USER;
    public static String DB_PASS;
    public static int PORT;

    public static void load() {
        DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/userdb");
        DB_USER = System.getenv().getOrDefault("DB_USER", "root");
        DB_PASS = System.getenv().getOrDefault("DB_PASS", "");
        PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }
}
