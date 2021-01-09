package com.project.askethan.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseModule {
    public static String USER_TABLE_KEY = "users";
    public static String QUESTION_TABLE_KEY = "questions";
    public static String LOCATION_TABLE_KEY = "locations";

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static DatabaseReference getUserDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(USER_TABLE_KEY);
    }

    public static DatabaseReference getQuestionDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(QUESTION_TABLE_KEY);
    }

    public static DatabaseReference getLocationDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(LOCATION_TABLE_KEY);
    }

    public static void signOut() {
        getAuth().signOut();
    }

    public static StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }
}
