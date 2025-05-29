package com.example.userservice.middleware;

import com.example.userservice.dto.ErrorResponse;
import com.example.userservice.service.JwtService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpResponseException;

public class AuthMiddleware {
    public static Handler authenticate = ctx -> {
        String authHeader = ctx.header("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new HttpResponseException(401, "Missing or invalid token");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        if (!JwtService.validateToken(token)) {
            throw new HttpResponseException(401, "Invalid or expired token");
        }

        // Add the authenticated email to the context for use in handlers
        ctx.attribute("authenticatedEmail", JwtService.getEmailFromToken(token));
    };
} 