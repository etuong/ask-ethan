package com.project.askethan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.askethan.utilities.FirebaseModule;

public class AnswerActivity extends BaseActivity {
    private TextView title, question, views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Intent intent = getIntent();
        final long id = intent.getExtras().getLong("question_id");
        title = findViewById(R.id.title);
        question = findViewById(R.id.question);
        views = findViewById(R.id.views);

        DatabaseReference dbPostRef = FirebaseModule.getQuestionDatabaseReference().child(String.valueOf(id)).getRef();
        dbPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title.setText(dataSnapshot.child("title").getValue().toString());
                question.setText(dataSnapshot.child("question").getValue().toString());
                views.setText(dataSnapshot.child("views").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
