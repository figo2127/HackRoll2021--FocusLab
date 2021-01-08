package com.example.opencvproject;

import android.content.Intent;
import android.database.Cursor;
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
import com.jjoe64.graphview.GridLabelRenderer;
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
    DatabaseHelper DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.stats_activity);

        DB = new DatabaseHelper(this);

        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {

            cal.set(Calendar.DAY_OF_WEEK, i);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            DataPoint dp;
            Cursor cursorData = DB.retrieveData(df.format(cal.getTime()));
            Log.e("current date: ", df.format(cal.getTime()));

            if(cursorData.getCount() <= 0) {
                dp = new DataPoint(i-1, 0);
            }
            else{
                 dp = new DataPoint(i-1, Integer.parseInt(cursorData.getString(1)));
            }
            series.appendData(dp,true,7);
        }
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Sun", "Mon", "Tues", "Weds", "Thurs",
                "Fri", "Sat"});

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setHumanRounding(true);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(35);

        Button statistic_btn = findViewById(R.id.back_btn);
        statistic_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
