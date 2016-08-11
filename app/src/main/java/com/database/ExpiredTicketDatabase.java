package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.data.tickets.ActiveTicket;
import com.data.tickets.ExpiredTicket;

import java.util.ArrayList;


public class ExpiredTicketDatabase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "ExpiredTicketDBName.db";
    public static final String EXPIRED_TABLE_NAME = "expiredtickets";
    public static final String EXPIRED_COLUMN_ID = "id";
    public static final String EXPIRED_COLUMN_NO = "ticket_no";
    public static final String EXPIRED_COLUMN_CODE = "ticket_code";
    public static final String EXPIRED_FROM_STATION = "from_station";
    public static final String EXPIRED_TO_STATION = "to_station";
    public static final String EXPIRED_TICKET_TYPE = "ticket_type";
    public static final String EXPIRED_NO_OF_TICKETS = "no_of_tickets";
    public static final String EXPIRED_TICKET_CATEGORY = "ticket_category";
    public static final String EXPIRED_TICKET_PERIOD = "ticket_period";
    public static final String EXPIRED_TICKET_AMOUNT = "ticket_amount";
    public static final String EXPIRED_PURCHASED_ON = "purchased_date";
    public static final String EXPIRED_ACTIVATED_ON = "activated_date";
    public static final String EXPIRED_ACTIVATED_STATION = "activated_station";
    public static final String EXPIRED_VALID_DATE = "valid_date";
    public static final String EXPIRED_EXPIRED_DATE = "expired_date";
    public ExpiredTicketDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table expiredtickets " +
                        "(id text primary key, ticket_no text, ticket_code text, from_station text, to_station text," +
                        " ticket_type text,no_of_tickets text, ticket_period text, ticket_category text, ticket_amount text, purchased_date text," +
                        "activated_date text, activated_station text, valid_date text, expired_date text,proof_document text,photo text," +
                        "imei_device text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expiredtickets");
        onCreate(db);
    }
    public boolean insertExpiredTicket(String ticket_id, String ticket_no, String ticket_code,
                                      String from_station, String to_station, String ticket_type,String no_of_tickets,
                                      String ticket_category, String ticket_period, String ticket_amount,
                                      String purchased_date, String activated_date, String activated_station,
                                      String valid_date, String expired_date,String proof_document,String photo,String imei_device) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("from_station", from_station);
        contentValues.put("id", ticket_id);
        contentValues.put("ticket_code", ticket_code);
        contentValues.put("ticket_no", ticket_no);
        contentValues.put("to_station", to_station);
        contentValues.put("ticket_type", ticket_type);
        contentValues.put("no_of_tickets",no_of_tickets);
        contentValues.put("ticket_category", ticket_category);
        contentValues.put("ticket_period", ticket_period);
        contentValues.put("ticket_amount", ticket_amount);
        contentValues.put("purchased_date", purchased_date);
        contentValues.put("activated_date", activated_date);
        contentValues.put("activated_station", activated_station);
        contentValues.put("valid_date", valid_date);
        contentValues.put("expired_date",expired_date);
        contentValues.put("proof_document",proof_document);
        contentValues.put("imei_device",imei_device);
        contentValues.put("photo",photo);

        db.insert("expiredtickets", null, contentValues);
        return true;
    }
    public ArrayList<ExpiredTicket> getData(String ticket_type){
        ArrayList<ExpiredTicket> activeTicketsList = new ArrayList<ExpiredTicket>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from expiredtickets where ticket_type='" + ticket_type + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                ExpiredTicket expiredTicket = new ExpiredTicket();
                expiredTicket.setTicket_type(res.getString(res.getColumnIndex(EXPIRED_TICKET_TYPE)));
                expiredTicket.setTicket_period(res.getString(res.getColumnIndex(EXPIRED_TICKET_PERIOD)));
                expiredTicket.setPurchased_date(res.getString(res.getColumnIndex(EXPIRED_PURCHASED_ON)));
                expiredTicket.setActivated_date(res.getString(res.getColumnIndex(EXPIRED_ACTIVATED_ON)));
                expiredTicket.setActivated_station(res.getString(res.getColumnIndex(EXPIRED_ACTIVATED_STATION)));
                expiredTicket.setFrom_station(res.getString(res.getColumnIndex(EXPIRED_FROM_STATION)));
                expiredTicket.setNo_of_tickets(res.getString(res.getColumnIndex(EXPIRED_NO_OF_TICKETS)));
                expiredTicket.setTicket_amount(res.getString(res.getColumnIndex(EXPIRED_TICKET_AMOUNT)));
                expiredTicket.setTicket_category(res.getString(res.getColumnIndex(EXPIRED_TICKET_CATEGORY)));
                expiredTicket.setTo_station(res.getString(res.getColumnIndex(EXPIRED_TO_STATION)));
                expiredTicket.setValid_date(res.getString(res.getColumnIndex(EXPIRED_VALID_DATE)));
                expiredTicket.setTicket_id(res.getString(res.getColumnIndex(EXPIRED_COLUMN_ID)));
                expiredTicket.setTicket_no(res.getString(res.getColumnIndex(EXPIRED_COLUMN_NO)));
                expiredTicket.setTicket_code(res.getString(res.getColumnIndex(EXPIRED_COLUMN_CODE)));
                expiredTicket.setExpired_date(res.getString(res.getColumnIndex(EXPIRED_EXPIRED_DATE)));
                expiredTicket.setProof_document(res.getString(res.getColumnIndex("proof_document")));
                expiredTicket.setImei_device(res.getString(res.getColumnIndex("imei_device")));
                expiredTicket.setPhoto(res.getString(res.getColumnIndex("photo")));

                activeTicketsList.add(expiredTicket);
                res.moveToNext();
            }
            return activeTicketsList;
        }
        catch(Exception e){
            e.printStackTrace();
            return activeTicketsList;
        }
    }
    public void deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from expiredtickets");
        db.close();
    }
}
