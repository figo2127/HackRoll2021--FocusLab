package com.example.opencvproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Statistic extends AppCompatActivity {

    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.stats_activity);

        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        db = openHelper.getWritableDatabase();


        for (int i = Calendar.MONDAY; i <= Calendar.SATURDAY; i++) {

            cal.set(Calendar.DAY_OF_WEEK, i);

            Log.e("", cal.getTime().toString());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Log.e("current date" , df.format(cal.getTime()));//Returns Date

            DataPoint dp;

            if(MainActivity.accessDuration(df.format(cal.getTime())) == null) {
                 dp = new DataPoint(i-1, 1);
                Log.e(null , String.valueOf(MainActivity.accessDuration(df.format(cal.getTime()))));

            }
            else{
                Log.e(null, "graph focused time" + MainActivity.accessDuration(df.format(cal.getTime())).toString());
                int data = Integer.parseInt(String.valueOf(MainActivity.accessDuration(df.format(cal.getTime()))));
                 dp = new DataPoint(i-1, data);
            }
            series.appendData(dp,true,7);

            if(i== Calendar.SATURDAY){
                cal.set(Calendar.SUNDAY, i);
                System.out.println("sun is" + cal.getTime());//Returns Date

                if(MainActivity.accessDuration(cal.getTime().toString()) == null) {
                    dp = new DataPoint(7, 10);
                }
                else{
                    int data = Integer.parseInt(String.valueOf(MainActivity.accessDuration(cal.getTime().toString())));
                    Log.e(null, "graph focused time" + MainActivity.accessDuration(cal.getTime().toString()).toString());
                    dp = new DataPoint(7, data);
                }
                series.appendData(dp,true,7);

            }
        }
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Mon", "Tues", "Weds", "Thurs",
                "Fri", "Sat","Sun" });

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getGridLabelRenderer().setHumanRounding(false);

        Button statistic_btn = findViewById(R.id.back_btn);
        statistic_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }
}
