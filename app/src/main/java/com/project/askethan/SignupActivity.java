package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.project.askethan.model.User;

public class SignupActivity extends AppCompatActivity {
    private TextView registersignin;
    private Button register;
    private CheckBox check;
    private EditText name, email, password, phone;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registersignin = findViewById(R.id.regsignin);
        check = findViewById(R.id.checkBox);
        name = findViewById(R.id.nametxt);
        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);
        phone = findViewById(R.id.etphone);
        register = findViewById(R.id.signupbtn);

        firebaseAuth = FirebaseAuth.getInstance();


        register.setOnClickListener(view -> {

            registerUser();

            if (validate() && registerUser()) {
                final String user_name = name.getText().toString().trim();
                final String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                final String user_phone;
                user_phone = phone.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

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

                            // Toast.makeText(signup.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(signup.this,MainFeed.class));


                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        registersignin.setOnClickListener(view -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }

    private Boolean validate() {
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String phone = this.phone.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean registerUser() {
        Boolean result = false;

        String nam = this.name.getText().toString();
        String ema = this.email.getText().toString();
        String pass = this.password.getText().toString();
        String phon = this.phone.getText().toString();

        if (pass.length() < 6) {
            password.setError("Password should be at least 6 characters long");
            password.requestFocus();
            return false;
        }

        if (phon.length() != 10) {
            phone.setError("Enter a valid phone number");
            phone.requestFocus();
            return false;
        }
        if (check.isChecked() == false) {
            check.setError("Please click confirmation");
            check.requestFocus();
            return false;
        } else {

            result = true;
        }

        return result;
    }

    private void sendEmail() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Successfully registered,Verification email sent", Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SignupActivity.this, "Verification email hasnt been sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
