package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private static int PASSWORD_MIN_NUM = 6;
    private TextView signinText;
    private Button registerBtn;
    private CheckBox checkBox;
    private EditText nameEdit, emailEdit, passwordEdit, phoneEdit;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.signinText = findViewById(R.id.signin);
        this.checkBox = findViewById(R.id.checkBox);
        this.nameEdit = findViewById(R.id.nametxt);
        this.emailEdit = findViewById(R.id.emailtxt);
        this.passwordEdit = findViewById(R.id.passwordtxt);
        this.phoneEdit = findViewById(R.id.etphone);
        this.registerBtn = findViewById(R.id.signupbtn);

        this.signinText.setOnClickListener(view -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });

        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(view -> {
            final String user_name = nameEdit.getText().toString().trim();
            final String user_email = emailEdit.getText().toString().trim();
            final String user_password = passwordEdit.getText().toString().trim();
            final String user_phone = phoneEdit.getText().toString().trim();

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
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean validateUser(String user_name, String user_email, String user_password, String user_phone) {
        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password) || TextUtils.isEmpty(user_phone)) {
            Toast.makeText(SignupActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isValidated = true;

        if (!user_email.matches("^(.+)@(.+)$")) {
            this.emailEdit.setError("Please enter a valid email address");
            this.emailEdit.requestFocus();
            isValidated = false;
        }

        if (user_password.length() < PASSWORD_MIN_NUM) {
            this.passwordEdit.setError(String.format("Password should be at least %d characters long", PASSWORD_MIN_NUM));
            this.passwordEdit.requestFocus();
            isValidated = false;
        }

        if (!user_phone.matches("[0-9]+") || user_phone.length() < 7) {
            this.phoneEdit.setError("Please enter a valid phone number");
            this.phoneEdit.requestFocus();
            isValidated = false;
        }

        if (!checkBox.isChecked()) {
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
                    Toast.makeText(SignupActivity.this, "Successfully registered! Verification email sent", Toast.LENGTH_LONG).show();
                    this.firebaseAuth.signOut();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SignupActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
