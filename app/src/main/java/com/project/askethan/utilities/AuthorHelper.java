package com.project.askethan.utilities;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.askethan.model.AuthorLocation;
import com.project.askethan.model.Mood;

import java.util.concurrent.Semaphore;

public class AuthorHelper {
    private static String authorEmailAddres = "etuong@gmail.com";

    public static boolean isOwner() {
        FirebaseUser user = FirebaseModule.getCurrentUser();
        return user.getEmail().equals(authorEmailAddres);
    }

    public static Mood getAuthorMood() {
        final Mood[] mood = {Mood.OK};
        DatabaseReference dbMoodRef = FirebaseModule.getMoodDatabaseReference();
        dbMoodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                mood[0] = Mood.valueOf(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return mood[0];
    }
}
