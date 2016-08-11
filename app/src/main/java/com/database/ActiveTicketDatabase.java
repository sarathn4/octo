package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.data.tickets.ActiveTicket;
import com.data.tickets.VerificationCode;

import java.util.ArrayList;

/**
 * Created by sarath on 19/08/15.
 */
public class ActiveTicketDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ActiveTicketDBName.db";
    public static final String ACTIVE_TABLE_NAME = "activetickets";
    public static final String ACTIVE_COLUMN_ID = "id";
    public static final String ACTIVE_COLUMN_NO = "ticket_no";
    public static final String ACTIVE_COLUMN_CODE = "ticket_code";
    public static final String ACTIVE_FROM_STATION = "from_station";
    public static final String ACTIVE_TO_STATION = "to_station";
    public static final String ACTIVE_NO_OF_TICKETS = "no_of_tickets";
    public static final String ACTIVE_TICKET_TYPE = "ticket_type";
    public static final String ACTIVE_TICKET_CATEGORY = "ticket_category";
    public static final String ACTIVE_TICKET_PERIOD = "ticket_period";
    public static final String ACTIVE_TICKET_AMOUNT = "ticket_amount";
    public static final String ACTIVE_PURCHASED_ON = "purchased_date";
    public static final String ACTIVE_ACTIVATED_ON = "activated_date";
    public static final String ACTIVE_ACTIVATED_STATION = "activated_station";
    public static final String ACTIVE_VALID_DATE = "valid_date";

    public ActiveTicketDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table activetickets " +
                        "(id text primary key, ticket_no text, ticket_code text, from_station text, to_station text," +
                        " ticket_type text, no_of_tickets text,ticket_period text, ticket_category text, ticket_amount text, purchased_date text," +
                        "activated_date text, activated_station text, valid_date text,proof_document text,photo text,validated_count text," +
                        "imei_device text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS activetickets");
        onCreate(db);
    }

    public boolean insertActiveTicket(String ticket_id, String ticket_no, String ticket_code,
                                      String from_station, String to_station, String ticket_type,String no_of_tickets,
                                      String ticket_category, String ticket_period, String ticket_amount,
                                      String purchased_date, String activated_date, String activated_station,
                                      String valid_date,String proof_document,String photo,String validated_count,
                                      String imei_device) {
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
        contentValues.put("proof_document",proof_document);
        contentValues.put("photo",photo);
        contentValues.put("validated_count",validated_count);
        contentValues.put("imei_device",imei_device);
        db.insert("activetickets", null, contentValues);
        return true;
    }

    public ArrayList<ActiveTicket> getData(String ticket_type){
        ArrayList<ActiveTicket> activeTicketsList = new ArrayList<ActiveTicket>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res;
            if (ticket_type.equals("ALL"))
                res = db.rawQuery("select * from activetickets", null);
            else
                res = db.rawQuery("select * from activetickets where ticket_type='" + ticket_type + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                ActiveTicket activeTicket = new ActiveTicket();
                activeTicket.setTicket_type(res.getString(res.getColumnIndex(ACTIVE_TICKET_TYPE)));
                activeTicket.setTicket_period(res.getString(res.getColumnIndex(ACTIVE_TICKET_PERIOD)));
                activeTicket.setPurchased_date(res.getString(res.getColumnIndex(ACTIVE_PURCHASED_ON)));
                activeTicket.setActivated_date(res.getString(res.getColumnIndex(ACTIVE_ACTIVATED_ON)));
                activeTicket.setActivated_station(res.getString(res.getColumnIndex(ACTIVE_ACTIVATED_STATION)));
                activeTicket.setFrom_station(res.getString(res.getColumnIndex(ACTIVE_FROM_STATION)));
                activeTicket.setNo_of_tickets(res.getString(res.getColumnIndex(ACTIVE_NO_OF_TICKETS)));
                activeTicket.setTicket_amount(res.getString(res.getColumnIndex(ACTIVE_TICKET_AMOUNT)));
                activeTicket.setTicket_category(res.getString(res.getColumnIndex(ACTIVE_TICKET_CATEGORY)));
                activeTicket.setTo_station(res.getString(res.getColumnIndex(ACTIVE_TO_STATION)));
                activeTicket.setValid_date(res.getString(res.getColumnIndex(ACTIVE_VALID_DATE)));
                activeTicket.setTicket_id(res.getString(res.getColumnIndex(ACTIVE_COLUMN_ID)));
                activeTicket.setTicket_no(res.getString(res.getColumnIndex(ACTIVE_COLUMN_NO)));
                activeTicket.setTicket_code(res.getString(res.getColumnIndex(ACTIVE_COLUMN_CODE)));
                activeTicket.setProof_document(res.getString(res.getColumnIndex("proof_document")));
                activeTicket.setPhoto(res.getString(res.getColumnIndex("photo")));
                activeTicket.setValidated_count(res.getString(res.getColumnIndex("validated_count")));
                activeTicket.setImei_device(res.getString(res.getColumnIndex("imei_device")));
                activeTicketsList.add(activeTicket);
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
        db.execSQL("delete from activetickets");
        db.close();
    }
}