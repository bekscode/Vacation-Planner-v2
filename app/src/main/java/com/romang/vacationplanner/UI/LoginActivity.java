package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.romang.vacationplanner.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameText, passwordText;
    private Button loginButton;
    private TextView registerTextView;

    // Login for testing
    private final String validUsername = "Admin";
    private final String validPassword = "Password123";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        loginButton.setOnClickListener(v -> {
            String username = usernameText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();

            if (username.equals(validUsername) && password.equals(validPassword)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                registerTextView.setOnClickListener(v1 -> {
                    Toast.makeText(this, "Registration not implemented yet.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}