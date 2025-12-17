package com.example.mychatapp;

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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

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
            Log.d("NewChatActivity", "Received User ID: " + receivedUserId);
        } else {
            Log.e("NewChatActivity", "User ID was not passed!");
        }

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            finish();
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
                    public void onUserFound(User user, String uid) {
                        Log.d("USER_LOOKUP", "Found user:");
                        Log.d("USER_LOOKUP", "Username: " + user.getUsername());
                        Log.d("USER_LOOKUP", "UID: " + uid);

                        String currentUid = FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid();

                        if (currentUid.equals(uid)) {
                            Toast.makeText(
                                    NewChatActivity.this,
                                    "You can’t start a chat with yourself",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        Log.d("USER_LOOKUP", "Starting chat with " + user.getUsername());

                        String chatId = Util.generateChatId(currentUid, uid);
                        Log.d("CHAT", "Chat ID: " + chatId);
                    }

                    @Override
                    public void onUserNotFound() {
                        Log.d("USER_LOOKUP", "User NOT found");

                        Toast.makeText(
                                NewChatActivity.this,
                                "No user with that username",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("USER_LOOKUP", "Error: " + errorMessage);

                        Toast.makeText(
                                NewChatActivity.this,
                                "Error: " + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}