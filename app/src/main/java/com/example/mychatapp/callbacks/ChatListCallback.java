package com.example.mychatapp.callbacks;

import com.example.mychatapp.models.Chat;

public interface ChatListCallback {
    void onChatAdded(Chat chat);
    void onChatUpdated(Chat chat);
    void onError(String error);
}