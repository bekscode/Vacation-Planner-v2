package com.romang.vacationplanner.UI;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.User;
import com.romang.vacationplanner.utils.PasswordUtils;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, passwordInput, confirmPasswordInput;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        repository = new Repository(getApplication());

        EditText usernameInput = findViewById(R.id.usernameText);
        EditText passwordInput = findViewById(R.id.passwordText);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordText);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields required.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords must match.", Toast.LENGTH_LONG).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                User existingUser = repository.getUserByUsername(username);
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Username unavailable", Toast.LENGTH_LONG).show());
                } else {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswordHash(PasswordUtils.hashPassword(password));
                    repository.insertUser(newUser);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration complete", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            });
        });
    }
}