package com.example.userservice.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PersistenceConfig {
    public static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", Config.DB_URL);
        properties.put("javax.persistence.jdbc.user", Config.DB_USER);
        properties.put("javax.persistence.jdbc.password", Config.DB_PASS);
        
        return Persistence.createEntityManagerFactory("userPU", properties);
    }
} 