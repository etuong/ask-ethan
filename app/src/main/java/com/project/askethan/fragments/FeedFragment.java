package com.project.askethan.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.project.askethan.R;
import com.project.askethan.model.Question;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private List<Question> questionList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_feed, container, false);
       /* ListView mainListView = view.findViewById(R.id.question_listview);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = database.getReference("posts");

        FirebaseListAdapter<Question> firebaseListAdapter = new FirebaseListAdapter<Question>(
                this.getActivity(),
                Question.class,
                R.layout.question_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Question model, int position) {

                TextView questionTitle = v.findViewById(R.id.question_title);
                TextView questionAuthor = v.findViewById(R.id.question_author);
                TextView questionViews = v.findViewById(R.id.views);

                questionTitle.setText(model.getTitle());
                questionAuthor.setText("by "+model.getAuthor());
                questionViews.setText(String.valueOf(model.getViews()));

            }
        };

        mainListView.setAdapter(firebaseListAdapter);

        mainListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            final DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("posts").child(String.valueOf(i));

            dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int viewCount = Integer.parseInt(dataSnapshot.child("views").getValue().toString());
                    dbRef1.child("views").setValue(viewCount+1);
                    Intent viewQuestion = new Intent(getActivity(), ViewQuestion.class);
                    viewQuestion.putExtra("question_id", Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                    startActivity(viewQuestion);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });*/

        return view;
    }

}
