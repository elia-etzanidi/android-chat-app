package com.example.mychatapp.services;

import android.util.Log;

import com.example.mychatapp.callbacks.LoginCallback;
import com.example.mychatapp.callbacks.SignUpCallback;
import com.google.firebase.auth.FirebaseAuth;

public class AuthService {
    private static AuthService instance;
    private final FirebaseAuth auth;

    // constructor
    public AuthService() {
        auth = FirebaseAuth.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            // Use synchronization to make it thread-safe for initial creation
            synchronized (AuthService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }

    public void signUp(String email, String password, String username, SignUpCallback callback) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            Log.e("AUTH_TRACE", "3a. AuthService validation failed.");
            callback.onFailure("Email and password fields cannot be empty.");
            return; // Stop execution if validation fails
        }

        Log.d("AUTH_TRACE", "3b. AuthService calling Firebase createUserWithEmailAndPassword.");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sihg Up successful
                        if (task.getResult() != null && task.getResult().getUser() != null) {
                            Log.d("AUTH_TRACE", "3c. Firebase task succeeded.");
                            callback.onSuccess(task.getResult().getUser(), username, email);
                        } else {
                            // Sign Up failed
                            Log.e("AUTH_TRACE", "3c. Firebase task FAILED. Exception: " + task.getException());
                            callback.onFailure("Sign up succeeded but user data is missing.");
                        }
                    } else {
                        // Failure: Safely handle the exception
                        String errorMessage = "Sign Up Failed.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getLocalizedMessage();
                        }
                        callback.onFailure(errorMessage);
                    }
                });
    }

    public void login(String email, String password, LoginCallback callback) {
        // Input Validation
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            callback.onFailure("Please enter both email and password for login.");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        // Login successful
                        callback.onSuccess(task.getResult().getUser());
                    } else {
                        // Login failed
                        String errorMessage = task.getException() != null ? task.getException().getLocalizedMessage() : "Login failed.";
                        callback.onFailure(errorMessage);
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public String getCurrentUserUid() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
