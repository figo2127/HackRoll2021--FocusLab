package com.example.opencvproject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    JavaCameraView javaCameraView;
    File cascFile;

    FaceEyeDetector faceEyeDetector;
    CascadeClassifier faceDetector;
    private Mat mRgba, mGrey;

    long startTime = SystemClock.elapsedRealtime();
    long endTime;
    long dailyDuration = 0;
    public static HashMap<String, Long> durations = new HashMap<String, Long>();

    private static final int PERMISSION_REQUEST_CODE = 200;

    public static Long accessDuration(String date) {
        return durations.get(date);
    }

    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private VideoView mVideoView;


    //    SQLiteOpenHelper openHelper;
//    SQLiteDatabase db;
    DatabaseHelper DB;


    // to insert data: Steps below
    // 1) db = openHelper.getWritableDatabase(); <-- you need this before u can call insertData
    // 2) call insertData(Date date, Float TimeFocused)
    // 3) Toast.makeText(getApplicationContext(), "INSERTED SUCCESSFULLY", Toast.LENGTH_LONG).show();

    // to look at data inside db, click on bottom right Device File Explorer -> data -> look for .db file associated to this project, after u created the db.


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = (VideoView) findViewById(R.id.bgVideoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.study);

        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        //Setup DB
        DB = new DatabaseHelper(this);

        this.getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();

                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, FrontPage.class);
                MainActivity.this.startActivity(myIntent);
                endTime = SystemClock.elapsedRealtime();
                long timeInterval = (SystemClock.elapsedRealtime() - startTime) / 1000;
                dailyDuration += timeInterval;
                Log.e(null, String.format("Session duration: %s", dailyDuration));
                saveDuration();

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String sdate = df.format(date);

                try {
                    Date date2=df.parse(sdate);
                    Log.e(null, String.format("%s", date2.toString() instanceof String));
                    Log.e(null, String.format("%s", Long.toString(dailyDuration) instanceof String));
                    Boolean checkInsertData =  DB.insertUserData(date2.toString(), Long.toString(dailyDuration));
                    if(checkInsertData == true) {
                        Toast.makeText(MainActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(MainActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        if (checkPermission()) {
            javaCameraView = (JavaCameraView)findViewById(R.id.javaCamView);

            if (!OpenCVLoader.initDebug()) {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,baseCallback);
            }
            else {
                try {
                    baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            javaCameraView.setCameraIndex(1);
            javaCameraView.setCvCameraViewListener(this);

        } else {
            requestPermission();
        }
    }

    //MYSQL : START
//    public void insertData(Date date, long TimeFocused) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.COL0, DateToDays(date));
//        contentValues.put(DatabaseHelper.COL1, TimeFocused);
//        long id = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
//        Log.e("return code is : ", String.valueOf(id));
//    }
//
//    public boolean deleteData(Date date) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);
//        return db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COL0 + "=?", new String[]{strDate}) > 0;
//        //returns true if deletion successful
//    }
//
//    public long retrieveData(Date id) {
//
//        String res = "not found";
//        String whereclause = "ID=?";
//        String[] whereargs = new String[]{String.valueOf(id)};
//        Cursor csr = db.query(DatabaseHelper.TABLE_NAME,null,whereclause,whereargs,null,null,null);
//        if (csr.moveToFirst()) {
//            res = csr.getString(csr.getColumnIndex(DatabaseHelper.COL1));
//        }
//
//        if(res == "not found"){
//            return -1;
//        }
//        else{
//            return Long.parseLong(res);
//        }
//    }
//
//    public boolean updateData(Date date, Float TimeFocused) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.COL0, DateToDays(date));
//        contentValues.put(DatabaseHelper.COL1, TimeFocused);
//        //get the date then convert it to String before u can update/delete
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);
//        return db.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.COL0 + "=?", new String[]{strDate}) > 0;
//    }
//
//
//    // magic number=
//    // millisec * sec * min * hours
//    // 1000 * 60 * 60 * 24 = 86400000
//    public static final long MAGIC=86400000L;
//
//    public int DateToDays (Date date){
//        //  convert a date to an integer and back again
//        long currentTime=date.getTime();
//        currentTime=currentTime/MAGIC;
//        return (int) currentTime;
//    }
//
//    public Date DaysToDate(int days) {
//        //  convert integer back again to a date
//        long currentTime=(long) days*MAGIC;
//        return new Date(currentTime);
//    }
    //MYSQL : END


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void saveDuration() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (durations.get(date) == null) {
            durations.put(date, dailyDuration);
        } else {
            durations.put(date, durations.get(date) + dailyDuration);
        }
        Log.e(null, String.format("%s", durations));
    }

    private void showFocusAlert(String message, DialogInterface.OnClickListener okListener) {
        Log.e(null, "ALERT");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setTitle("FocusLab Reminder")
                .setIcon(android.R.drawable.ic_popup_reminder);

        final AlertDialog alert = builder.create();
        alert.show();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1000);
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
            }
        }.start();
        mTimerRunning = true;
        updateWatchInterface();
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }
    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }
    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat();
        mGrey = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGrey.release();

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGrey = inputFrame.gray();

//        FaceEyeDetector faceEyeDetector = new FaceEyeDetector();
//        faceEyeDetector.detect(mRgba);

        //detect face
        MatOfRect faceDetections = new MatOfRect();
        //FaceEyeDetector faceEyeDetector = new FaceEyeDetector();
        int faces = faceEyeDetector.detect(mRgba);
//        faceDetector.detectMultiScale(mRgba, faceDetections);
//
//        for (Rect rect: faceDetections.toArray()) {
//            Imgproc.rectangle(mRgba, new Point(rect.x, rect.y),
//                    new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0,255,0));
//        }

        if (faces == 0) {
            Log.e(null, "NO FACE DETECTED");

            new Thread()
            {
                public void run()
                {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            long timeInterval = (SystemClock.elapsedRealtime() - startTime) / 1000;
                            dailyDuration += timeInterval;
                            showFocusAlert("Go back to work!", null);
                            Log.e(null, String.format("Session duration: %s", dailyDuration));
                            long updatedStartTime = SystemClock.elapsedRealtime();
                            startTime = updatedStartTime;
                        }
                    });
                }
            }.start();

        } else {
            Log.e(null, String.format("Detected %s facial parts", faces));
        }

        return mRgba;
    }

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) throws IOException {

            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
//                    InputStream is =  getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
//                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                    cascFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
//
//                    FileOutputStream fos = new FileOutputStream(cascFile);
//
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//
//                    while((bytesRead = is.read(buffer))!= -1) {
//                        fos.write(buffer, 0, bytesRead);
//                    }
//                    is.close();
//                    fos.close();
//
//                    faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());
                    faceEyeDetector = new FaceEyeDetector(MainActivity.this);

//                    if (faceEyeDetector.faceClassifier.empty() || faceEyeDetector.eyeClassifier.empty()) {
//                        faceEyeDetector = null;
//                    }
//                    else {
//                        cascadeDir.delete();
//                    }
                    javaCameraView.enableView();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}












