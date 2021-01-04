package com.project.askethan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.project.askethan.adapters.SliderAdapter;

public class MainActivity extends AppCompatActivity {
    private ViewPager mainPage;
    private LinearLayout horzDotsLayout;
    private SliderAdapter sliderAdapter;
    private Button nextBtn;
    private Button backBtn;
    private Button signInBtn;
    private int currentSliderNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // User has already logged in, declare intent to activate the main feed
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent mainFeed = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(mainFeed);
        }

        this.mainPage = findViewById(R.id.mainViewPager);
        this.horzDotsLayout = findViewById(R.id.sliderDots);
        this.nextBtn = findViewById(R.id.next);
        this.backBtn = findViewById(R.id.back);
        this.signInBtn = findViewById(R.id.signin);

        this.sliderAdapter = new SliderAdapter(this);
        this.mainPage.setAdapter(sliderAdapter);

        renderHorzDots(0);

        this.mainPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                renderHorzDots(position);
                currentSliderNumber = position;

                if (currentSliderNumber == 0) {
                    setButtonEnability(nextBtn, true);
                    setButtonEnability(backBtn, false);
                } else if (currentSliderNumber == sliderAdapter.getCount() - 1) {
                    setButtonEnability(nextBtn, false);
                    setButtonEnability(backBtn, true);
                    setButtonEnability(signInBtn, true);
                } else {
                    setButtonEnability(nextBtn, true);
                    setButtonEnability(backBtn, true);
                    setButtonEnability(signInBtn, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.nextBtn.setOnClickListener(view -> {
            if (this.currentSliderNumber + 1 < this.sliderAdapter.getCount())
                this.mainPage.setCurrentItem(this.currentSliderNumber + 1);
        });

        this.backBtn.setOnClickListener(view -> {
            if (this.currentSliderNumber - 1 >= 0)
                this.mainPage.setCurrentItem(this.currentSliderNumber - 1);
        });

        this.signInBtn.setOnClickListener(view -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    private void setButtonEnability(Button btn, boolean enabled) {
        if (enabled) {
            btn.setVisibility(View.VISIBLE);
            btn.setEnabled(true);
        } else {
            btn.setVisibility(View.INVISIBLE);
            btn.setEnabled(false);
        }
    }

    private void renderHorzDots(int position) {
        int numberOfSlides = this.sliderAdapter.getCount();
        TextView[] horzDots = new TextView[numberOfSlides];
        this.horzDotsLayout.removeAllViews();

        for (int i = 0; i < numberOfSlides; i++) {
            horzDots[i] = new TextView(this);
            horzDots[i].setText(Html.fromHtml("&#8226;"));
            horzDots[i].setTextSize(35);
            horzDots[i].setTextColor(Color.WHITE);

            this.horzDotsLayout.addView(horzDots[i]);
        }

        if (position >= 0) {
            horzDots[position].setTextColor(Color.GRAY);
        }
    }
}