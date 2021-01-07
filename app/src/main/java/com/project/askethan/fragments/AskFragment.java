package com.project.askethan.fragments;


import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.askethan.BaseFragment;
import com.project.askethan.R;
import com.project.askethan.model.Question;
import com.project.askethan.utilities.FirebaseModule;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AskFragment extends BaseFragment {
    private EditText titleEdit, questionEdit;
    private Button btnPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);

        titleEdit = view.findViewById(R.id.questionTitle);
        questionEdit = view.findViewById(R.id.question);
        btnPost = view.findViewById(R.id.btnPostNewQuestion);

        btnPost.setOnClickListener(view1 -> {

            if (TextUtils.isEmpty(titleEdit.getText())) {
                Toast.makeText(getActivity().getApplicationContext(), "Title field cannot be left empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(questionEdit.getText())) {
                Toast.makeText(getActivity().getApplicationContext(), "Description field cannot be left empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference dbRef = FirebaseModule.getQuestionDatabaseReference();

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FirebaseUser currentUser = FirebaseModule.getCurrentUser();
                    ZoneId zoneId = ZoneId.systemDefault();
                    long s = LocalDateTime.now().atZone(zoneId).toEpochSecond();
                    long newId = -1 * s;

                    Question question = Question.builder()
                            .id(newId)
                            .authorName(currentUser.getDisplayName())
                            .authorUid(currentUser.getUid())
                            .title(titleEdit.getText().toString())
                            .question(AskFragment.this.questionEdit.getText().toString())
                            .build();

                    DatabaseReference ref = dbRef.child(Long.toString(newId));
                    ref.setValue(question);

                    Toast.makeText(getActivity().getApplicationContext(), "Posted Successfully!", Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(AskFragment.this.getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_feed);

                    titleEdit.setText("");
                    questionEdit.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });

        return view;
    }
}
