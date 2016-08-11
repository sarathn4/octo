package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class Mydatabase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String STATION_TABLE_NAME = "stations";
    public static final String STATION_COLUMN_ID = "id";
    public static final String STATION_COLUMN_NAME = "name";
    public static final String STATION_COLUMN_CODE = "code";

    public Mydatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table stations " +
                        "(id text primary key, name text,code text)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS stations");
        onCreate(db);
    }

    public boolean insertStation(String station_id,String station_name,String station_code){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", station_name);
        contentValues.put("id", station_id);
        contentValues.put("code", station_code);
        db.insert("stations", null, contentValues);
        return true;
    }

    public ArrayList<String> getStationsList(){
        try {
            ArrayList<String> stations_list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from stations", null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                stations_list.add(res.getString(res.getColumnIndex(STATION_COLUMN_NAME)));
                res.moveToNext();
            }
            return stations_list;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public String getData(String station_name){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from stations where name='" + station_name + "'", null);
            res.moveToFirst();
            Log.e("CountCheck", res.getCount() + "");
            String id = res.getString(res.getColumnIndex(STATION_COLUMN_ID));
            Log.e("IDCheck", id);
            return id;
        }
        catch(Exception e){
            e.printStackTrace();
            return "0";
        }
    }

    public String getStationName(String id){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from stations where id='" + id + "'", null);
            res.moveToFirst();
            String name = res.getString(res.getColumnIndex(STATION_COLUMN_NAME));
            return name;
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
