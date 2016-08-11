package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.ExpiredTicket;
import com.data.tickets.VolleyRequestData;
import com.database.Mydatabase;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpiredTicketView extends Home {

    public static final String TAG = "SendMail";
    TextView textView_ticketno, textView_date, textView_user, textView_source, textView_destination,
            textView_no_of_tickets, textView_purchaseddate, textView_validdate, textView_period, textView_category;
    Button send_mail;

    ExpiredTicket expiredTicket;
    Mydatabase mydatabase;
    SharedPreferences sharedPreferences;
    ImageView imageView_route;
    LinearLayout layout_ticket_type;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    PopupWindow popupWindow;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired_ticket_view);
        super.onCreateDrawer();
        mydatabase = new Mydatabase(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);

        expiredTicket = getIntent().getParcelableExtra("expiredtickets");
        textView_ticketno = (TextView) findViewById(R.id.ticketView_ticketno);
        textView_date = (TextView) findViewById(R.id.ticketView_date);
        textView_user = (TextView) findViewById(R.id.ticketView_name);
        textView_source = (TextView) findViewById(R.id.ticketView_source);
        textView_destination = (TextView) findViewById(R.id.ticketView_destination);
        textView_purchaseddate = (TextView) findViewById(R.id.ticketView_purchased_date);
        textView_validdate = (TextView) findViewById(R.id.ticketView_valid_date);
        textView_no_of_tickets = (TextView) findViewById(R.id.ticketView_no_of_tickets);
        textView_period = (TextView) findViewById(R.id.ticketView_period);
        textView_category = (TextView) findViewById(R.id.ticketView_category);
        send_mail = (Button) findViewById(R.id.button_send_mail);

        layout_ticket_type = (LinearLayout) findViewById(R.id.layout_ticket_type);
        imageView_route = (ImageView) findViewById(R.id.ticketView_route);
        textView_ticketno.setText(expiredTicket.getTicket_no().toString());
        textView_purchaseddate.setText(getSplitDateAndTime(expiredTicket.getPurchased_date()));
        textView_date.setText(getSplitDateAndTime(expiredTicket.getPurchased_date()));
        textView_validdate.setText(getSplitDateAndTime(expiredTicket.getValid_date()));
        textView_no_of_tickets.setText(expiredTicket.getNo_of_tickets() + " Tickets");
        textView_source.setText(mydatabase.getStationName(expiredTicket.getFrom_station()));
        textView_user.setText(sharedPreferences.getString("user_name", "User"));
        if (expiredTicket.getTicket_type().equals("Platform")) {
            LinearLayout destinationlinearLayout = (LinearLayout) findViewById(R.id.ticketView_destination_layout);
            destinationlinearLayout.setVisibility(View.INVISIBLE);
            imageView_route.setVisibility(View.INVISIBLE);
            textView_category.setVisibility(View.GONE);
            textView_period.setVisibility(View.GONE);
        } else
            textView_destination.setText(mydatabase.getStationName(expiredTicket.getTo_station()));
        if (expiredTicket.getTicket_type().equals("Platform"))
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_pf));
        else if (expiredTicket.getTicket_type().equals("Unreserved Ticket")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.GONE);
            textView_category.setText(expiredTicket.getTicket_category());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_uts));
        } else if (expiredTicket.getTicket_type().equals("MonthlyPass")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.VISIBLE);
            textView_category.setText(expiredTicket.getTicket_category());
            textView_period.setText(expiredTicket.getTicket_period());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_st));
        }

        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SendMail().execute();
                sendMail();
            }
        });
    }

//    class SendMail extends AsyncTask<Void, Void, Void> {
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 40);
//        }
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "sendTicketEmail"));
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//                params.add(new BasicNameValuePair("ticketnumber", expiredTicket.getTicket_no()));
//
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                httpResponse = httpClient.execute(httpPost);
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Response", result);
//
//                jsonObject = new JSONObject(result);
//                message = jsonObject.getString("message");
//                Log.e("Check", message);
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
//            popupWindow.dismiss();
//        }
//    }

    public void sendMail() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    Log.e("Check", message);
                    pDialog.hide();
                    notifyUser(message+"");
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.hide();
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "sendTicketEmail");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("ticketnumber",expiredTicket.getTicket_no()+"");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public String getSplitDateAndTime(String date_time) {
        String splitDateAndTime = "";
        int index = date_time.indexOf(" ");
        String date = date_time.substring(0, index);
        String time = date_time.substring(index + 1, date_time.length());

        splitDateAndTime = changeDateFormat(date.trim()) + "\n" + changeTimeFormat(time.trim());
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

}
