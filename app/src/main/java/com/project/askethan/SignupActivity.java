package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.project.askethan.model.User;

public class SignupActivity extends AppCompatActivity {
    private TextView signinText;
    private Button registerBtn;
    private CheckBox checkBox;
    private EditText nameText, emailText, passwordText, phoneText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.signinText = findViewById(R.id.signin);
        this.checkBox = findViewById(R.id.checkBox);
        this.nameText = findViewById(R.id.nametxt);
        this.emailText = findViewById(R.id.emailtxt);
        this.passwordText = findViewById(R.id.passwordtxt);
        this.phoneText = findViewById(R.id.etphone);
        this.registerBtn = findViewById(R.id.signupbtn);

        this.signinText.setOnClickListener(view -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });

        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(view -> {
            final String user_name = nameText.getText().toString().trim();
            final String user_email = emailText.getText().toString().trim();
            final String user_password = passwordText.getText().toString().trim();
            final String user_phone = phoneText.getText().toString().trim();

            if (validateUser(user_name, user_email, user_password, user_phone)) {
                this.firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = User.builder()
                                .name(user_name)
                                .email(user_email)
                                .phone(user_phone)
                                .build();

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user);

                        sendEmail();

                    } else {
                        Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateUser(String user_name, String user_email, String user_password, String user_phone) {
        if (user_name.isEmpty() || user_email.isEmpty() ||
                user_password.isEmpty() || user_phone.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isValidated = true;

        if (user_email.matches("^(.+)@(.+)$")) {
            this.emailText.setError("Please enter a valid email address");
            this.emailText.requestFocus();
            isValidated = false;
        }

        if (user_password.length() < 6) {
            this.passwordText.setError("Password should be at least 6 characters long");
            this.passwordText.requestFocus();
            isValidated = false;
        }

        if (user_phone.matches("[0-9]+") && user_phone.length() != 7) {
            this.phoneText.setError("Please enter a valid phone number");
            this.phoneText.requestFocus();
            isValidated = false;
        }

        if (checkBox.isChecked() == false) {
            this.checkBox.setError("Please agree to the terms and conditions");
            this.checkBox.requestFocus();
            isValidated = false;
        }

        return isValidated;
    }

    private void sendEmail() {
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Successfully registered,Verification email sent", Toast.LENGTH_LONG).show();
                    this.firebaseAuth.signOut();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SignupActivity.this, "Verification email hasnt been sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
