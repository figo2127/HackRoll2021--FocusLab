package com.example.opencvproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "track_table";
    private static final String COL0 = "date";
    private static final String COL1 = "time focused";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (date INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + "FLOAT(23));";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data inserted wrongly return -1
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }


    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;

        //to obtain data into an arraylist:
        // Cursor data = mDatabaseHelper.getData();
        // ArrayList<String> listData = new ArrayList<>();
        // while (data.moveToNext()) {
        //    listData.add(data.getString(1));
        // }

    }
}
