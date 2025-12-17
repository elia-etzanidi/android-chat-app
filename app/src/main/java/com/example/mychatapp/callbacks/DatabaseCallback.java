package com.example.mychatapp.callbacks;

public interface DatabaseCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
