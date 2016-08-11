package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.data.tickets.VerificationCode;
import com.database.Mydatabase;
import com.trail.octo.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sarath on 18/08/15.
 */
public class VerificationCodeListAdapter extends BaseAdapter {
    Mydatabase mydatabase;
    ArrayList<VerificationCode> verificationCodes;
    Context context;

    public VerificationCodeListAdapter(Context context, ArrayList<VerificationCode> verificationCodes) {
        this.context = context;
        this.verificationCodes = verificationCodes;
        mydatabase = new Mydatabase(context);
    }

    @Override
    public int getCount() {
        return verificationCodes.size();
    }

    @Override
    public Object getItem(int position) {
        return verificationCodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_filipper_ticket_view, null);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.view_flipper_layout);
        LinearLayout destinationlayout = (LinearLayout) view.findViewById(R.id.view_flipper_layout_destination);
        VerificationCode verificationCode = (VerificationCode) getItem(position);

        if (verificationCode.getTicket_type().equals("Platform")) {
            linearLayout.setBackground(context.getResources().getDrawable(R.drawable.flipper_ticket_pf));
            destinationlayout.setVisibility(View.INVISIBLE);
        } else if (verificationCode.getTicket_type().equals("MonthlyPass")) {
            linearLayout.setBackground(context.getResources().getDrawable(R.drawable.flipper_ticket_st));
            destinationlayout.setVisibility(View.VISIBLE);
            TextView textView_destination = (TextView) view.findViewById(R.id.flipper_textView_destination);
            textView_destination.setText(mydatabase.getStationName(verificationCode.getTo_station_name()));
        } else if (verificationCode.getTicket_type().equals("Unreserved Ticket")) {
            linearLayout.setBackground(context.getResources().getDrawable(R.drawable.flipper_ticket_uts));
            destinationlayout.setVisibility(View.VISIBLE);
            TextView textView_destination = (TextView) view.findViewById(R.id.flipper_textView_destination);
            textView_destination.setText(mydatabase.getStationName(verificationCode.getTo_station_name()));
        }
        TextView textView_ticket_no = (TextView) view.findViewById(R.id.flipper_textView_ticketno);
        TextView textView_station = (TextView) view.findViewById(R.id.flipper_textView_source);
        TextView textView_date = (TextView) view.findViewById(R.id.flipper_textView_ticket_date);
        TextView textView_no_of_tickets = (TextView) view.findViewById(R.id.flipper_textView_no_of_tickets);
        if(verificationCode.getNo_of_tickets().equals("1")){
            textView_no_of_tickets.setText(verificationCode.getNo_of_tickets() + " TICKET");
        } else {
            textView_no_of_tickets.setText(verificationCode.getNo_of_tickets() + " TICKETS");
        }
        textView_ticket_no.setText(verificationCode.getVerification_code());
        textView_station.setText(mydatabase.getStationName(verificationCode.getFrom_station_name()));
        textView_date.setText(getDateAndTime(verificationCode.getPurchased_date()));

//        LayoutInflater layoutInflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.verification_code_list_item, null);
//        TextView code,icon_source,icon_destination;
//
//        code = (TextView) view.findViewById(R.id.textView_code);
//        icon_source = (TextView) view.findViewById(R.id.textView_source);
//        icon_destination = (TextView) view.findViewById(R.id.textView_destination);
//
//        VerificationCode verificationCode = (VerificationCode) getItem(position);
//        code.setText(verificationCode.getVerification_code());
//        icon_source.setText(mydatabase.getStationName(verificationCode.getFrom_station_name()));
//        icon_destination.setText(verificationCode.getTo_station_name());

        return view;
    }

    public String getDateAndTime(String date_time) {
        String splitDateAndTime = "";
        int index = date_time.indexOf(" ");
        String date = date_time.substring(0, index);
        String time = date_time.substring(index + 1, date_time.length());

        splitDateAndTime = changeDateFormat(date.trim()) + "|" + changeTimeFormat(time.trim());
        return splitDateAndTime;
    }

    public String changeDateFormat(String old_date) {
        String formatted_date = "";
        DateFormat writeFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = readFormat.parse(old_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_date = writeFormat.format(date);
        }
        return formatted_date;
    }

    public String changeTimeFormat(String old_time) {
        String formatted_time = "";
        DateFormat writeFormat = new SimpleDateFormat("hh:mm:ss aa");
        DateFormat readFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = readFormat.parse(old_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_time = writeFormat.format(date);
        }
        return formatted_time.toUpperCase();
    }
    public void dataSetChanges(ArrayList<VerificationCode> verificationCodes){
        this.verificationCodes = verificationCodes;
        this.notifyDataSetChanged();
    }

}
