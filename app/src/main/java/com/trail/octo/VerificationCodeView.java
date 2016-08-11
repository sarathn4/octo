package com.trail.octo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import com.data.tickets.VerificationCode;
import com.data.tickets.VolleyRequestData;
import com.database.ActiveTicketDatabase;
import com.database.Mydatabase;
import com.database.VerificationCodeDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerificationCodeView extends Home implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;

    public static final String TAG = "ActivateTicket";
    public static final int REQUEST_IMEI_NUMBER = 1000;
    GoogleApiClient mGoogleApiClient;

    ActiveTicketDatabase activeTicketDatabase;
    SharedPreferences sharedPreferences;
    Button button_activate;
    VerificationCode verificationCode;
    VerificationCodeDatabase verificationCodeDatabase;
    SharedPreferences sharedPreferences_stations;
    ImageView imageView_route;
    TextView textView_ticketno, textView_date, textView_user, textView_source, textView_destination,
            textView_no_of_tickets, textView_purchaseddate, textView_period, textView_category;
    LinearLayout layout_ticket_type;

    Mydatabase mydatabase;
    boolean activate = false;
    String activation_station = "";
    PopupWindow popupWindow, popupWindow_image;
    View view, view_image;
    ImageView imageView_profile;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code_view);
        super.onCreateDrawer();
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        activeTicketDatabase = new ActiveTicketDatabase(this);
        verificationCodeDatabase = new VerificationCodeDatabase(this);
        mydatabase = new Mydatabase(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        sharedPreferences_stations = getSharedPreferences("station", MODE_APPEND);

        verificationCode = getIntent().getParcelableExtra("verificationcode");
        imageView_profile = (ImageView) findViewById(R.id.image_profile);

        button_activate = (Button) findViewById(R.id.button_activate);
        imageView_route = (ImageView) findViewById(R.id.ticketView_route);
        textView_ticketno = (TextView) findViewById(R.id.ticketView_ticketno);
        textView_date = (TextView) findViewById(R.id.ticketView_date);
        textView_user = (TextView) findViewById(R.id.ticketView_name);
        textView_source = (TextView) findViewById(R.id.ticketView_source);
        textView_destination = (TextView) findViewById(R.id.ticketView_destination);
        textView_purchaseddate = (TextView) findViewById(R.id.ticketView_purchased_date);
        textView_no_of_tickets = (TextView) findViewById(R.id.ticketView_no_of_tickets);
        textView_period = (TextView) findViewById(R.id.ticketView_period);
        textView_category = (TextView) findViewById(R.id.ticketView_category);

        layout_ticket_type = (LinearLayout) findViewById(R.id.layout_ticket_type);
        textView_ticketno.setText(verificationCode.getVerification_code().toString());
        textView_purchaseddate.setText(getSplitDateAndTime(verificationCode.getPurchased_date()));
        textView_date.setText(getSplitDateAndTime(verificationCode.getPurchased_date()));
        textView_no_of_tickets.setText(verificationCode.getNo_of_tickets() + " Tickets");
        textView_source.setText(mydatabase.getStationName(verificationCode.getFrom_station_name()));
        textView_user.setText(sharedPreferences.getString("user_name", "User"));
        if (verificationCode.getTicket_type().equals("Platform")) {
            LinearLayout destinationlinearLayout = (LinearLayout) findViewById(R.id.ticketView_destination_layout);
            destinationlinearLayout.setVisibility(View.INVISIBLE);
            imageView_route.setVisibility(View.INVISIBLE);
            textView_category.setVisibility(View.GONE);
            textView_period.setVisibility(View.GONE);
        } else
            textView_destination.setText(mydatabase.getStationName(verificationCode.getTo_station_name()));
        if (verificationCode.getTicket_type().equals("Platform"))
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_pf));
        else if (verificationCode.getTicket_type().equals("Unreserved Ticket")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.GONE);
            textView_category.setText(verificationCode.getCategory());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_uts));
        } else if (verificationCode.getTicket_type().equals("MonthlyPass")) {
            textView_category.setVisibility(View.VISIBLE);
            textView_period.setVisibility(View.VISIBLE);
            textView_category.setText(verificationCode.getCategory());
            textView_period.setText(verificationCode.getTicket_period());
            layout_ticket_type.setBackground(getResources().getDrawable(R.drawable.bg_activeticket_top_st));
        }
        activation_station = sharedPreferences_stations.getString("station", "Nothing");

        if (activation_station.equals("Nothing")) {
            activate = false;
            Log.e("activation_station", activation_station);
        } else {
            Log.e("activation_station", activation_station);
            if (activation_station.equals(mydatabase.getStationName(verificationCode.getFrom_station_name())) ||
                    activation_station.equals(mydatabase.getStationName(verificationCode.getTo_station_name()))) {
                activate = true;
            }
        }
        Log.e("Source", mydatabase.getStationName(verificationCode.getFrom_station_name()));
        Log.e("Station", sharedPreferences_stations.getString("station", "Nothing"));
        Log.e("Destination", mydatabase.getStationName(verificationCode.getTo_station_name()));
        Log.e("Check Zone activate", activate + "");

        button_activate.setEnabled(true);
        button_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    checkBluetooth();
                } else {
                    call();
                }
                activation_station = sharedPreferences_stations.getString("station", "Nothing");

                if (activation_station.equals("Nothing")) {
                    activate = false;
                    Log.e("activation_station", activation_station);
                } else {
                    Log.e("activation_station", activation_station);
                    if (activation_station.equals(mydatabase.getStationName(verificationCode.getFrom_station_name())) ||
                            activation_station.equals(mydatabase.getStationName(verificationCode.getTo_station_name()))) {
                        activate = true;
                    }
                }

                Log.e("Source", mydatabase.getStationName(verificationCode.getFrom_station_name()));
                Log.e("Station", sharedPreferences_stations.getString("station", "Nothing"));
                Log.e("Destination", mydatabase.getStationName(verificationCode.getTo_station_name()));
                Log.e("Check Zone activate", activate + "");
                if (activate) {
                    Toast.makeText(getApplicationContext(), "You are in Activation Zone!", Toast.LENGTH_SHORT).show();
                    activateTicket();
//                    new ActivateTicket().execute();
                } else
                    Toast.makeText(getApplicationContext(), "You are out of Activation Zone!", Toast.LENGTH_SHORT).show();
            }
        });

        if (!verificationCode.getPhoto().equals("")) {
            byte[] imageData = Base64.decode(verificationCode.getPhoto(), Base64.DEFAULT);
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
        String imei_number = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getIMEIPermission();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        Log.e("Permission Check", "No:" + permissionCheck);
        if (permissionCheck == 0) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    getSystemService(Context.TELEPHONY_SERVICE);
            imei_number = telephonyManager.getDeviceId();
            Log.e("IMEI", "" + imei_number);
        }
    }

//    class ActivateTicket extends AsyncTask<Void, Void, Void> {
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
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "activateTickets"));
//                Log.e("Check", sharedPreferences.getString("user_id", "0"));
//                String id = sharedPreferences.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
//                params.add(new BasicNameValuePair("ticketid", verificationCode.getCode_id()));
//                params.add(new BasicNameValuePair("imei_device", telephonyManager.getDeviceId()));
//
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                httpResponse = httpClient.execute(httpPost);
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("ResponseActivate", result);
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                Log.e("MsgConetnthasrespnonse", msgcontent.has("responseInfo") + "");
////                if(msgcontent.has("responseInfo")){
//                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//
//                Log.e("Length", responseInfo.length() + "");
//                for (int i = 0; i < responseInfo.length(); i++) {
//                    JSONObject activatedTicket = responseInfo.getJSONObject(i);
//                    String ticket_id = activatedTicket.getString("id");
//                    String ticket_no = activatedTicket.getString("ticket_number");
//                    Log.e("Ticket No", ticket_no);
//                    String ticket_code = activatedTicket.getString("ticket_code");
//                    String from_station = activatedTicket.getString("from_station");
//                    String to_station = activatedTicket.getString("to_station");
//                    String ticket_type = activatedTicket.getString("ticket_type");
//                    String no_of_tickets = activatedTicket.getString("no_of_tickets");
//                    String ticket_category = activatedTicket.getString("ticket_category");
//                    String ticket_period = activatedTicket.getString("ticket_period");
//                    String ticket_amount = activatedTicket.getString("ticket_amount");
//                    String purchased_date = activatedTicket.getString("purchased_on");
//                    String activated_date = activatedTicket.getString("activated_on");
//                    String activated_station = activatedTicket.getString("activated_station_code");
//                    String valid_date = activatedTicket.getString("valid_till");
//                    String proof_document = activatedTicket.getString("proof_document");
//                    String photo = activatedTicket.getString("upload_photo");
//                    String validated_count = activatedTicket.getString("validated_count");
//                    String imei_device = activatedTicket.getString("imei_device");
//
//                    Log.e("DownloadedPhoto", "Data:" + photo);
//                    Log.e("VCPhoto", "Data:" + verificationCode.getPhoto());
//                    boolean check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
//                            from_station, to_station, ticket_type, no_of_tickets,
//                            ticket_category, ticket_period, ticket_amount,
//                            purchased_date, activated_date, activated_station,
//                            valid_date, proof_document, photo, validated_count,imei_device);
//
//                    popupWindow.dismiss();
//                }
//                verificationCodeDatabase.deleteVerificationCode(verificationCode.getCode_id());
//                Toast.makeText(getApplicationContext(), "Ticket is successfully activated", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(VerificationCodeView.this, Home.class);
//                startActivity(intent);
////                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                popupWindow.dismiss();
//            }
//        }
//    }

    public void activateTicket() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                parseActiveTicket(response);
                pDialog.hide();
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
                params.put("actiontype", "activateTickets");
                Log.e("Check", sharedPreferences.getString("user_id", "0"));
                String id = sharedPreferences.getString("user_id", "0");
                params.put("userid", id);
                params.put("ticketid", verificationCode.getCode_id());
                params.put("imei_device", telephonyManager.getDeviceId());

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

    public void parseActiveTicket(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            Log.e("MsgConetnthasrespnonse", msgcontent.has("responseInfo") + "");
//                if(msgcontent.has("responseInfo")){
            JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");

            Log.e("Length", responseInfo.length() + "");
            for (int i = 0; i < responseInfo.length(); i++) {
                JSONObject activatedTicket = responseInfo.getJSONObject(i);
                String ticket_id = activatedTicket.getString("id");
                String ticket_no = activatedTicket.getString("ticket_number");
                Log.e("Ticket No", ticket_no);
                String ticket_code = activatedTicket.getString("ticket_code");
                String from_station = activatedTicket.getString("from_station");
                String to_station = activatedTicket.getString("to_station");
                String ticket_type = activatedTicket.getString("ticket_type");
                String no_of_tickets = activatedTicket.getString("no_of_tickets");
                String ticket_category = activatedTicket.getString("ticket_category");
                String ticket_period = activatedTicket.getString("ticket_period");
                String ticket_amount = activatedTicket.getString("ticket_amount");
                String purchased_date = activatedTicket.getString("purchased_on");
                String activated_date = activatedTicket.getString("activated_on");
                String activated_station = activatedTicket.getString("activated_station_code");
                String valid_date = activatedTicket.getString("valid_till");
                String proof_document = activatedTicket.getString("proof_document");
                String photo = activatedTicket.getString("upload_photo");
                String validated_count = activatedTicket.getString("validated_count");
                String imei_device = activatedTicket.getString("imei_device");

                Log.e("DownloadedPhoto", "Data:" + photo);
                Log.e("VCPhoto", "Data:" + verificationCode.getPhoto());
                boolean check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
                        from_station, to_station, ticket_type, no_of_tickets,
                        ticket_category, ticket_period, ticket_amount,
                        purchased_date, activated_date, activated_station,
                        valid_date, proof_document, photo, validated_count, imei_device);
            }
            verificationCodeDatabase.deleteVerificationCode(verificationCode.getCode_id());
            Toast.makeText(getApplicationContext(), "Ticket is successfully activated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VerificationCodeView.this, Home.class);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void getIMEIPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        REQUEST_IMEI_NUMBER);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void checkBluetooth() {
        Log.e("Check", "checkBTE");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If Bluetooth is not enabled, let user enable it.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

        }
    }

    public void call() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e("Permission", "Already Granted");
            requestLocationAndBle();
        }
    }

    public void requestLocationAndBle() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());
        builder.setNeedBle(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Log.e("Check", "Status Success");
                        checkBluetooth();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            Log.e("Check", "Resolution Required");
                            status.startResolutionForResult(
                                    VerificationCodeView.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        Log.e("Check", "Status Change unavailable");
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                checkBluetooth();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            Log.e("Check", "Result Ok");
                            checkBluetooth();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            Toast.makeText(getApplicationContext(), "OCTO cannot function without" +
                                    " these must permissions", Toast.LENGTH_SHORT).show();
                            Log.e("Check", "Result Cancelled");
                            requestLocationAndBle();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}


//ACTIVATE\\\\
//{"message":null,
// "msgcontent":{"requestParam":{"actiontype":"activateTickets","userid":"10","ticketid":"6","passengerid":"9"},
// "responseInfo":[
// {"id":"2","from_station":"9","to_station":"0","ticket_type":"Platform","ticket_category":null,
// "ticket_period":null,"ticket_number":"DDRP01002","ticket_code":"2RD01D","ticket_amount":"10",
// "purchased_on":"2015-08-18 07:08:49","activated_on":"2015-08-18 23:27:42","activated_station_code":"0",
// "valid_till":"2015-08-19 01:27:42","status_date":"2015-08-18 23:27:42","activatedDate":"2015-08-18 23:27:42"}]}}
//CSMUMADH1001
//CSMUMDDR1054PF
//{"message":null,
// "msgcontent":{"requestParam":
// {"actiontype":"activateTickets","userid":"57","ticketid":"271","passengerid":"33"},
// "responseInfo":[
// {"id":"239","from_station":"9","to_station":"1","ticket_type":"Unreserved Ticket","ticket_category":"","ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMDDR1051UR","ticket_code":"DUCD0R","ticket_amount":"11","purchased_on":"2015-08-03 19:46:29","activated_on":"2015-09-04 02:46:29","activated_station_code":"0","valid_till":"2015-09-05 02:46:29","status_date":"2015-09-04 02:46:29","txnid":"99d79c67e5b83f3dd237","activatedDate":"2015-09-04 02:46:29"},{"id":"257","from_station":"9","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Monthly","no_of_tickets":"1","ticket_number":"CSMUMDDR1025ST","ticket_code":"SCDSRM","ticket_amount":"11","purchased_on":"2015-08-04 19:10:17","activated_on":"2015-09-04 19:15:27","activated_station_code":"0","valid_till":"2015-10-04 19:15:27","status_date":"2015-09-04 19:15:27","txnid":"6f94d11c070cb5fd3e4c","activatedDate":"2015-09-04 19:15:27"},{"id":"262","from_station":"8","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Monthly","no_of_tickets":"1","ticket_number":"CSMUMEPR1027ST","ticket_code":"SRTC2M","ticket_amount":"11","purchased_on":"2015-08-05 08:54:24","activated_on":"2015-09-05 08:58:47","activated_station_code":"0","valid_till":"2015-10-05 08:58:47","status_date":"2015-09-05 08:58:47","txnid":"c761cc3b5193fd41b544","activatedDate":"2015-09-05 08:58:47"},{"id":"263","from_station":"4","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Monthly","no_of_tickets":"1","ticket_number":"CSMUMGTR1028ST","ticket_code":"RTMG12","ticket_amount":"11","purchased_on":"2015-08-05 09:18:59","activated_on":"2015-09-05 09:21:03","activated_station_code":"0","valid_till":"2015-10-05 09:21:03","status_date":"2015-09-05 09:21:03","txnid":"7275ee70519da3a65794","activatedDate":"2015-09-05 09:21:03"},{"id":"264","from_station":"11","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Quarterly","no_of_tickets":"1","ticket_number":"CSMUMMM1029ST","ticket_code":"M9MMMU","ticket_amount":"11","purchased_on":"2015-08-05 09:21:35","activated_on":"2015-09-05 09:25:00","activated_station_code":"0","valid_till":"2015-12-05 09:25:00","status_date":"2015-09-05 09:25:00","txnid":"9d9ca8cd96f4e5f2c0cc","activatedDate":"2015-09-05 09:25:00"},{"id":"265","from_station":"23","to_station":"1","ticket_type":"Unreserved Ticket","ticket_category":"II Class","ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMMIRA1064UR","ticket_code":"MARMR0","ticket_amount":"11","purchased_on":"2015-08-05 11:37:38","activated_on":"2015-09-05 11:39:55","activated_station_code":"0","valid_till":"2015-09-06 11:39:55","status_date":"2015-09-05 11:39:55","txnid":"25adf2ba1d123d4d5e2f","activatedDate":"2015-09-05 11:39:55"},{"id":"266","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1055PF","ticket_code":"GFC5CS","ticket_amount":"11","purchased_on":"2015-08-05 11:41:45","activated_on":"2015-09-05 11:42:56","activated_station_code":"0","valid_till":"2015-09-05 13:42:56","status_date":"2015-09-05 11:42:56","txnid":"432762ebd2b403e66422","activatedDate":"2015-09-05 11:42:56"},{"id":"267","from_station":"16","to_station":"1","ticket_type":"MonthlyPass","ticket_category":"I Class","ticket_period":"Quarterly","no_of_tickets":"1","ticket_number":"CSMUMADH1030ST","ticket_code":"31A0S0","ticket_amount":"11","purchased_on":"2015-08-05 11:44:38","activated_on":"2015-09-05 11:49:11","activated_station_code":"0","valid_till":"2015-12-05 11:49:11","status_date":"2015-09-05 11:49:11","txnid":"133097395bb8f78ee64e","activatedDate":"2015-09-05 11:49:11"},{"id":"268","from_station":"9","to_station":"1","ticket_type":"Unreserved Ticket","ticket_category":"I Class","ticket_period":null,"no_of_tickets":"4",


//CSMUMBVI1058PF
//CSMUMDDR1032ST