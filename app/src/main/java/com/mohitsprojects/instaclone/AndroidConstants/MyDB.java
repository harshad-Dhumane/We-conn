package com.mohitsprojects.instaclone.AndroidConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDB {

    private Database dbHelper;

    private SQLiteDatabase database;

    public final static String EMP_TABLE="MyEmployees"; // name of table


    public final static String EMP_ID="_id"; // id value for employee
    public final static String EMP_NAME="name";  // name of employee
    public final static String EMP_TEXT="mtext";  // name of employee

    /**
     *
     * @param context
     */
    public MyDB(Context context){
        dbHelper = new Database(context);
        database = dbHelper.getWritableDatabase();
    }


    public long createRecords( String name,String text){
        ContentValues values = new ContentValues();
        values.put(EMP_NAME, name);
        values.put(EMP_TEXT, text);
        return database.insert(EMP_TABLE, null, values);
    }

    public Cursor selectRecords() {
        String[] cols = new String[] {EMP_ID,EMP_NAME,EMP_TEXT};
        Cursor mCursor = database.query(
                EMP_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null              // The sort order
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }
}
