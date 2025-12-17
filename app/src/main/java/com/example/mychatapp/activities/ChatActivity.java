package com.example.mychatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.R;
import com.example.mychatapp.adapters.MessageAdapter;
import com.example.mychatapp.callbacks.DatabaseCallback;
import com.example.mychatapp.models.Message;
import com.example.mychatapp.services.DatabaseService;
import com.example.mychatapp.util.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText messageEditText;
    private String currentChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        currentChatId = intent.getStringExtra("CHAT_ID");
        messageEditText = findViewById(R.id.editTextMessage);
    }

    private void handleSendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        String currentUid = FirebaseAuth.getInstance().
                getCurrentUser()
                .getUid();
        long timestamp = System.currentTimeMillis();

        // Create message object
        Message message = new Message(currentUid, text, timestamp);

        // currentChatId should be the ID you received from createOrGetChat
        DatabaseService.getInstance().sendMessage(currentChatId, message, new DatabaseCallback() {
            @Override
            public void onSuccess() {
                messageEditText.setText("");
            }

            @Override
            public void onFailure(String errorMessage) {
                Util.showMessage(ChatActivity.this, "Failed to send message.", errorMessage);
            }
        });
    }

    public void onSendButtonClick(View view){
        handleSendMessage();
    }


}