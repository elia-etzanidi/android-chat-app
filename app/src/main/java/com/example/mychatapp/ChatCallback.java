package com.example.mychatapp;

public interface ChatCallback {
    void onChatReady(String chatId);
    void onError(String errorMessage);
}
