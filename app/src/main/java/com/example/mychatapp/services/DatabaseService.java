package com.example.mychatapp.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mychatapp.BuildConfig;
import com.example.mychatapp.callbacks.ChatListCallback;
import com.example.mychatapp.callbacks.MessagesCallback;
import com.example.mychatapp.callbacks.UserProfileCallback;
import com.example.mychatapp.callbacks.UsernameLookupCallback;
import com.example.mychatapp.models.Chat;
import com.example.mychatapp.models.Message;
import com.example.mychatapp.util.Util;
import com.example.mychatapp.callbacks.ChatCallback;
import com.example.mychatapp.callbacks.DatabaseCallback;
import com.example.mychatapp.callbacks.UserLookupCallback;
import com.example.mychatapp.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    private static DatabaseService instance;
    private final DatabaseReference usersRef;
    private final DatabaseReference chatsRef;
    private final DatabaseReference usernamesRef;

    public DatabaseService() {
        Log.d("DB_TRACE", "DatabaseService constructor started.");
        // Initialize the main reference to the "users" node
        usersRef = FirebaseDatabase
                .getInstance(BuildConfig.DATABASE_URL)
                .getReference("users");

        chatsRef = FirebaseDatabase
                .getInstance(BuildConfig.DATABASE_URL)
                .getReference("chats");

        usernamesRef = FirebaseDatabase
                .getInstance(BuildConfig.DATABASE_URL)
                .getReference("usernames");
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

    public void createUserWithUsername(String userId, User user, DatabaseCallback callback) {
        String username = user.getUsername();

        usernamesRef.child(username).setValue(userId)
                .addOnSuccessListener(aVoid -> {
                    usersRef.child(userId).setValue(user)
                            .addOnSuccessListener(v -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Username already exists");
                });
    }

    public void findUserByUsername(String username, UserLookupCallback callback) {
        usernamesRef.child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            callback.onUserNotFound();
                            return;
                        }

                        String uid = snapshot.getValue(String.class);
                        callback.onUserFound(uid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getUserProfile(String userId, UserProfileCallback callback) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Firebase maps the JSON directly to User object
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onUserLoaded(user);
                    } else {
                        callback.onError("Failed to parse user data.");
                    }
                } else {
                    callback.onError("User does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void findUsernameByUserId(String userId, UsernameLookupCallback callback) {
        usersRef.child(userId).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String username = snapshot.getValue(String.class);
                            callback.onUsernameFound(username);
                        } else {
                            callback.onUsernameNotFound();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void createOrGetChat(String uid1, String uid2, ChatCallback callback) {

        if (uid1.equals(uid2)) {
            callback.onError("Cannot create chat with yourself");
            return;
        }

        String chatId = Util.generateChatId(uid1, uid2);

        chatsRef.child(chatId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            callback.onChatReady(chatId);
                            return;
                        }

                        Map<String, Object> chatData = new HashMap<>();

                        Map<String, Object> participants = new HashMap<>();
                        participants.put(uid1, true);
                        participants.put(uid2, true);

                        chatData.put("participants", participants);
                        chatData.put("createdAt", System.currentTimeMillis());

                        chatsRef.child(chatId)
                                .setValue(chatData)
                                .addOnSuccessListener(aVoid -> callback.onChatReady(chatId))
                                .addOnFailureListener(e ->
                                        callback.onError(e.getMessage())
                                );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void sendMessage(String chatId, Message message, DatabaseCallback callback) {
        // Reference: chats -> chatId -> messages -> [unique_id]
        chatsRef.child(chatId).child("messages").push().setValue(message)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void listenToMessages(String chatId, MessagesCallback callback) {
        DatabaseReference messagesRef = chatsRef.child(chatId).child("messages");

        messagesRef.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d("CHAT_LOG", "Raw snapshot: " + snapshot.getValue());
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    callback.onMessageAdded(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void listenToUserChats(String currentUserId, ChatListCallback callback) {
        Log.d("CHAT_DEBUG", "Starting listener for UID: " + currentUserId);
        // Query for chats where the current user is a participant
        Query userChatsQuery = chatsRef.orderByChild("participants/" + currentUserId).equalTo(true);

        userChatsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d("CHAT_DEBUG", "Found a chat! ID: " + snapshot.getKey());
                Log.d("CHAT_DEBUG", "Full Snapshot Content: " + snapshot.getValue());
                Chat model = parseChatSnapshot(snapshot, currentUserId);
                if (model != null) {
                    callback.onChatAdded(model);
                } else {
                    Log.e("CHAT_DEBUG", "Parsing failed for chat: " + snapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d("CHAT_DEBUG", "Chat updated: " + snapshot.getKey());
                Chat model = parseChatSnapshot(snapshot, currentUserId);
                if (model != null) callback.onChatUpdated(model);
            }

            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CHAT_DEBUG", "Database Error: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
    }

    // Helper method to extract data from the snapshot
    private Chat parseChatSnapshot(DataSnapshot snapshot, String currentUserId) {
        String chatId = snapshot.getKey();
        String lastMsg = "No messages";

        // Get last message text
        DataSnapshot messagesSnapshot = snapshot.child("messages");
        if (messagesSnapshot.exists()) {
            DataSnapshot lastChild = null;
            for (DataSnapshot child : messagesSnapshot.getChildren()) {
                lastChild = child;
            }
            if (lastChild != null) {
                lastMsg = lastChild.child("message").getValue(String.class);
            }
        }

        // Identify the other user from the Chat ID (UID1_UID2)
        String[] uids = chatId.split("_");
        String otherUserId = uids[0].equals(currentUserId) ? uids[1] : uids[0];

        return new Chat(otherUserId, "User: " + otherUserId, lastMsg, "");
    }
}
