package com.example.mychatapp;

import com.google.firebase.auth.FirebaseUser;
import com.example.mychatapp.User;

public interface UserLookupCallback {
    void onUserFound(User user, String uid);
    void onUserNotFound();
    void onFailure(String errorMessage); // For database errors
}
