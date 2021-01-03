package com.project.askethan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView signupText, forgotPasswordText, attemptText;
    private Button btnLogin;
    private EditText email, password;
    private int counter = 5;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.signupText = findViewById(R.id.signup);
        this.forgotPasswordText = findViewById(R.id.forgot);
        this.email = findViewById(R.id.logemail);
        this.password = findViewById(R.id.logpassword);
        this.attemptText = findViewById(R.id.tvinfo);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.attemptText.setText("Login Attempts remaining: 5");
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.progressDialog = new ProgressDialog(this);

        this.signupText.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(i);
        });

        this.forgotPasswordText.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, PasswordActivity.class);
            startActivity(i);
        });

        this.btnLogin.setOnClickListener(view -> process(email.getText().toString().trim(), password.getText().toString().trim()));
    }

    public void process(String userEmail, String userPassword) {
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        this.progressDialog.setMessage("Your account has been successfully verified through email");
        this.progressDialog.show();
        this.firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.progressDialog.dismiss();
                checkEmailVerification();
            } else {
                this.progressDialog.dismiss();

                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                this.counter--;

                this.attemptText.setText("Login Attempts remaining: " + counter);

                if (this.counter == 0) {
                    this.btnLogin.setEnabled(false);
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified()) {
            finish();
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, FeedActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "Please verify by checking your email", Toast.LENGTH_LONG).show();
            this.firebaseAuth.signOut();
        }
    }
}
