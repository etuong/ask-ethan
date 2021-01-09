package com.project.askethan.utilities;

import com.google.firebase.auth.FirebaseUser;

public class AuthorHelper {
    private static String authorEmailAddres = "etuong@gmail.com";

    public static boolean isOwner() {
        FirebaseUser user = FirebaseModule.getCurrentUser();
        return user.getEmail().equals(authorEmailAddres);
    }
}
