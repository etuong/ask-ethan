package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class PasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);
        EditText emailText = findViewById(R.id.etcheckemail);
        Button emailSendBtn = findViewById(R.id.btnemailsend);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        emailSendBtn.setOnClickListener(view -> {

            String user_email = emailText.getText().toString().trim();

            if (TextUtils.isEmpty(user_email)) {
                Toast.makeText(PasswordActivity.this, "Please enter your registered email address", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PasswordActivity.this, "Email to reset is sent!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(PasswordActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
