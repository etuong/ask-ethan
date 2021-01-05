package com.project.askethan.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.askethan.AnswerActivity;
import com.project.askethan.R;
import com.project.askethan.model.Question;
import com.project.askethan.utilities.FirebaseModule;

public class FeedFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ListView questionList = view.findViewById(R.id.question_listview);

        final DatabaseReference dbRef = FirebaseModule.getQuestionDatabaseReference();

        FirebaseListAdapter<Question> firebaseListAdapter = new FirebaseListAdapter<Question>(
                this.getActivity(),
                Question.class,
                R.layout.fragment_questions,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Question model, int position) {
                TextView questionTitle = v.findViewById(R.id.question_title);
                TextView questionAuthor = v.findViewById(R.id.question_author);
                TextView questionViews = v.findViewById(R.id.views);

                questionTitle.setText(model.getTitle());
                questionAuthor.setText("By " + model.getAuthor());
                questionViews.setText(String.valueOf(model.getViews()));
            }
        };

        questionList.setAdapter(firebaseListAdapter);
        questionList.setOnItemClickListener((adapterView, view1, pos, id) -> {
            final DatabaseReference dbRef1 = dbRef.child(String.valueOf(pos));

            dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent viewQuestion = new Intent(getActivity(), AnswerActivity.class);
                    viewQuestion.putExtra("question_id", Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                    startActivity(viewQuestion);

                    int viewCount = Integer.parseInt(dataSnapshot.child("views").getValue().toString());
                    dbRef1.child("views").setValue(viewCount + 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });

        return view;
    }

}
