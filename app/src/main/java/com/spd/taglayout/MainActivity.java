package com.spd.taglayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.spd.widget.AdaptiveTagLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AdaptiveTagLayout tagLayout = (AdaptiveTagLayout) findViewById(R.id.tagLayout);
        LayoutInflater layoutInflater = getLayoutInflater();
        String tag;
        String[] tags = new String[] {
                "Steve Jobs", "Sunny Leon", "TagLayout", "India", "Lollipop",
                "Android", "college", "Asian Hot", "Ballroom", "Mia Khalifa",
        };
        for (int i = 0; i < 10; i++) {
            tag = tags[i];
            View tagView = layoutInflater.inflate(R.layout.tag_layout, null, false);

            TextView tagTextView = (TextView) tagView.findViewById(R.id.tagTextView);
            tagTextView.setText(tag);
            tagLayout.addView(tagView);
        }
    }
}
