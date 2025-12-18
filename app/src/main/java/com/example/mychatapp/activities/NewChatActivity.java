package com.example.mychatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychatapp.R;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.util.Util;
import com.example.mychatapp.callbacks.ChatCallback;
import com.example.mychatapp.callbacks.UserLookupCallback;
import com.example.mychatapp.models.User;
import com.example.mychatapp.services.DatabaseService;
import com.google.android.material.textfield.TextInputLayout;

public class NewChatActivity extends AppCompatActivity {

    private TextInputLayout usernameInput;
    private Button createChatButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String receivedUserId = intent.getStringExtra("USER_ID");

        if (receivedUserId != null) {
            Log.d("NEW_CHAT_ACTIVITY", "Received User ID: " + receivedUserId);
        } else {
            Log.e("NEW_CHAT_ACTIVITY", "User ID was not passed!");
        }

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Util.redirectTo(this, MainActivity.class, false);
        });

        usernameInput = findViewById(R.id.usernameInput);
        createChatButton = findViewById(R.id.createChatButton);

        createChatButton.setOnClickListener(v -> {
            String username = usernameInput.getEditText().getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            FindUser(username);
        });
    }

    private void FindUser(String username) {

        DatabaseService.getInstance().findUserByUsername(
                username,
                new UserLookupCallback() {

                    @Override
                    public void onUserFound(String uid) {
                        String currentUid = AuthService.getInstance().getCurrentUserUid();

                        if (currentUid.equals(uid)) {
                            Toast.makeText(
                                    NewChatActivity.this,
                                    "You can’t start a chat with yourself",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        startChatWithUser(currentUid, uid);
                    }

                    @Override
                    public void onUserNotFound() {
                        Log.d("NEW_CHAT_ACTIVITY", "User NOT found");

                        Toast.makeText(
                                NewChatActivity.this,
                                "No user with that username",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("NEW_CHAT_ACTIVITY", "Error: " + errorMessage);

                        Toast.makeText(
                                NewChatActivity.this,
                                "Error: " + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void startChatWithUser(String currentUid, String uid) {

        DatabaseService.getInstance().createOrGetChat(
                currentUid,
                uid,
                new ChatCallback() {

                    @Override
                    public void onChatReady(String chatId) {
                        openChat(chatId);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Util.showMessage(NewChatActivity.this, "Error starting chat", errorMessage);
                    }
                }
        );
    }

    private void openChat(String chatId) {
        Log.d("NEW_CHAT_ACTIVITY", "Opening chat: " + chatId);

        Bundle extras = new Bundle();
        extras.putString("CHAT_ID", chatId);
        Util.redirectToWithData(this, ChatActivity.class, extras, false);
    }

}