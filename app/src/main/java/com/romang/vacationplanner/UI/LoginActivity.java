package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.User;
import com.romang.vacationplanner.utils.PasswordUtils;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameText, passwordText;
    private Button loginButton;
    private TextView registerTextView;
    private Repository repository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new Repository(getApplication());
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        Executors.newSingleThreadExecutor().execute(() -> {
           //login for testing
            if (repository.getUserByUsername("Admin") == null) {
                User user = new User();
                user.setUsername("Admin");
                user.setPasswordHash(PasswordUtils.hashPassword("Password"));
                repository.insertUser(user);
            }
        });

        loginButton.setOnClickListener(v -> {
                    String username = usernameText.getText().toString().trim();
                    String password = passwordText.getText().toString().trim();
                    String hash = PasswordUtils.hashPassword(password);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        //input validation for all required fields
                        if (username == null || username.trim().isEmpty() || hash == null || hash.trim().isEmpty()) {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Username and password are required.", Toast.LENGTH_LONG).show();
                            });
                            return;
                        }
                        User user = repository.getUserByUsername(username);
                        //authentication to check if user exists and password matches
                        if (user != null && user.getPasswordHash().equals(hash)) {
                            //if true login
                            runOnUiThread(() -> {
                                Intent intent = new Intent(LoginActivity.this, VacationList.class);
                                startActivity(intent);
                                finish();
                            });
                            //if false username or password is incorrect
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_LONG).show();
                            });
                        }
                    });
        });
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}