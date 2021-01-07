package com.project.askethan.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.project.askethan.BaseFragment;
import com.project.askethan.LoginActivity;
import com.project.askethan.MainActivity;
import com.project.askethan.PasswordActivity;
import com.project.askethan.R;
import com.project.askethan.utilities.FirebaseModule;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;

import static android.app.Activity.RESULT_OK;
import static com.project.askethan.utilities.FirebaseModule.USER_TABLE_KEY;

public class ProfileFragment extends BaseFragment {
    private static final int SELECT_PICTURE = 10001;

    private CircleImageView profileImageView;
    private TextView displayNameText, displayEmailText, displayPhoneText;

    DatabaseReference dbUserRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        displayNameText = view.findViewById(R.id.profileNameTextView);
        displayEmailText = view.findViewById(R.id.profileEmailTextView);
        displayPhoneText = view.findViewById(R.id.profilePhoneTextView);
        RelativeLayout profilePictureLayout = view.findViewById(R.id.profileChangePicture);
        profilePictureLayout.setOnClickListener(this::handleImageClick);

        Button signoutBtn = view.findViewById(R.id.profileSignOut);
        signoutBtn.setOnClickListener(view1 -> {
            FirebaseModule.signOut();
            Toast.makeText(this.getActivity(), "Logout Successfully!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button changePasswordBtn = view.findViewById(R.id.profileChangePassword);
        changePasswordBtn.setOnClickListener(view12 -> {
            Intent change = new Intent(this.getActivity(), PasswordActivity.class);
            startActivity(change);
        });

        Button deleteAccountBtn = view.findViewById(R.id.profileDeleteAccount);
        deleteAccountBtn.setOnClickListener(view13 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext());
            dialog.setTitle("Do you reeaaallly want to delete this account?");
            dialog.setMessage("Deleting this account will result in completely removing your account and you will not be able to log in again.");
            dialog.setPositiveButton("Delete", (dialogInterface, i) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle("Please confirm your password");
                final EditText input = new EditText(this.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog1, which) -> {
                    FirebaseUser user = FirebaseModule.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), input.getText().toString());
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.delete()
                                            .addOnCompleteListener(task1 -> {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                    Query query = ref.child(USER_TABLE_KEY).orderByChild("email").equalTo(user.getEmail());

                                                    ((Query) query).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                                                appleSnapshot.getRef().removeValue();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });

                                                    Toast.makeText(this.getActivity(), "Account Deleted", Toast.LENGTH_LONG).show();

                                                    Intent intent = new Intent(this.getActivity(), MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(this.getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this.getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                });
                builder.setNegativeButton("Cancel", (dialog12, which) -> dialogInterface.cancel());
                builder.show();
            });
            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });

        FirebaseUser user = FirebaseModule.getCurrentUser();

        if (user != null) {
            displayNameText.setText(user.getDisplayName());
            displayEmailText.setText(user.getEmail());

            dbUserRef = FirebaseModule.getUserDatabaseReference().child(user.getUid()).getRef();
            dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    displayPhoneText.setText(dataSnapshot.child("phone").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(profileImageView);
            }
        }

        return view;
    }

    public void handleImageClick(View view) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Select or take a new picture for your profile";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @SneakyThrows
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = null;
                    if (data.getExtras() != null && data.getExtras().get("data") != null) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                    } else {
                        Uri imageUri = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                        if (getOrientation(this.getActivity().getApplicationContext(), imageUri) != 0) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(getOrientation(this.getActivity().getApplicationContext(), imageUri));
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        }
                        bitmap = Bitmap.createScaledBitmap(bitmap, 306, 408, true);
                    }
                    profileImageView.setImageBitmap(bitmap);
                    handleImageUpload(bitmap);
            }
        }
    }

    private int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private void handleImageUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseModule.getCurrentUser().getUid();
        final StorageReference profileImagesStorageRef = FirebaseModule.getStorageReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        profileImagesStorageRef.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> profileImagesStorageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            setUserProfileUrl(uri);
                        }));
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseModule.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity().getApplicationContext(),
                        "Profile Picture Updated Succesfully", Toast.LENGTH_SHORT).show());
    }
}
