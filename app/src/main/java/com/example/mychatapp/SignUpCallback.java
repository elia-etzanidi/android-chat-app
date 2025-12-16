package com.example.mychatapp;

import com.google.firebase.auth.FirebaseUser;

public interface SignUpCallback {
    void onSuccess(FirebaseUser user, String username, String email);

    void onFailure(String errorMessage);
}
