package com.example.loginapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.R;
import com.example.loginapp.data.AuthRepository;
import com.example.loginapp.ui.login.MainActivity;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        authRepository = new AuthRepository();
        TextView userEmailTextView = findViewById(R.id.userEmailTextView);
        TextView logoutButton = findViewById(R.id.logoutButton);

        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmailTextView.setText(currentUser.getEmail());
        } else {
            userEmailTextView.setText("user@email.com");
        }

        logoutButton.setOnClickListener(v -> {
            authRepository.logout();
            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
