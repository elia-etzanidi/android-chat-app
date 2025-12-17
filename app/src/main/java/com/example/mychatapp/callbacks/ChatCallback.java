package com.example.mychatapp.callbacks;

public interface ChatCallback {
    void onChatReady(String chatId);
    void onError(String errorMessage);
}
