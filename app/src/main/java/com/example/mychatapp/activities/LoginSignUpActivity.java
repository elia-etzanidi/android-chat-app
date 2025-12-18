package com.example.mychatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychatapp.R;
import com.example.mychatapp.util.Util;
import com.example.mychatapp.callbacks.DatabaseCallback;
import com.example.mychatapp.callbacks.LoginCallback;
import com.example.mychatapp.callbacks.SignUpCallback;
import com.example.mychatapp.models.User;
import com.example.mychatapp.services.AuthService;
import com.example.mychatapp.services.DatabaseService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpActivity extends AppCompatActivity {
    private boolean isLoginMode = true;
    private TextView tvTitle;
    private TextInputLayout tilUsername, tilEmail, tilPassword;
    private Button btnAction;
    private TextView tvToggleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tv_title);
        tilUsername = findViewById(R.id.til_username);
        btnAction = findViewById(R.id.btn_action);
        tvToggleMode = findViewById(R.id.tv_toggle_mode);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
    }

    public void toggleMode(View view) {
        if (isLoginMode) {
            // Switch to Sign Up mode
            tvTitle.setText("Sign Up");
            tilUsername.setVisibility(View.VISIBLE);
            btnAction.setText("Sign Up");
            tvToggleMode.setText("Already have an account? Login");
            isLoginMode = false;
        } else {
            // Switch to Login mode
            tvTitle.setText("Login");
            tilUsername.setVisibility(View.GONE);
            btnAction.setText("Login");
            tvToggleMode.setText("Don't have an account? Sign Up");
            isLoginMode = true;
        }
    }

    public void handleAuthentication(View view) {

        String email = tilEmail.getEditText().getText().toString().trim();
        String password = tilPassword.getEditText().getText().toString();
        String username = tilUsername.getEditText().getText().toString();

        // Check if we are in Sign Up mode and if inputs are valid
        if (!isLoginMode) {
            // We will add more front-end validation later, but check the basics now
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Util.showMessage(this, "Sign Up Error", "Please fill in all fields.");
                return;
            }
        }

        AuthService authService = AuthService.getInstance();

        if (isLoginMode) {
            // LOGIN
            authService.login(email, password, new LoginCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Util.redirectTo(LoginSignUpActivity.this, MainActivity.class, true);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Util.showMessage(LoginSignUpActivity.this, "Login Error", errorMessage);
                }
            });
        } else {
            // SIGN UP
            authService.signUp(email, password, username, new SignUpCallback() {
                @Override
                public void onSuccess(FirebaseUser user, String username, String email) {
                    String userId = user.getUid();
                    User newUser = new User(username, email);
                    DatabaseService databaseService = DatabaseService.getInstance();

                    databaseService.createUserWithUsername(userId, newUser, new DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            Util.redirectTo(LoginSignUpActivity.this, MainActivity.class, true);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Util.showMessage(
                                    LoginSignUpActivity.this,
                                    "Sign Up Error",
                                    errorMessage
                            );
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Util.showMessage(LoginSignUpActivity.this, "Sign Up Error", errorMessage);
                }
            });
        }
    }
}