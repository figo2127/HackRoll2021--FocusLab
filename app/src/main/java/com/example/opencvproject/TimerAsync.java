package com.example.opencvproject;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;
import java.util.Date;
import java.lang.Object;


public class TimerAsync extends AsyncTask<Integer, Integer, Integer> {

    int timeElapsed;

    public TimerAsync(Integer integer) {
        this.timeElapsed = integer;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        //call execute(Param) in MainActivity, Param is the argument type, received in doInBackground

        return null;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //the type of the argument you pass to onProgressUpdate when you call publishProgress in MainActivity.
        //we can have a CheckTimeLeft button in MainActivity for user to peep their time left.
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer result) {
        //the type of argument you pass to onPostExecute, which is what you return from
        super.onPostExecute(result);
        //set a Toast to show timer ended.
    }
}
