package com.example.mychatapp.callbacks;

import com.example.mychatapp.models.User;

public interface UserProfileCallback {
    void onUserLoaded(User user);
    void onError(String errorMessage);
}