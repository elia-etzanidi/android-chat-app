package com.example.mychatapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.R;
import com.example.mychatapp.activities.ChatActivity;
import com.example.mychatapp.models.Chat;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.util.Util;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> chatList;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.username.setText(chat.getUsername());
        holder.lastMessage.setText(chat.getLastMessage());

        // clicking on a chat
        holder.itemView.setOnClickListener(v -> {
            String currentUid = AuthService.getInstance().getCurrentUserUid();
            String otherUserId = chat.getUserId();

            String chatId = Util.generateChatId(currentUid, otherUserId);

            Bundle extras = new Bundle();
            extras.putString("CHAT_ID", chatId);
            Util.redirectToWithData(context, ChatActivity.class, extras, false);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage;
        ImageView profileImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_text);
            lastMessage = itemView.findViewById(R.id.last_message_text);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
