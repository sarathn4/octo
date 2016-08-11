package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RouteDatabase extends SQLiteOpenHelper{
    Mydatabase mydatabase;
    public static final String DATABASE_NAME = "RouteDBName.db";
    public static final String ROUTE_TABLE_NAME = "routes";
    public static final String ROUTE_COLUMN_ID = "id";
    public static final String ROUTE_COLUMN_NAME = "route_name";
    public static final String ROUTE_INTER_STATION = "inter_station";
    public static final String ROUTE_FROM_STATION = "from_station";
    public static final String ROUTE_TO_STATION = "to_station";
    public RouteDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mydatabase = new Mydatabase(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table routes " +
                        "(id text, route_name text, inter_station text, from_station text, to_station text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS routes");
        onCreate(db);
    }

    public boolean insertRoute(String id,String route_name,String inter_station,String from_station,String to_station){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("from_station", from_station);
        contentValues.put("id", id);
        contentValues.put("route_name", route_name);
        contentValues.put("inter_station", inter_station);
        contentValues.put("to_station", to_station);

        db.insert("routes", null, contentValues);
        return true;
    }

    public boolean hasStation(String to_station,String from_station,String activation_station){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from routes where from_station ='" + from_station + "'& to_station='" + to_station +
                    "'", null);
            res.moveToFirst();

            StringBuilder intermediate_stations = new StringBuilder(res.getString(res.getColumnIndex(ROUTE_INTER_STATION)));

            ArrayList<String> interstations_id = new ArrayList<>();

            while (!(intermediate_stations.equals(""))) {
                int index = intermediate_stations.indexOf("::");
                if (index != -1) {
                    interstations_id.add(intermediate_stations.substring(0, index));
                    String temp = intermediate_stations.substring(index + 2, intermediate_stations.length());
                    intermediate_stations = new StringBuilder(temp);
                } else {
                    interstations_id.add(intermediate_stations.toString());
                    break;
                }
            }
            for (String id : interstations_id) {
                if (activation_station.equals(mydatabase.getStationName(id)))
                    return true;
            }
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
