package com.example.mychatapp.callbacks;

public interface UsernameLookupCallback {
    void onUsernameFound(String username);
    void onUsernameNotFound();
    void onFailure(String errorMessage);
}
