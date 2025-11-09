package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private Repository repository;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new Repository(getApplication());
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.registerTextView);

        // Initialize fields with empty text to prevent IndexOutOfBoundsException
        if (usernameText != null) {
            usernameText.setText("");
        }
        if (passwordText != null) {
            passwordText.setText("");
        }

        // Create default admin user for testing
        Executors.newSingleThreadExecutor().execute(() -> {
            if (repository.getUserByUsername("Admin") == null) {
                User user = new User();
                user.setUsername("Admin");
                user.setPasswordHash(PasswordUtils.hashPassword("Password"));
                repository.insertUser(user);
            }
        });

        loginButton.setOnClickListener(v -> attemptLogin());
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Hide keyboard and clear focus to prevent text selection issues
        hideKeyboard();
        clearFieldFocus();

        // Safely get text from fields
        String username = "";
        String password = "";

        if (usernameText != null && usernameText.getText() != null) {
            username = usernameText.getText().toString().trim();
        }
        if (passwordText != null && passwordText.getText() != null) {
            password = passwordText.getText().toString().trim();
        }

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password are required.", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable login button to prevent multiple clicks
        loginButton.setEnabled(false);

        // Store password for use in background thread
        final String finalUsername = username;
        final String finalPassword = password;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                User user = repository.getUserByUsername(finalUsername);
                if (user != null && PasswordUtils.verifyPassword(finalPassword, user.getPasswordHash())) {

                    // Store user session
                    saveUserSession(user.getUserID(), finalUsername);

                    // Login successful
                    runOnUiThread(() -> {
                        // Clear password field for security
                        clearPasswordField();

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, VacationList.class);
                        intent.putExtra("USER_ID", user.getUserID());
                        intent.putExtra("USERNAME", finalUsername);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // Invalid credentials
                    runOnUiThread(() -> {
                        // Clear password field for security
                        clearPasswordField();

                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(true);
                    });
                }
            } catch (Exception e) {
                // Handle any unexpected errors
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
                });
            }
        });
    }

    /**
     * Safely clears the password field to prevent IndexOutOfBoundsException
     */
    private void clearPasswordField() {
        if (passwordText != null) {
            passwordText.post(() -> {
                passwordText.clearFocus();
                passwordText.setText("");
            });
        }
    }

    /**
     * Clears focus from all input fields
     */
    private void clearFieldFocus() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }

        if (usernameText != null) {
            usernameText.clearFocus();
        }
        if (passwordText != null) {
            passwordText.clearFocus();
        }
    }

    /**
     * Hides the soft keyboard
     */
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Save user session to SharedPreferences for persistent login
     */
    private void saveUserSession(int userId, String username) {
        SharedPreferences prefs = getSharedPreferences("VacationPlannerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("USER_ID", userId);
        editor.putString("USERNAME", username);
        editor.putBoolean("IS_LOGGED_IN", true);
        editor.apply();
    }


    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("VacationPlannerPrefs", MODE_PRIVATE);
        return prefs.getBoolean("IS_LOGGED_IN", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-enable login button when returning to this activity
        if (loginButton != null) {
            loginButton.setEnabled(true);
        }
        // Clear password field when returning to login screen
        clearPasswordField();
    }
}