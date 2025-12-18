package com.example.mychatapp.callbacks;

import com.example.mychatapp.models.Message;

public interface MessagesCallback {
    void onMessageAdded(Message message);

    void onFailure(String errorMessage);
}
