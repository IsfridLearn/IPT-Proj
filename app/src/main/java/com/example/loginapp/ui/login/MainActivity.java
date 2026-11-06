package com.example.loginapp.ui.login;

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
import com.example.loginapp.ui.register.RegisterActivity;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView showHidePasswordButton;

    private AuthRepository authRepository;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authRepository = new AuthRepository();

        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        TextView superButton = findViewById(R.id.superButton);
        TextView createAccountButton = findViewById(R.id.createAccountButton);
        TextView forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);

        showHidePasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showHidePasswordButton.setText("SHOW");
                isPasswordVisible = false;
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showHidePasswordButton.setText("HIDE");
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        superButton.setOnClickListener(v -> loginUser());

        createAccountButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        forgotPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        authRepository.login(email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                String errMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(MainActivity.this, "Login failed: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email to reset password");
            emailEditText.requestFocus();
            return;
        }

        authRepository.sendPasswordResetEmail(email, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
            } else {
                String errMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(MainActivity.this, "Error: " + errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
