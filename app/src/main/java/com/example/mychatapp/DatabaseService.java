package com.example.mychatapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseService {
    private static DatabaseService instance;
    private final DatabaseReference usersRef;

    public DatabaseService() {
        Log.d("DB_TRACE", "DatabaseService constructor started.");
        // Initialize the main reference to the "users" node
        usersRef = FirebaseDatabase.getInstance(BuildConfig.DATABASE_URL).getReference("users");
        Log.d("DB_TRACE", "DatabaseService constructor finished successfully.");
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            synchronized (DatabaseService.class) {
                if (instance == null) {
                    instance = new DatabaseService();
                }
            }
        }
        return instance;
    }

    // Method to save the User object after sign-up
    public void saveNewUser(String userId, User user, DatabaseCallback callback) {
        usersRef.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Inform the calling component (Activity) that it was successful
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Inform the calling component of the failure
                    callback.onFailure(e.getMessage());
                });
    }
}
