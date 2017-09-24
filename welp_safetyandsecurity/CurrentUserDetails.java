package com.abomicode.welp_safetyandsecurity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rohan on 6/17/2017.
 */

public class CurrentUserDetails extends SQLiteOpenHelper {

    static String tableName = "mydb";
    static int version = 100;
    Context context;

    String keyField = "key";

    String tableCreate = "create table " + tableName + "(" + keyField + " varchar(50))";

    public CurrentUserDetails(Context context) {
        super(context, tableName, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + tableName + "'", null);
        if(cursor.getCount()!=1){
            db.execSQL(tableCreate);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void updateKey(String key){

        String []columns = {keyField};

        Cursor cursor = getReadableDatabase().query(true,tableName,columns,null,null,null,null,null,null);

        if(cursor.getCount()==1){
            //Update the key data.
            ContentValues contentValues = new ContentValues();
            contentValues.put(keyField,key);
            getWritableDatabase().update(tableName,contentValues,null,null);
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(keyField,key);
            getWritableDatabase().insert(tableName,null,contentValues);
        }
    }

    public String getKey(){

        String []columns = {keyField};

        Cursor cursor = getReadableDatabase().query(true,tableName,columns,null,null,null,null,null,null);

        String key = new String();

        while(cursor.moveToNext()){
            key = cursor.getString(cursor.getColumnIndex(keyField));
        }
        return key;
    }

}
