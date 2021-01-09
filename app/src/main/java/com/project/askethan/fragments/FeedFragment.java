package com.project.askethan.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.project.askethan.AnswerActivity;
import com.project.askethan.BaseFragment;
import com.project.askethan.R;
import com.project.askethan.model.Question;
import com.project.askethan.utilities.FirebaseModule;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedFragment extends BaseFragment {
    private static final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ListView questionList = view.findViewById(R.id.question_listview);

        final StorageReference profileImagesStorageRef = FirebaseModule.getStorageReference()
                .child("profileImages");

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
                CircleImageView profileImageView = v.findViewById(R.id.questionImageView);

                questionTitle.setText(model.getTitle());
                questionAuthor.setText("By " + model.getAuthorName());

                if (isAdded() && model.getAuthorUid() != null) {
                    final StorageReference profileImageStorageRef = profileImagesStorageRef.child(model.getAuthorUid() + ".jpeg");
                    profileImageStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(taskSnapshot -> profileImageStorageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                if (uri != null && FeedFragment.this.getActivity() != null) {
                                    Glide.with(FeedFragment.this.getActivity())
                                            .load(uri)
                                            .into(profileImageView);
                                }
                            }));
                }
            }
        };

        questionList.setAdapter(firebaseListAdapter);
        questionList.setOnItemClickListener((adapterView, view1, pos, id) -> {
            Question selectedQuestion = (Question) adapterView.getItemAtPosition(pos);
            final DatabaseReference questionDbRef = dbRef.child(String.valueOf(selectedQuestion.getId()));
            int viewCount = selectedQuestion.getViews();
            questionDbRef.child("views").setValue(viewCount + 1);

            Intent viewQuestion = new Intent(getActivity(), AnswerActivity.class);
            viewQuestion.putExtra("question_id", selectedQuestion.getId());
            startActivity(viewQuestion);
        });

        return view;
    }
}
