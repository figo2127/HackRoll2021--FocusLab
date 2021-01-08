package com.example.opencvproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.sql.Time;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "focuslab.db";
    public static final String TABLE_NAME = "track_table";


//    // COLUMNS
//    public static final String COL0 = "Date";
//    public static final String COL1 = "TimeFocused";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//      DATE: A date. Format: YYYY-MM-DD. The supported range is from '1000-01-01' to '9999-12-31'
        //store date as LONG
        String createTable = "CREATE TABLE " + TABLE_NAME + "(date TEXT PRIMARY KEY, TimeFocused TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Boolean insertUserData(String date, String TimeFocused) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date); //(column name, value)
        contentValues.put("TimeFocused", TimeFocused);
        long result = DB.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean updateUserData(String date, String TimeFocused) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date); //(column name, value)
        contentValues.put("TimeFocused", TimeFocused);
        Cursor cursor = DB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE date = ?", new String[]{date});
        if (cursor.getCount()>0) {
            long result = DB.update(TABLE_NAME, contentValues, "date=?", new String[]{date});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean deleteUserData(String date) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE date = ?", new String[]{date});
        if (cursor.getCount()>0) {
            long result = DB.delete(TABLE_NAME, "date=?", new String[]{date});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

}
