package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.ActiveTicket;
import com.data.tickets.VolleyRequestData;
import com.database.Mydatabase;
import com.google.zxing.BarcodeFormat;
import com.qrcode.QRCodeEncoder;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveTicketView extends Home {

    private static final String TAG = "ActiveTicketView";
    private static final String volleyTag = "ActiveTicketView";

    TextView textView_ticketno, textView_date, textView_user, textView_source, textView_destination,
            textView_no_of_tickets, textView_purchaseddate, textView_validdate, textView_period, textView_category;
    ImageView imageView_qrcode, imageView_route;
    LinearLayout layout_ticket_type;
    RelativeLayout ticket_layout_days, ticket_layout_hours, ticket_layout_mins, ticket_layout_secs,
            ticket_layout_millisecs;
    TextView ticket_textView_day, ticket_textView_hour, ticket_textView_min, ticket_textView_sec,
            ticket_textView_millisec;
    Bitmap pan_card = null, driving_license = null, aadhar_card = null, voter_id = null, passport = null;
    Bitmap empty_image = null;
    Bitmap current_image = null;
    Bitmap bitmap_qr_code;
    Button send_mail;
    Mydatabase mydatabase;
    ActiveTicket activeTicket;
    SharedPreferences sharedPreferences;
    //    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    String color_code = "";
    String secret_code = "";
    int smallerDimension;

    ImageView imageView_document;
    Spinner spinner;
    PopupWindow popupWindow, popupWindow_image;
    View view, view_image;
    SharedPreferences sharedPreferences_docs;
    ImageView imageView_profile;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_ticket_view);
        super.onCreateDrawer();
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        sharedPreferences_docs = getSharedPreferences("docs_data", MODE_PRIVATE);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);
        activeTicket = getIntent().getParcelableExtra("activeticket");

        mydatabase = new Mydatabase(this);

        imageView_profile = (ImageView) findViewById(R.id.image_profile);
        imageView_document = (ImageView) findViewById(R.id.imageView_document);
        spinner = (Spinner) findViewById(R.id.spinner_doc);
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
        imageView_qrcode = (ImageView) findViewById(R.id.ticketView_qrcode);
        imageView_route = (ImageView) findViewById(R.id.ticketView_route);
        layout_ticket_type = (LinearLayout) findViewById(R.id.layout_ticket_type);
        send_mail = (Button) findViewById(R.id.button_send_mail);

        ticket_layout_days = (RelativeLayout) findViewById(R.id.ticket_layout_days);
        ticket_layout_hours = (RelativeLayout) findViewById(R.id.ticket_layout_hours);
        ticket_layout_mins = (RelativeLayout) findViewById(R.id.ticket_layout_mins);
        ticket_layout_secs = (RelativeLayout) findViewById(R.id.ticket_layout_secs);
        ticket_layout_millisecs = (RelativeLayout) findViewById(R.id.ticket_layout_millisecs);

        ticket_textView_day = (TextView) findViewById(R.id.ticket_textView_day);
        ticket_textView_hour = (TextView) findViewById(R.id.ticket_textView_hour);
        ticket_textView_min = (TextView) findViewById(R.id.ticket_textView_min);
        ticket_textView_sec = (TextView) findViewById(R.id.ticket_textView_sec);
        ticket_textView_millisec = (TextView) findViewById(R.id.ticket_textView_millisec);
        byte[] decodedString;
        String encodedmessage = "";
        empty_image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_ticket_doc);

        encodedmessage = sharedPreferences_docs.getString("pancard", "Empty");

        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            Log.e("Pancard", "NotEmpty");
            pan_card = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences_docs.getString("drivinglicense", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            driving_license = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences_docs.getString("voterid", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            voter_id = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences_docs.getString("aadharcard", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            aadhar_card = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences_docs.getString("passport", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            passport = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("" + activeTicket.getProof_document());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_title_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_list_item);

        spinner.setAdapter(arrayAdapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                switch (i){
//                    case 0:
//                        if(pan_card!=null) {
//                            Log.e("PANCARD","Selected");
//                            imageView_document.setImageBitmap(pan_card);
//                            current_image = pan_card;
//                        }
//                        else {
//                            imageView_document.setImageBitmap(empty_image);
//                            current_image = null;
//                        }
//                        break;
//                    case 1:
//                        if(driving_license!=null) {
//                            imageView_document.setImageBitmap(driving_license);
//                            current_image = driving_license;
//                        }
//                        else {
//                            imageView_document.setImageBitmap(empty_image);
//                            current_image = null;
//                        }
//                        break;
//                    case 2:
//                        if(aadhar_card!=null) {
//                            imageView_document.setImageBitmap(aadhar_card);
//                            current_image = aadhar_card;
//                        }
//                        else {
//                            imageView_document.setImageBitmap(empty_image);
//                            current_image = null;
//                        }
//                        break;
//                    case 3:
//                        if(voter_id!=null) {
//                            imageView_document.setImageBitmap(voter_id);
//                            current_image = voter_id;
//                        }
//                        else {
//                            imageView_document.setImageBitmap(empty_image);
//                            current_image = null;
//                        }
//                        break;
//                    case 4:
//                        if(passport!=null) {
//                            imageView_document.setImageBitmap(passport);
//                            current_image = passport;
//                        }
//                        else {
//                            imageView_document.setImageBitmap(empty_image);
//                            current_image = null;
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        switch (activeTicket.getProof_document()) {
            case "PAN Card":
                if (pan_card != null) {
                    Log.e("PANCARD", "Selected");
                    imageView_document.setImageBitmap(pan_card);
                    current_image = pan_card;
                } else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
                break;
            case "Aadhar Card":
                if (aadhar_card != null) {
                    imageView_document.setImageBitmap(aadhar_card);
                    current_image = aadhar_card;
                } else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
                break;
            case "Driving License":
                if (driving_license != null) {
                    imageView_document.setImageBitmap(driving_license);
                    current_image = driving_license;
                } else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
                break;
            case "Passport":
                if (passport != null) {
                    imageView_document.setImageBitmap(passport);
                    current_image = passport;
                } else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
                break;
            case "Voter ID":
                if (voter_id != null) {
                    imageView_document.setImageBitmap(voter_id);
                    current_image = voter_id;
                } else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
                break;

            default:
                imageView_document.setImageBitmap(empty_image);
                current_image = null;
        }

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int dimension = width < height ? width : height;
        smallerDimension = dimension * 7 / 8;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String dateInString = "7-Jun-2013";
        long millisecs = 0;
        Calendar calendar = Calendar.getInstance();
        Date current_date = calendar.getTime();
        try {
            Date valid_date = formatter.parse(activeTicket.getValid_date());
            millisecs = valid_date.getTime() - current_date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(millisecs, 100);

        textView_ticketno.setText(activeTicket.getTicket_no().toString());
        textView_purchaseddate.setText(getSplitDateAndTime(activeTicket.getActivated_date()));

        textView_date.setText(getSplitDateAndTime(activeTicket.getPurchased_date()));
        textView_validdate.setText(getSplitDateAndTime(activeTicket.getValid_date()));
        textView_no_of_tickets.setText(activeTicket.getNo_of_tickets() + " Tickets");
        textView_source.setText(mydatabase.getStationName(activeTicket.getFrom_station()));
        textView_user.setText(sharedPreferences.getString("user_name", "User"));
        if (activeTicket.getTicket_type().equals("Platform")) {
            LinearLayout destinationlinearLayout = (LinearLayout) findViewById(R.id.ticketView_destination_layout);
            destinationlinearLayout.setVisibility(View.INVISIBLE);
            imageView_route.setVisibility(View.INVISIBLE);
            textView_category.setVisibility(View.GONE);
            textView_period.setVisibility(View.GONE);
        } else
            textView_destination.setText(mydatabase.getStationName(activeTicket.getTo_station()));

        if (activeTicket.getTicket_type().equals("Platform"))
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_pf));
        else if (activeTicket.getTicket_type().equals("Unreserved Ticket")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.GONE);
            textView_category.setText(activeTicket.getTicket_category());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_uts));
        } else if (activeTicket.getTicket_type().equals("MonthlyPass")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.VISIBLE);
            textView_category.setText(activeTicket.getTicket_category());
            textView_period.setText(activeTicket.getTicket_period());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_st));
        }

        imageView_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_image != null) {

                    LayoutInflater inflater = (LayoutInflater) getBaseContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view_image = inflater.inflate(R.layout.popup_touch_image, null);
                    TouchImageView touchImageView;
                    touchImageView = (TouchImageView) view_image.findViewById(R.id.touch_imageview);
                    popupWindow_image = new PopupWindow(view_image, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT, true);
                    popupWindow_image.setFocusable(false);
                    popupWindow_image.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow_image.setOutsideTouchable(true);
                    touchImageView.setImageBitmap(current_image);
                    popupWindow_image.showAtLocation(view_image, Gravity.CENTER, 0, 40);
                }
            }
        });
        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        //new GetSecretCode().execute();
        myCountDownTimer.start();

        Log.e("Photo", "Data:" + activeTicket.getPhoto());
        if (!activeTicket.getPhoto().equals("")) {
            byte[] imageData = Base64.decode(activeTicket.getPhoto(), Base64.DEFAULT);
            final Bitmap bitmap_profile = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            imageView_profile.setImageBitmap(bitmap_profile);
            imageView_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bitmap_profile != null) {

                        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view_image = inflater.inflate(R.layout.popup_touch_image, null);
                        TouchImageView touchImageView;
                        touchImageView = (TouchImageView) view_image.findViewById(R.id.touch_imageview);
                        popupWindow_image = new PopupWindow(view_image, ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT, true);
                        popupWindow_image.setFocusable(false);
                        popupWindow_image.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow_image.setOutsideTouchable(true);
                        touchImageView.setImageBitmap(bitmap_profile);
                        popupWindow_image.showAtLocation(view_image, Gravity.CENTER, 0, 40);
                    }
                }
            });
        }
        imageView_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap_qr_code != null) {
                    LayoutInflater inflater = (LayoutInflater) getBaseContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view_image = inflater.inflate(R.layout.popup_touch_image, null);
                    TouchImageView touchImageView;
                    touchImageView = (TouchImageView) view_image.findViewById(R.id.touch_imageview);
                    popupWindow_image = new PopupWindow(view_image, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT, true);
                    popupWindow_image.setFocusable(false);
                    popupWindow_image.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow_image.setOutsideTouchable(true);
                    touchImageView.setImageBitmap(bitmap_qr_code);
                    touchImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (popupWindow_image.isShowing()) {
                                popupWindow_image.dismiss();
                            }
                        }
                    });
                    popupWindow_image.showAtLocation(view_image, Gravity.CENTER, 0, 40);
                }
            }
        });

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

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.e("DeviceIMEI", telephonyManager.getDeviceId());
                Log.e("TicketIMEI", activeTicket.getImei_device());
                getSecretCode();

            }
        }, 500);
    }

//    class GetSecretCode extends AsyncTask<Void, Void, Void> {
//
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
//                Calendar calendar = Calendar.getInstance();
//                String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getSecretData"));
//                params.add(new BasicNameValuePair("user_id", sharedPreferences.getString("user_id", "0")));
//                params.add(new BasicNameValuePair("forTime", time));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
//                color_code = responseInfo.getString("colorcode");
//                secret_code = responseInfo.getString("secretcode");
//                QRCodeEncoder qrCodeEncoder;
//                String data = secret_code + " " + activeTicket.getTicket_id() + " " +
//                        sharedPreferences.getString("passenger_id", "0") + " " +
//                        encryptData(sharedPreferences.getString("user_name", "User").toUpperCase()) + " " + "04/12/1992";
////                String data = secret_code + " " + activeTicket.getTicket_id() + " " +
////                        sharedPreferences.getString("passenger_id", "0") + " " +
////                        (sharedPreferences.getString("user_name", "User")) + " " + "04/12/1992";
//
//                Log.e("QRCodeData", data);
//                qrCodeEncoder = new QRCodeEncoder(data, null,
//                        "TEXT_TYPE", BarcodeFormat.QR_CODE.toString(), smallerDimension);
//
//                bitmap_qr_code = qrCodeEncoder.encodeAsBitmap();
//                imageView_qrcode.setImageBitmap(bitmap_qr_code);
//                popupWindow.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//                popupWindow.dismiss();
//                //new GetSecretCode().execute();
//            }
//        }
//    }

    public void getSecretCode() {
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
                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                    JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
                    JSONObject secretCodes = responseInfo.getJSONObject("secretcodes");
                    color_code = secretCodes.getString("colorcode");
                    secret_code = secretCodes.getString("secretcode");

                    String imei_device_validity = responseInfo.getString("validimei_device");

                    if (imei_device_validity.equals("Valid")) {
                        QRCodeEncoder qrCodeEncoder;
                        String data = secret_code + " " + activeTicket.getTicket_id() + " " +
                                sharedPreferences.getString("passenger_id", "0") + " " +
                                encryptData(sharedPreferences.getString("user_name", "User").toUpperCase()) + " " +
                                sharedPreferences.getString("dob", "01/01/1990");
//                String data = secret_code + " " + activeTicket.getTicket_id() + " " +
//                        sharedPreferences.getString("passenger_id", "0") + " " +
//                        (sharedPreferences.getString("user_name", "User")) + " " + "04/12/1992";

                        Log.e("QRCodeData", data);
                        qrCodeEncoder = new QRCodeEncoder(data, null,
                                "TEXT_TYPE", BarcodeFormat.QR_CODE.toString(), smallerDimension);

                        bitmap_qr_code = qrCodeEncoder.encodeAsBitmap();
                        imageView_qrcode.setImageBitmap(bitmap_qr_code);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Mobile Device", Toast.LENGTH_LONG).show();
                    }
                    Log.e(TAG, "got");
                    pDialog.hide();
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
                Calendar calendar = Calendar.getInstance();
                String time = calendar.getTime().getHours() + "." + calendar.getTime().getMinutes();
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getSecretData");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("forTime", time);
                params.put("imei_device", "" + telephonyManager.getDeviceId());
                params.put("ticket_id", "" + activeTicket.getTicket_id());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, volleyTag);
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            long days = 0;
            long hours = 0;
            long minutes = 0;
            long secs = 0;
            long millisecs = 0;

            days = TimeUnit.MILLISECONDS.toDays(millisInFuture);
            hours = TimeUnit.MILLISECONDS.toHours(millisInFuture)
                    - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
                    .toDays(millisInFuture));
            minutes = TimeUnit.MILLISECONDS.toMinutes(millisInFuture)
                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                    .toHours(millisInFuture));
            secs = TimeUnit.MILLISECONDS.toSeconds(millisInFuture)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(millisInFuture));
            millisecs = (TimeUnit.MILLISECONDS.toMillis(millisInFuture)
                    - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS
                    .toSeconds(millisInFuture))) % 10;

            if (days < 0)
                ticket_layout_days.setVisibility(View.GONE);
            else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                ticket_layout_days.setVisibility(View.VISIBLE);
                ticket_layout_days.setPadding(10, 0, 10, 0);
                ticket_textView_day.setText(days + "");
            }
            if (hours < 0)
                ticket_layout_hours.setVisibility(View.GONE);
            else {
                if (days < 0)
                    ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_hours.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setVisibility(View.VISIBLE);
                ticket_layout_hours.setPadding(10, 0, 10, 0);
                ticket_textView_hour.setText(hours + "");
            }
            if (minutes < 0)
                ticket_layout_mins.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0)
                    ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_mins.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setVisibility(View.VISIBLE);
                ticket_layout_mins.setPadding(10, 0, 10, 0);
                ticket_textView_min.setText(minutes + "");
            }
            if (secs < 0)
                ticket_layout_secs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0)
                    ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_secs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setVisibility(View.VISIBLE);
                ticket_layout_secs.setPadding(10, 0, 10, 0);
                ticket_textView_sec.setText(secs + "");
            }
            if (millisecs < 0)
                ticket_layout_millisecs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0 && secs < 0)
                    ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_millisecs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setVisibility(View.VISIBLE);
                ticket_layout_millisecs.setPadding(10, 0, 10, 0);
                ticket_textView_millisec.setText(millisecs + "");
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;

            long days = 0;
            long hours = 0;
            long minutes = 0;
            long secs = 0;
            long millisecs = 0;

            days = TimeUnit.MILLISECONDS.toDays(millis);
            hours = TimeUnit.MILLISECONDS.toHours(millis)
                    - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS
                    .toDays(millis));
            minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                    .toHours(millis));
            secs = TimeUnit.MILLISECONDS.toSeconds(millis)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(millis));
            millisecs = (TimeUnit.MILLISECONDS.toMillis(millis)
                    - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS
                    .toSeconds(millis))) % 10;


            if (days < 0)
                ticket_layout_days.setVisibility(View.GONE);
            else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                ticket_layout_days.setVisibility(View.VISIBLE);
                ticket_textView_day.setText(days + "");
            }
            if (hours < 0)
                ticket_layout_hours.setVisibility(View.GONE);
            else {
                if (days < 0)
                    ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_hours.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setVisibility(View.VISIBLE);
                ticket_textView_hour.setText(hours + "");
            }
            if (minutes < 0)
                ticket_layout_mins.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0)
                    ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_mins.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setVisibility(View.VISIBLE);
                ticket_textView_min.setText(minutes + "");
            }
            if (secs < 0)
                ticket_layout_secs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0)
                    ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_secs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setVisibility(View.VISIBLE);
                ticket_textView_sec.setText(secs + "");
            }
            if (millisecs < 0)
                ticket_layout_millisecs.setVisibility(View.GONE);
            else {
                if (days < 0 && hours < 0 && minutes < 0 && secs < 0)
                    ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
                else
                    ticket_layout_millisecs.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setVisibility(View.VISIBLE);
                ticket_textView_millisec.setText(millisecs + "");
            }
            if (days == 0 && hours == 0 && minutes == 0 && secs == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_millisecs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0 && hours == 0 && minutes == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_secs.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0 && hours == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_mins.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else if (days == 0) {
                ticket_layout_days.setBackgroundColor(Color.parseColor("#EDEDED"));
                ticket_layout_hours.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            } else {
                ticket_layout_days.setBackground(getResources().getDrawable(R.drawable.bgimg_redcircle_timer));
            }
        }

        @Override
        public void onFinish() {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_active_ticket_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.navigate) {
            Intent intent = new Intent(getApplicationContext(), DocumentsView.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
//                params.add(new BasicNameValuePair("ticketnumber", activeTicket.getTicket_no()));
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
                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "got");
                    pDialog.hide();
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
                params.put("ticketnumber", activeTicket.getTicket_no());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, volleyTag);

    }

    @Override
    public void onBackPressed() {
        if (popupWindow_image != null) {
            if (popupWindow_image.isShowing()) {
                Log.e("PopupCheck", "Not null");
                popupWindow_image.dismiss();
                popupWindow_image = null;
            } else
                super.onBackPressed();
        } else
            super.onBackPressed();
    }

    public String encryptData(String data) {
        String encryptedData = "";
        char[] dataChars = data.toCharArray();

        for (char c : dataChars) {
            encryptedData += getNumber(c);
        }
        return encryptedData;
    }

    public String getNumber(char c) {
        String number = "";
        switch (c) {
            case 'A':
                number = "01";
                break;
            case 'B':
                number = "02";
                break;
            case 'C':
                number = "03";
                break;
            case 'D':
                number = "04";
                break;
            case 'E':
                number = "05";
                break;
            case 'F':
                number = "06";
                break;
            case 'G':
                number = "07";
                break;
            case 'H':
                number = "08";
                break;
            case 'I':
                number = "09";
                break;
            case 'J':
                number = "10";
                break;
            case 'K':
                number = "11";
                break;
            case 'L':
                number = "12";
                break;
            case 'M':
                number = "13";
                break;
            case 'N':
                number = "14";
                break;
            case 'O':
                number = "15";
                break;
            case 'P':
                number = "16";
                break;
            case 'Q':
                number = "17";
                break;
            case 'R':
                number = "18";
                break;
            case 'S':
                number = "19";
                break;
            case 'T':
                number = "20";
                break;
            case 'U':
                number = "21";
                break;
            case 'V':
                number = "22";
                break;
            case 'W':
                number = "23";
                break;
            case 'X':
                number = "24";
                break;
            case 'Y':
                number = "25";
                break;
            case 'Z':
                number = "26";
                break;
            default:
                number = "00";
        }
        return number;
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

}

//'userid' = '10',
//'ticketnumber' = 'ERPATPL1022UR',
//'actiontype' = 'sendTicketEmail',

// {"message":"Saved","msgcontent":{"requestParam":{"actiontype":"uploaddocuments","is_default":"0","document_name":"drivinglicense","userid":"10","document_file_name":"drivinglicense_10_doc_file.png","document_file_content":"iVBORw0KGgoAAAANSUhEUgAAAMIAAABtCAIAAAB8ySldAAAAA3NCSVQICAjb4U\/gAAAgAElEQVR4\nnIS9348d15Uu9nVx1eHaVJVUZfW56pLUMo\/HbU0zkhDSHmdEwA8mMAEsY\/Jw5y2D+xIjL0H+ngHu\nw1wgD85LMA4QxDLGA1C4I4CMrRvyjqmolZCZwzvk6JwRG64adw33UvdyOQ9r711V3fRNqdFqnlO1\na\/9Ye61v\/dw7zJhdBAAMiIIJAKDj53Yx4cWXwjGDABWmsV1RYWYoQGBAACYGRCS0zICoVMxghogo\nuGCoAHBgD3HEALx0jitmiE66QYC1w8wEa15UoBJ6DjAziKHhjVwwUxigaLi\/YhYVdgygeyZVxaLC\nxAKBMhPgAHUgDzioBwEKOUNduvakhTI7OK69tDgDcoAgXhgsIlyynMXJVAExl+FxiHDNciJwjDNw\njs6eUgFB+jh1DHaMEwgBgPQdiJkZImFpiNtOHMGrSC9csI1LRBgcJk3HdYzDn6wRIGGNWABAQAwR\nDzhiqPhIDIk20uPZRVKATmjI1mlGQ1P6AIhBcPbDAOQ8DUFSU0wAgQlGIszhn6LCBCGIiKgww8XH\nvcrYDXCgcmajPFGBABQ+ASEQCiACm2guKmaGiiiYmTltD4m9MnIBOwacg2MGcgCAcxAwATkcHOCh\ncAQQHIc+evF1XXPpAOelBYyGHOA4ryWtnApU2NmwAXJyBs4BZptwnNnGckwcCMj6a7sLgBcwABER\npAVKUy0SOmbkBQaEybbQfB0prsy4pmAwiK218EVs2WgIYBefvchH5mREv5\/TTO4Jl44EAcCnrU8s\nk7UPfVEBxIjXVlp0nAsmBpjBTIEup4TrCAC8SCRBQEUABjPYXiAQsV9puzMzgcs69NCeJYEGEjHC\ngwhyBoRz4AziWxC4ZPEigjRxaeCOnVd0m873HuQgAnK+97aEIOfYOXJQb1uLHTiMiNnVOAMDXDqo\nN8YTJ1OQh202ds+2Rw5bWQnTyAC4qkAMCDiuN7GHJNHhbNeBGZNJnkws4xw7sA9D+4Aw4uKG1RR\/\ngRYStUzIiEY+9PuIKaziZFoB+4TdRY4VltzGyUzMZBtObBiBhZiEioPiQP5j+17hVYLExLirjO1D\nwMRMHHgyRDSKUWLx3oQCuzq0z4yz+HgOEDv7j1wSagDYcVWwT7edhTu9eKjninEGBzCzYyAILG9P\ne3hHzosHIGeCHI4cVJwxOeNzcIGkcoBcVVc1O9taOIOxHHYuzScTs2MQc2LUABSVc6ISZEJkyg5s\n28kZkaXlSOtFEFuFAAPmS4wkT2aIx9nSzGnDSP8SJfLMoAMA6ACaMikNxBZpS8evCMjgMk7wBCDK\nAIKcChFhgAxKRBiEMsIAzYgXJAMKZlFRAANA9kaFwhXMBFXkRDlRPmhOyDOoIh+UrhTIFAo9FRAo\nI2SgTDEoiIlIM3AGAnhBOoABGawnqqIiwlfY5cgvO1wCAMoov5TnmQI5LgNfoZe+LJc57eipOpfr\nqRLIlQ4LbZ+1lBMItEO4hDxT3YGeqrvskCGnHKdABj1VzXJ3JQcxROVUXO5wCQp1mcsXeZ4hz6CX\nlC6X8pWqqg7ApRyi9JLTHcWgGIgvu\/5UOQ\/rggHsHA1533fFFdZBMKRlsOUkeS4A\/CA5kUJVCSrI\nSE6VBlUFBtj0Uka2TKmFwOoGKJQyAhBeQYyMMCgADJoDeQbNEN9uVBAvmULpCakgscQpYQGgkTy9\nGg1JIu10PxMY4KIaJZ0RucrIiozYAzwP3QmfEwMMheMIojXwIQ4gwDoT8JS9l4kTgmZiETEuyMwi\nAnJt2wYWkiP0nwDj2hLheQ4Q2LEzrqBclzUAR84VDjAAZGLOQ2MjQfZFNJ1PeDPcXFi7caQ5HIFL\ndgR4kc4AuGcKbwEEOUQ9TEXIHTtOPDytTeBGAbCyM5BgbJs4gRZbiyQAbI0CnJiIswCY5uxqRgNx\nAC+A2DyHJrjI+mymJje4qCKFvukUO7MJ9SDskeiATUgZ0HbEXgKICchdpZURiWNUrDCSj0qAUyIM\nl6SA9TkoZrkLBBcp26uP3XaOnc2IF48cosJFBYJX7wgGqL16Ly0HAOu8ek6TUARKArOLLbuJ4HHk\n4OABF\/U7P+8DO+Ycjp0HQM6LF4XpiVDUpYtogeVEcIbOtwyWM8\/kotoLjAMXZky11PAwAmXY3FYc\noLSBAVykFOKRDOQF3ycysMYzjBBs\/D2+O\/VOca5nkWIkUC6xjSEs8UTdCys6qt+oCjbVFIYKFV7F\nBWA47hIXdHgGmAtG2B+pGfsfj894byynEwnUQwzyIIgIE7hkNsW8ZJcDtrkZjhy8uAkoFC+2U7io\nHTnHtYh4iXq+TaJ6gCPwFJCPDAZJqfQER84RPOCKGmCIB7E17pgBZzRkcN4VdVXXAlS7FQIuhM0n\nM7NzTMzOce4AIAfn4Bw488gdCAIWZY8pI5CktfBsfWW6viOgpmgcSYIi6MhzVEQzeqCp8SBZU+L7\nx7cwzQjcRe0Aipkg04DdwIklTtYczAUASCAyhllrKGI3RZA7aSKmimHBIpJuEIqAEj4sm3M4C6vj\nmD0B3juqvRNWwAWZEva996bPe3hX1HLC0kpd13FWGQVDg+EAEC4qr2CuTArYG5nBym0vtanlGkEi\nwcw2DgCzF6krbjtxzMarHKEVMETOvOQOZ4G9ezMXqQNB1LMPim1g1Z0A7ChQszdwrZGmVYTMwFQF\nzk1RXwF7ERuII3iditowzwmHBGkwfmu2AxZlkMkccVEHMirfMTvKzNj4AomWPk9YZFSGvcLNHzFz\nGTQo\/2a2CbRkJp0p0ortmP7tknoceuXCHBkPN62THZJQYCfiq7oOAtQL4KGOXRT2KuLNJoS64PZZ\nB5iMCWzM92EbmIWQ82nHJsiARnmNqBLGKQq2yvhYUJuDin4GziFnSUcbrzAaG6mLGh8g4iXqxVF5\ncaIeZJLRRRwXmZ\/Cwzu41gQuUAd7o4DgJdhvAXiBI\/jUQxN\/FLpstsrJTogbftpnjFYenCOj84sa\nzcShCZrO4wUisAHRi0hw2iDxpKnIElUSdSF+OyKLRLgRJI4LZcs2Z7McOJMDPJNDstWKlwjXGAm0\nwpUMhfcCx1CIF8PUUMiZcA5HJrPCpLu0\/yi8KWg3Gkw1M8uKjg+ajXj8d2Ko3jM5MyhwzmYdwJlR\ngDflXgSI+E8wWY6womEm\/ZSw1ItKzWEPzMgoWp7GdVQBcUAdLxRHho3MMplWLbRm2BHZKNQS+hkN\n5OPSjq+0WbMu0wxop1WfqXWRhoAo76YEF1gu3MRo6dWMaQDYR+xlzMws1yKBhoxWDNhHvgWRVgSi\nvhMfthQQTLrkAg3lYU49BDngBWdiJkFgZK4ehtgYBDFvgBnrg6MmDNBx0NEcJj9xd0RzV1SsgmLB\nTFyVNfLAt8Lgz9iAl8AJWIw30CguJXKIxAt9YE0uIO3Ap9lH\/SQy+LC4brKgjsLkjAsd1z2hYeYJ\nJ7abk4E77usRYo8LDOCFFshEyPHyOt1\/QU2L5lFOund8q0y6Mv7BYYsLM0cdFT5C8iDIo6\/N2o8t\nh1ZHMDi5kiIj6hngorZ9L+ptop1jqE0xI4+oGQyDWjkcMzRABEfMFE2sCjGfWlwMH7\/yCBDVA14h\nXsTL5tkGZzCzeBi4RtJW4Ex45LKMPJhzwxhzILg\/4oTPftiDweyKyGzG4Yffidow8Syl

//{"message":"Success","msgcontent":{"requestParam":{"forTime":"11.21","actiontype":"getSecretData","ticket_id":"380","imei_device":"355819060361361","user_id":"57"},"responseInfo":{"secretcodes":{"codeofhour":"11.0","colorcode":"#FFCBDC","secretcode":"I96gu7"},"validimei_device":"Valid"}}}