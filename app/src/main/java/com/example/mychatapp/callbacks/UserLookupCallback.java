package com.example.mychatapp.callbacks;

import com.example.mychatapp.models.User;

public interface UserLookupCallback {
    void onUserFound(String uid);
    void onUserNotFound();
    void onFailure(String errorMessage);
}
