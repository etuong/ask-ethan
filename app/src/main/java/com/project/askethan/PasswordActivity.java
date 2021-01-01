package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);
        EditText emailcheck = findViewById(R.id.etcheckemail);
        Button emailsend = findViewById(R.id.btnemailsend);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        emailsend.setOnClickListener(view -> {

            String user_email = emailcheck.getText().toString().trim();

            if (user_email.equals("")) {
                Toast.makeText(PasswordActivity.this, "Please enter your registered emailID", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PasswordActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(PasswordActivity.this, "Errpr in sending reset password email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView back = findViewById(R.id.btnhome);
        back.setOnClickListener(view -> {
            Intent i = new Intent(PasswordActivity.this, LoginActivity.class);
            startActivity(i);
            //firebaseAuth.signOut();
        });
    }
}
