package com.example.mychatapp.models;

public class Chat {
    private String userId;
    private String username;
    private String lastMessage;
    private String profileImageUrl;

    // Empty constructor needed for Firebase (if you use it later)
    public Chat() {}

    public Chat(String userId, String username, String lastMessage, String profileImageUrl) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
