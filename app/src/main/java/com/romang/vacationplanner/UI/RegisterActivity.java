package com.romang.vacationplanner.UI;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.User;
import com.romang.vacationplanner.utils.PasswordUtils;

import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput;
    private Repository repository;
    private Button registerButton;

    // Password requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_USERNAME_LENGTH = 30;
    private static final int MIN_USERNAME_LENGTH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        repository = new Repository(getApplication());

        usernameInput = findViewById(R.id.usernameText);
        passwordInput = findViewById(R.id.passwordText);
        confirmPasswordInput = findViewById(R.id.confirmPasswordText);
        registerButton = findViewById(R.id.registerButton);

        // Initialize fields with empty text to prevent IndexOutOfBoundsException
        if (usernameInput != null) {
            usernameInput.setText("");
        }
        if (passwordInput != null) {
            passwordInput.setText("");
        }
        if (confirmPasswordInput != null) {
            confirmPasswordInput.setText("");
        }

        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        // Hide keyboard and clear focus to prevent text selection issues

        clearFieldFocus();

        // Prevent empty field crash
        String username = "";
        String password = "";
        String confirmPassword = "";

        if (usernameInput != null && usernameInput.getText() != null) {
            username = usernameInput.getText().toString().trim();
        }
        if (passwordInput != null && passwordInput.getText() != null) {
            password = passwordInput.getText().toString();
        }
        if (confirmPasswordInput != null && confirmPasswordInput.getText() != null) {
            confirmPassword = confirmPasswordInput.getText().toString();
        }

        // Validate all inputs
        String validationError = validateInputs(username, password, confirmPassword);
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
            return;
        }

        // Disable button to prevent multiple submissions
        registerButton.setEnabled(false);

        // Store values for use in background thread
        final String finalUsername = username;
        final String finalPassword = password;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Check if username already exists
                User existingUser = repository.getUserByUsername(finalUsername);
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already exists.", Toast.LENGTH_LONG).show();
                        registerButton.setEnabled(true);
                    });
                    return;
                }

                // Create new user with hashed password
                User newUser = new User();
                newUser.setUsername(finalUsername);
                newUser.setPasswordHash(PasswordUtils.hashPassword(finalPassword));
                repository.insertUser(newUser);

                runOnUiThread(() -> {
                    clearAllFields();

                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                });
            }
        });
    }

    // Username and password validation
    private String validateInputs(String username, String password, String confirmPassword) {
        // Check for empty fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "All fields are required.";
        }

        // Validate username length
        if (username.length() < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters.";
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            return "Username must not exceed " + MAX_USERNAME_LENGTH + " characters.";
        }

        // Validate username characters
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
            return "Username can only contain letters, numbers, and underscores.";
        }

        // Validate password length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.";
        }

        // Validate password strength
        if (!isPasswordStrong(password)) {
            return "Password must contain at least one uppercase letter, one lowercase letter, and one number.";
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        return null;
    }


    private boolean isPasswordStrong(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasLowercase && hasDigit;
    }


    private void clearAllFields() {
        if (usernameInput != null) {
            usernameInput.post(() -> {
                usernameInput.clearFocus();
                usernameInput.setText("");
            });
        }
        if (passwordInput != null) {
            passwordInput.post(() -> {
                passwordInput.clearFocus();
                passwordInput.setText("");
            });
        }
        if (confirmPasswordInput != null) {
            confirmPasswordInput.post(() -> {
                confirmPasswordInput.clearFocus();
                confirmPasswordInput.setText("");
            });
        }
    }


    private void clearFieldFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }

        if (usernameInput != null) {
            usernameInput.clearFocus();
        }
        if (passwordInput != null) {
            passwordInput.clearFocus();
        }
        if (confirmPasswordInput != null) {
            confirmPasswordInput.clearFocus();
        }
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (registerButton != null) {
            registerButton.setEnabled(true);
        }
    }
}