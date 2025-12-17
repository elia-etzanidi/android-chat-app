package com.example.mychatapp;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String email;
    private Map<String, Boolean> chats = new HashMap<>();

    // Required empty constructor for Firebase
    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getChats() {
        return chats;
    }

    public void setChats(Map<String, Boolean> chats) {
        this.chats = chats;
    }

}
