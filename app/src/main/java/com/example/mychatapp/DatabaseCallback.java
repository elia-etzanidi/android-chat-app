package com.example.mychatapp;

public interface DatabaseCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
