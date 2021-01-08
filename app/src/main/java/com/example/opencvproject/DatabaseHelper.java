package com.example.opencvproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Stats.db";
    public static final String TABLE_NAME = "track_table";


    // COLUMNS
    public static final String COL0 = "Date";
    public static final String COL1 = "TimeFocused";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//      DATE: A date. Format: YYYY-MM-DD. The supported range is from '1000-01-01' to '9999-12-31'
        //store date as LONG
        String createTable = "CREATE TABLE " + TABLE_NAME + "(Date INTEGER PRIMARY KEY, TimeFocused FLOAT(23))";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



//    public boolean addData(String item) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL1, item);
//        long result = db.insert(TABLE_NAME, null, contentValues);
//
//        //if data inserted wrongly return -1
//        if (result == -1) {
//            return false;
//        }
//        else {
//            return true;
//        }
//    }


//    public Cursor getData() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_NAME;
//        Cursor data = db.rawQuery(query, null);
//        return data;
//
//        //to obtain data into an arraylist:
//        // Cursor data = mDatabaseHelper.getData();
//        // ArrayList<String> listData = new ArrayList<>();
//        // while (data.moveToNext()) {
//        //    listData.add(data.getString(1));
//        // }
//
//    }
}
