package com.example.iskeduler;

public class User {
    private String name;
    private String email;
    private String username;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String username) {
        this.name = name;
        this.email = email;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
