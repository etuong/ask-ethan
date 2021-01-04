package com.project.askethan.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.project.askethan.R;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private static int numberOfSlides = 4;
    private SpannableString[] slideHeadings = new SpannableString[numberOfSlides];
    private String[] slideDescription = new String[numberOfSlides];
    private int[] slideImages = new int[numberOfSlides];

    public SliderAdapter(Context context) {
        this.context = context;

        slideHeadings[0] = new SpannableString("What is Ask Ethan?");
        slideHeadings[1] = new SpannableString("What kind of question can I ask?");
        slideHeadings[2] = new SpannableString("What else can this app do?");
        slideHeadings[3] = new SpannableString("How do I get started!?");

        slideDescription[0] = "It's a general advise columnist app where Ethan can answer all your questions!";
        slideDescription[1] = "Ethan answers any and all your questions with care and attention, while also providing a plainspoken, straight-shooting dose of reality that often only comes to us from close friends.";
        slideDescription[2] = "It can provide real-time status about where Ethan is and how he is feeling.";
        slideDescription[3] = "Create an account or log in and start asking your questions!";

        slideImages[0] = R.drawable.matrix;
        slideImages[1] = R.drawable.question;
        slideImages[2] = R.drawable.navigation;
        slideImages[3] = R.drawable.lock;
    }

    @Override
    public int getCount() {
        return numberOfSlides;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_content_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageHolder);
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.pageDescription);

        imageView.setImageResource(slideImages[position]);
        title.setText(slideHeadings[position]);
        description.setText(slideDescription[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((androidx.constraintlayout.widget.ConstraintLayout) object);
    }
}
