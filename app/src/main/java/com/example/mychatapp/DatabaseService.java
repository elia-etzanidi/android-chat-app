package com.example.mychatapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseService {
    private static DatabaseService instance;
    private final DatabaseReference usersRef;
    private final DatabaseReference chatsRef;

    public DatabaseService() {
        Log.d("DB_TRACE", "DatabaseService constructor started.");
        // Initialize the main reference to the "users" node
        usersRef = FirebaseDatabase
                .getInstance(BuildConfig.DATABASE_URL)
                .getReference("users");

        chatsRef = FirebaseDatabase
                .getInstance(BuildConfig.DATABASE_URL)
                .getReference("chats");
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

    public void findUserByUsername(String username, UserLookupCallback callback) {
        usersRef.orderByChild("username")
                .equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            callback.onUserNotFound();
                            return;
                        }

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            User user = userSnapshot.getValue(User.class);
                            String uid = userSnapshot.getKey(); // 🔑 UID here

                            callback.onUserFound(user, uid);
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
}
