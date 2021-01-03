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
    private static int numberOfSlides = 3;
    private SpannableString[] slideHeadings = new SpannableString[numberOfSlides];
    private String[] slideDescription = new String[numberOfSlides];
    private int[] slide_images = new int[numberOfSlides];

    public SliderAdapter(Context context) {
        this.context = context;

        slideHeadings[0] = new SpannableString("Ethan1");
        slideHeadings[1] = new SpannableString("Ethan2");
        slideHeadings[2] = new SpannableString("Ethan3");

        slideDescription[0] = "Ethan1";
        slideDescription[1] = "Ethan2";
        slideDescription[2] = "Ethan3";

        slide_images[0] = R.drawable.whatisthis;
        slide_images[1] = R.drawable.people;
        slide_images[2] = R.drawable.gotaquestion;
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

        imageView.setImageResource(slide_images[position]);
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