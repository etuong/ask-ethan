package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.project.askethan.utilities.FirebaseModule;

public class LoginActivity extends AppCompatActivity {
    private TextView signupText, forgotPasswordText, attemptText;
    private Button loginBtn;
    private EditText emailEdit, passwordEdit;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.signupText = findViewById(R.id.signup);
        this.forgotPasswordText = findViewById(R.id.forgot);
        this.emailEdit = findViewById(R.id.logemail);
        this.passwordEdit = findViewById(R.id.logpassword);
        this.attemptText = findViewById(R.id.tvinfo);
        this.loginBtn = findViewById(R.id.btnLogin);
        this.attemptText.setText("Login Attempts remaining: 5");

        this.signupText.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(i);
        });

        this.forgotPasswordText.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, PasswordActivity.class);
            startActivity(i);
        });

        this.loginBtn.setOnClickListener(view -> process(this.emailEdit.getText().toString().trim(), this.passwordEdit.getText().toString().trim()));
    }

    public void process(String userEmail, String userPassword) {
        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(LoginActivity.this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseModule.getAuth().signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkEmailVerification();
            } else {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                this.counter--;

                this.attemptText.setText("Login Attempts remaining: " + counter);

                if (this.counter == 0) {
                    this.loginBtn.setEnabled(false);
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseModule.getCurrentUser();

        if (firebaseUser.isEmailVerified()) {
            finish();
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "Please first verify your account by checking your email", Toast.LENGTH_LONG).show();
            FirebaseModule.signOut();
        }
    }
}
