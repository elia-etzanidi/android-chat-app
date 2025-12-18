package com.example.mychatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.example.mychatapp.callbacks.MessagesCallback;
import com.example.mychatapp.callbacks.UsernameLookupCallback;
import com.example.mychatapp.models.Message;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.services.DatabaseService;
import com.example.mychatapp.util.Util;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    EditText messageEditText;
    private String currentChatId;
    TextView textUsername;
    private RecyclerView recyclerMessages;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private String currentUsername;

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
        textUsername = findViewById(R.id.textUsername);

        displayUsername(currentChatId);

        ImageButton backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Util.redirectTo(this, MainActivity.class, false);
        });

        fetchCurrentUsername();
        recyclerMessages = findViewById(R.id.chatRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // 👈 important for chat
        recyclerMessages.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(messageList);
        recyclerMessages.setAdapter(adapter);

        listenForMessages(currentChatId);


    }

    private void handleSendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        String currentUid = AuthService.getInstance().getCurrentUserUid();
        long timestamp = System.currentTimeMillis();

        // Create message object
        Message message = new Message(
                currentUid,
                currentUsername,
                text,
                timestamp
        );

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

    public void displayUsername(String chatId) {
        String currentUid = AuthService.getInstance().getCurrentUserUid();
        String[] ids = chatId.split("_");
        String otherUserId = ids[0].equals(currentUid) ? ids[1] : ids[0];

        DatabaseService.getInstance().findUsernameByUserId(otherUserId, new UsernameLookupCallback() {
            @Override
            public void onUsernameFound(String username) {
                textUsername.setText(username);
            }

            @Override
            public void onUsernameNotFound() {
                Util.showMessage(ChatActivity.this, "Error: ", "User not found");
            }

            @Override
            public void onFailure(String errorMessage) {
                Util.showMessage(ChatActivity.this, "Error: ", errorMessage);
            }
        });
    }

    private void listenForMessages(String chatId) {
        DatabaseService.getInstance().listenToMessages(chatId, new MessagesCallback() {
            @Override
            public void onMessageAdded(Message message) {
                Log.d("CHAT_LOG", "Received message: "
                        + ", Username=" + message.getSenderUsername()
                        + ", Message=" + message.getMessage()
                        + ", Timestamp=" + message.getTimestamp());
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerMessages.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onFailure(String errorMessage) {
                Util.showMessage(ChatActivity.this, "Error: ", errorMessage);
            }
        });
    }

    private void fetchCurrentUsername() {
        String uid = AuthService.getInstance().getCurrentUserUid();

        DatabaseService.getInstance().findUsernameByUserId(uid, new UsernameLookupCallback() {
            @Override
            public void onUsernameFound(String username) {
                currentUsername = username;
            }

            @Override public void onUsernameNotFound() {}
            @Override public void onFailure(String errorMessage) {}
        });
    }

}