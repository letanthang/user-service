package com.example.userservice.service;

public class CustomClaim {
    private final String Email;
    private final String Role;
    public CustomClaim(String Email, String Role) {
        this.Email = Email;
        this.Role = Role;
    }
    public String getEmail() {
        return Email;
    }

    public String getRole() {
        return Role;
    }
}
