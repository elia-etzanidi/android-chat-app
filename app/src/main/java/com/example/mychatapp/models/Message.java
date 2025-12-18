package com.example.mychatapp.models;

public class Message {
    private String senderId;
    private String senderUsername;
    private String message;
    private long timestamp;

    // Required public no-argument constructor for Firebase
    public Message() {
    }

    public Message(String senderId, String senderUsername, String text, long timestamp) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.message = text;
        this.timestamp = timestamp;
    }

    // Getters and Setters (required by Firebase)
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String text) {
        this.message = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}
