package com.example.mychatapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mychatapp.R;
import com.example.mychatapp.adapters.ChatAdapter;
import com.example.mychatapp.callbacks.ChatListCallback;
import com.example.mychatapp.callbacks.UsernameLookupCallback;
import com.example.mychatapp.models.Chat;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> chatList;
    private String currentUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_chats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), chatList);
        recyclerView.setAdapter(adapter);

        currentUid = AuthService.getInstance().getCurrentUserUid();

        setupChatListener();

        return view;
    }

    private void setupChatListener() {
        DatabaseService.getInstance().listenToUserChats(currentUid, new ChatListCallback() {
            @Override
            public void onChatAdded(Chat chat) {
                // To show the username instead of ID
                DatabaseService.getInstance().findUsernameByUserId(chat.getUserId(), new UsernameLookupCallback() {
                    @Override
                    public void onUsernameFound(String username) {
                        chat.setUsername(username);
                        chatList.add(chat);
                        adapter.notifyItemInserted(chatList.size() - 1);
                    }

                    @Override
                    public void onUsernameNotFound() {
                        chatList.add(chat);
                        adapter.notifyItemInserted(chatList.size() - 1);
                    }

                    @Override
                    public void onFailure(String error) {
                        chatList.add(chat);
                        adapter.notifyItemInserted(chatList.size() - 1);
                    }
                });
            }

            @Override
            public void onChatUpdated(Chat updatedChat) {
                for (int i = 0; i < chatList.size(); i++) {
                    if (chatList.get(i).getUserId().equals(updatedChat.getUserId())) {
                        // Keep the username we already fetched previously
                        updatedChat.setUsername(chatList.get(i).getUsername());

                        chatList.set(i, updatedChat);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CHATS_FRAGMENT", "Error loading chats: " + error);
            }
        });
    }
}