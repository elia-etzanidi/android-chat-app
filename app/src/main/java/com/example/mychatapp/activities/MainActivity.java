package com.example.mychatapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychatapp.fragments.ChatsFragment;
import com.example.mychatapp.fragments.ProfileFragment;
import com.example.mychatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ChatsFragment chatsFragment;
    ProfileFragment profileFragment;
    private String userEmail;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            userId = currentUser.getUid();
            userEmail = currentUser.getEmail();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatsFragment = new ChatsFragment();
        profileFragment = ProfileFragment.newInstance(userEmail);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, chatsFragment)
                    .commit();
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.chats) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chatsFragment).commit();
                return true;
            } else if (id == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                return true;
            } else if (id == R.id.new_chat) {
                Intent intent = new Intent(this, NewChatActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                // so the add icon doesn't get highlighted
                return false;
            }
            return false;
        });
    }
}