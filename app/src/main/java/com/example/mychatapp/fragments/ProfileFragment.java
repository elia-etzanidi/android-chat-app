package com.example.mychatapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mychatapp.R;
import com.example.mychatapp.activities.LoginSignUpActivity;
import com.example.mychatapp.callbacks.UserProfileCallback;
import com.example.mychatapp.models.User;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.services.DatabaseService;
import com.example.mychatapp.util.Util;

public class ProfileFragment extends Fragment {

    private TextView tvDisUsername, tvUsername, tvEmail;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvDisUsername = view.findViewById(R.id.tv_display_username);
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);

        loadProfile();

        logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            AuthService.getInstance().logout();

            Util.redirectTo(getActivity(), LoginSignUpActivity.class, true);
        });

        return view;
    }

    private void loadProfile() {
        String uid = AuthService.getInstance().getCurrentUserUid();

        if (uid != null) {
            DatabaseService.getInstance().getUserProfile(uid, new UserProfileCallback() {
                @Override
                public void onUserLoaded(User user) {
                    tvDisUsername.setText(user.getUsername());
                    tvUsername.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                }

                @Override
                public void onError(String errorMessage) {
                    Util.showMessage(getContext(), "Couldn't load profile", errorMessage);
                }
            });
        } else {
            Util.redirectTo(getActivity(), LoginSignUpActivity.class, true);
        }
    }
}