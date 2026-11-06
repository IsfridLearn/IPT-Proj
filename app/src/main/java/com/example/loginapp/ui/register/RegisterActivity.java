package com.example.loginapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.R;
import com.example.loginapp.data.AuthRepository;
import com.example.loginapp.ui.home.HomeActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView showHidePasswordButton;

    private AuthRepository authRepository;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        TextView registerButton = findViewById(R.id.registerButton);
        TextView backToLoginButton = findViewById(R.id.backToLoginButton);
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);

        showHidePasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showHidePasswordButton.setText("SHOW");
                isPasswordVisible = false;
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showHidePasswordButton.setText("HIDE");
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
            confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
        });

        registerButton.setOnClickListener(v -> registerUser());

        backToLoginButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        authRepository.register(email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                String errMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(RegisterActivity.this, "Registration failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
