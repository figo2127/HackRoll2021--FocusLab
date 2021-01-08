package com.example.opencvproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FrontPage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page_activity);
        Log.e(null, String.format("Today's duration: %s", MainActivity.accessDuration("2021-01-07")));

        Button btn = findViewById(R.id.open_activity_button);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FrontPage.this, MainActivity.class);
                FrontPage.this.startActivity(myIntent);
            }
        });

        Button view_stats_btn = findViewById(R.id.stats_btn);
        view_stats_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FrontPage.this, Statistic.class);
                FrontPage.this.startActivity(myIntent);
            }
        });
    }
}

