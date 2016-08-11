package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.data.tickets.VerificationCode;

import java.util.ArrayList;

public class VerificationCodeDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VCDBName.db";
    public static final String VC_TABLE_NAME = "verificationcode";
    public static final String VC_COLUMN_ID = "id";
    public static final String VC_COLUMN_NO = "ticket_no";
    public static final String VC_COLUMN_CODE = "ticket_code";
    public static final String VC_FROM_STATION = "from_station";
    public static final String VC_TO_STATION = "to_station";
    public static final String VC_TICKET_TYPE = "ticket_type";
    public static final String VC_NO_OF_TICKETS = "no_of_tickets";
    public static final String VC_TICKET_AMOUNT = "ticket_amount";
    public static final String VC_PURCHASED_ON = "purchased_date";
    public static final String VC_TICKET_PERIOD = "ticket_period";
    public static final String VC_TICKET_CATEGORY = "ticket_category";

    public VerificationCodeDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table verificationcode " +
                        "(id text  primary key, ticket_no text, ticket_code text, from_station text, to_station text," +
                        " ticket_type text,no_of_tickets text, ticket_period text, ticket_category text, ticket_amount text, purchased_date text" +
                        ",proof_document text,photo text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS verificationcode");
        onCreate(db);
    }

    public boolean insertStation(String code_id,String ticket_no,String verification_code,
                                 String from_station_name, String to_station_name,String ticket_type,
                                 String no_of_tickets, String category,String ticket_period,
                                 String amount,String purchased_date,String proof_document,String photo){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("from_station", from_station_name);
            contentValues.put("id", code_id);
            contentValues.put("ticket_code", verification_code);
            contentValues.put("ticket_no", ticket_no);
            contentValues.put("to_station", to_station_name);
            contentValues.put("ticket_type", ticket_type);
            contentValues.put("no_of_tickets", no_of_tickets);
            contentValues.put("ticket_category", category);
            contentValues.put("ticket_period", ticket_period);
            contentValues.put("ticket_amount", amount);
            contentValues.put("purchased_date", purchased_date);
            contentValues.put("proof_document",proof_document);
            contentValues.put("photo",photo);

            db.insert("verificationcode", null, contentValues);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<VerificationCode> getData(String ticket_type){
        ArrayList<VerificationCode> verificationCodesList = new ArrayList<VerificationCode>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res;
            if (ticket_type.equals("ALL"))
                res = db.rawQuery("select * from verificationcode", null);
            else
                res = db.rawQuery("select * from verificationcode where ticket_type='" + ticket_type + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                VerificationCode verificationCode = new VerificationCode();
                verificationCode.setVerification_code(res.getString(res.getColumnIndex(VC_COLUMN_CODE)));
                verificationCode.setAmount(res.getString(res.getColumnIndex(VC_TICKET_AMOUNT)));
                verificationCode.setCategory(res.getString(res.getColumnIndex(VC_TICKET_CATEGORY)));
                verificationCode.setCode_id(res.getString(res.getColumnIndex(VC_COLUMN_ID)));
                verificationCode.setNo_of_tickets(res.getString(res.getColumnIndex(VC_NO_OF_TICKETS)));
                verificationCode.setFrom_station_name(res.getString(res.getColumnIndex(VC_FROM_STATION)));
                verificationCode.setTo_station_name(res.getString(res.getColumnIndex(VC_TO_STATION)));
                verificationCode.setPurchased_date(res.getString(res.getColumnIndex(VC_PURCHASED_ON)));
                verificationCode.setTicket_period(res.getString(res.getColumnIndex(VC_TICKET_PERIOD)));
                verificationCode.setTicket_type(res.getString(res.getColumnIndex(VC_TICKET_TYPE)));
                verificationCode.setProof_document(res.getString(res.getColumnIndex("proof_document")));
                verificationCode.setPhoto(res.getString(res.getColumnIndex("photo")));

                verificationCodesList.add(verificationCode);
                res.moveToNext();
            }
            return verificationCodesList;
        }
        catch(Exception e){
            e.printStackTrace();
            return verificationCodesList;
        }
    }

    public void deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from verificationcode");
        db.close();
    }

    public void deleteVerificationCode(String code_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from verificationcode where id='" + code_id + "'");
        db.close();
    }
}