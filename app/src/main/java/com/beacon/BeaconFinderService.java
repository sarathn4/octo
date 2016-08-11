package com.beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.ActiveTicket;
import com.data.tickets.VerificationCode;
import com.data.tickets.VolleyRequestData;
import com.database.ActiveTicketDatabase;
import com.database.Mydatabase;
import com.database.RouteDatabase;
import com.database.VerificationCodeDatabase;
import com.jaalee.sdk.Beacon;
import com.jaalee.sdk.BeaconManager;
import com.jaalee.sdk.MonitoringListener;
import com.jaalee.sdk.RangingListener;
import com.jaalee.sdk.Region;
import com.jaalee.sdk.ServiceReadyCallback;
import com.jaalee.sdk.utils.L;
import com.trail.octo.R;
import com.trail.octo.SplashScreen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BeaconFinderService extends Service {

    public static final String TAG = "ActivateTicket";

    private static final int NOTIFICATION_DADAR = 1;
    private static final int NOTIFICATION_BORIVALI = 2;
    private static final int NOTIFICATION_CHURCHGATE = 3;
    private static final int NOTIFICATION_ACTIVATION = 4;

    private static final Region ALL_BEACONS_REGION = new Region("rid", null,
            null, null);
    VerificationCodeDatabase verificationCodeDatabase;
    ActiveTicketDatabase activeTicketDatabase;
    Mydatabase mydatabase;
    RouteDatabase routeDatabase;
    int size = 0;
    List<Beacon> list_of_beacons;
    int count = 0;
    Region myregion;
    SharedPreferences sharedPreferences, sharedPreferences_user, sharedPreferences_ticket_actiavtion;
    boolean station_check = false;
    private NotificationManager notificationManager;
    private BeaconManager beaconManager;

    VerificationCode myVerificationCode = null;

    //    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    boolean activate;

    TelephonyManager telephonyManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        list_of_beacons = new ArrayList<Beacon>();
        //Configure verbose debug logging.
        L.enableDebugLogging(false);
        //Configure BeaconManager.

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        beaconManager = new BeaconManager(this);
        sharedPreferences = getSharedPreferences("station", MODE_PRIVATE);
        sharedPreferences_user = getSharedPreferences("user_data", MODE_PRIVATE);
        sharedPreferences_ticket_actiavtion = getSharedPreferences("ticket_activation", MODE_PRIVATE);
        activate = sharedPreferences_ticket_actiavtion.getBoolean("activate", false);

        verificationCodeDatabase = new VerificationCodeDatabase(this);
        routeDatabase = new RouteDatabase(this);
        mydatabase = new Mydatabase(this);
        activeTicketDatabase = new ActiveTicketDatabase(this);

        beaconManager.setRangingListener(new RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region,
                                            final List beacons) {
                try {
                    List<Beacon> rangedBeacons = beacons;
                    for (Beacon beacon : rangedBeacons) {
                        if (!(list_of_beacons.contains(beacon))) {
                            list_of_beacons.add(beacon);
                            //Beacon beacon = rangedBeacons.get(0);
                            size = rangedBeacons.size();
                            //postNotification("Octo Tickets");
                            myregion = new Region("regionId", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
                            beaconManager.connect(new ServiceReadyCallback() {
                                @Override
                                public void onServiceReady() {
                                    beaconManager.startMonitoring(myregion);
                                }
                            });
                            // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
                            // In order for this demo to be more responsive and immediate we lower down those values.
                            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

                            beaconManager.setMonitoringListener(new MonitoringListener() {
                                @Override
                                public void onEnteredRegion(Region region) {
                                    count = 0;
                                    station_check = false;
                                    Log.e("Check", "Entered");
                                    ArrayList<VerificationCode> verificationCodeArrayList = new ArrayList<VerificationCode>();
                                    verificationCodeArrayList = verificationCodeDatabase.getData("ALL");
                                    ArrayList<ActiveTicket> activeTicketArrayList = new ArrayList<ActiveTicket>();
                                    activeTicketArrayList = activeTicketDatabase.getData("ALL");
                                    Log.e("Check", verificationCodeArrayList.size() + "");

                                    Log.e("Check", region.getMajor() + "");

                                    if (region.getMajor().equals(10000)) {
                                        //postNotification();
                                        count = 0;
                                        station_check = false;
                                        Log.e("Check", "Entered 10000");
                                        //21685 29430 40741
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("station", "Churchgate");
                                        editor.commit();
                                        Log.e("CheckChurchgate", sharedPreferences.getString("station", "Nothing!!!") + "");
                                        if (!verificationCodeArrayList.isEmpty()) {

                                            for (VerificationCode verificationCode : verificationCodeArrayList) {
                                                Log.e("Station Name", mydatabase.getStationName(verificationCode.getFrom_station_name()));
                                                if (mydatabase.getStationName(verificationCode.getFrom_station_name()).equals("Churchgate")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (mydatabase.getStationName(verificationCode.getTo_station_name()).equals("Churchgate")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (routeDatabase.hasStation(mydatabase.getData(verificationCode.getFrom_station_name()), mydatabase.getData(verificationCode.getTo_station_name()), "Churchgate")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                }
                                            }
                                            if (count > 0) {
                                                if (count == 1) {
                                                    activate = sharedPreferences_ticket_actiavtion.getBoolean("activate", false);
                                                    if (activate) {
                                                        activateTicket();
//                                                        new ActivateTicket().execute();
                                                    } else {
                                                        notifyToActivate(verificationCodeArrayList.get(0), NOTIFICATION_CHURCHGATE + count);
//                                                        postNotification("Welcome to Churchgate station! " + count + " ticket can be activated",
//                                                                NOTIFICATION_CHURCHGATE);
                                                    }
                                                } else {

                                                    for (int i = 0; i < count; i++) {
                                                        notifyToActivate(verificationCodeArrayList.get(i), NOTIFICATION_CHURCHGATE + i);
                                                    }
//                                                    postNotification("Welcome to Churchgate station! " + count + " tickets can be activated",
//                                                            NOTIFICATION_CHURCHGATE);
                                                }
                                            }
                                        }

                                        if (!station_check) {
                                            if (!(activeTicketArrayList.isEmpty())) {
                                                for (ActiveTicket activeTicket : activeTicketArrayList) {
                                                    if (mydatabase.getStationName(activeTicket.getFrom_station()).equals("Churchgate")) {
                                                        station_check = true;
                                                    } else if (mydatabase.getStationName(activeTicket.getTo_station()).equals("Churchgate")) {
                                                        station_check = true;
                                                    } else if (routeDatabase.hasStation(mydatabase.getData(activeTicket.getFrom_station()), mydatabase.getData(activeTicket.getTo_station()), "Churchgate")) {
                                                        station_check = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (!station_check)
                                            postNotification("Hi " + sharedPreferences_user.getString("user_name", "User")
                                                    + "!You don't have a ticket for Churchgate. Please make sure you have the appropriate " +
                                                    "ticket", NOTIFICATION_CHURCHGATE);
                                    }
                                    if (region.getMajor().equals(16182)) {
                                        count = 0;
                                        station_check = false;

                                        Log.e("Check", "Entered 16182");
                                        //21685 29430 40741
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.putString("station", "Borivali");
                                        editor.commit();
                                        if (!verificationCodeArrayList.isEmpty()) {

                                            for (VerificationCode verificationCode : verificationCodeArrayList) {
                                                Log.e("Station Name", mydatabase.getStationName(verificationCode.getFrom_station_name()));
                                                if (mydatabase.getStationName(verificationCode.getFrom_station_name()).equals("Borivali")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (mydatabase.getStationName(verificationCode.getTo_station_name()).equals("Borivali")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (routeDatabase.hasStation(mydatabase.getData(verificationCode.getFrom_station_name()), mydatabase.getData(verificationCode.getTo_station_name()), "Borivali")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                }
                                            }
                                            if (count > 0) {
                                                if (count == 1) {
                                                    activate = sharedPreferences_ticket_actiavtion.getBoolean("activate", false);
                                                    if (activate) {
                                                        activateTicket();
//                                                        new ActivateTicket().execute();
                                                    } else {
                                                        notifyToActivate(verificationCodeArrayList.get(0), NOTIFICATION_BORIVALI + count);
//                                                        postNotification("Welcome to Borivali station! " + count + " ticket can be activated",
//                                                                NOTIFICATION_CHURCHGATE);
                                                    }
                                                } else {
                                                    for (int i = 0; i < count; i++) {
                                                        notifyToActivate(verificationCodeArrayList.get(i), NOTIFICATION_BORIVALI + i);
                                                    }
//                                                    postNotification("Welcome to Borivali station! " + count + " tickets can be activated",
//                                                            NOTIFICATION_BORIVALI);
                                                }
                                            }
                                        }
                                        if (!station_check) {
                                            if (!(activeTicketArrayList.isEmpty())) {
                                                for (ActiveTicket activeTicket : activeTicketArrayList) {
                                                    if (mydatabase.getStationName(activeTicket.getFrom_station()).equals("Borivali")) {
                                                        station_check = true;
                                                    } else if (mydatabase.getStationName(activeTicket.getTo_station()).equals("Borivali")) {
                                                        station_check = true;
                                                    } else if (routeDatabase.hasStation(mydatabase.getData(activeTicket.getFrom_station()), mydatabase.getData(activeTicket.getTo_station()), "Borivali")) {
                                                        station_check = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (!station_check) {
                                            postNotification("Hi " + sharedPreferences_user.getString("user_name", "User")
                                                    + "!You don't have a ticket for Borivali. Please make sure you have the appropriate " +
                                                    "ticket", NOTIFICATION_BORIVALI);
                                        }
                                    }

                                    if (region.getMajor().equals(13869)) {
                                        count = 0;
                                        station_check = false;
                                        Log.e("Check", "Entered 13869");
                                        //21685 29430 40741
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.putString("station", "Dadar");
                                        editor.commit();
                                        if (!verificationCodeArrayList.isEmpty()) {

                                            for (VerificationCode verificationCode : verificationCodeArrayList) {
                                                Log.e("Station Name", mydatabase.getStationName(verificationCode.getFrom_station_name()));
                                                if (mydatabase.getStationName(verificationCode.getFrom_station_name()).equals("Dadar")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (mydatabase.getStationName(verificationCode.getTo_station_name()).equals("Dadar")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                } else if (routeDatabase.hasStation(mydatabase.getData(verificationCode.getFrom_station_name()), mydatabase.getData(verificationCode.getTo_station_name()), "Dadar")) {
                                                    ++count;
                                                    station_check = true;
                                                    myVerificationCode = verificationCode;
                                                }
                                            }
                                            if (count > 0) {
                                                if (count == 1) {
                                                    activate = sharedPreferences_ticket_actiavtion.getBoolean("activate", false);
                                                    if (activate) {
                                                        activateTicket();
//                                                        new ActivateTicket().execute();
                                                    } else {
                                                        notifyToActivate(verificationCodeArrayList.get(0), NOTIFICATION_CHURCHGATE + count);
//                                                        postNotification("Welcome to Dadar station! " + count + " ticket can be activated",
//                                                                NOTIFICATION_CHURCHGATE);
                                                    }
                                                } else {
                                                    for (int i = 0; i < count; i++) {
                                                        notifyToActivate(verificationCodeArrayList.get(i), NOTIFICATION_CHURCHGATE + i);
                                                    }
//                                                    postNotification("Welcome to Dadar station! " + count + " tickets can be activated",
//                                                            NOTIFICATION_DADAR);
                                                }
                                            }
                                        }
                                        if (!station_check) {
                                            if (!(activeTicketArrayList.isEmpty())) {
                                                for (ActiveTicket activeTicket : activeTicketArrayList) {
                                                    if (mydatabase.getStationName(activeTicket.getFrom_station()).equals("Dadar")) {
                                                        station_check = true;
                                                    } else if (mydatabase.getStationName(activeTicket.getTo_station()).equals("Dadar")) {
                                                        station_check = true;
                                                    } else if (routeDatabase.hasStation(mydatabase.getData(activeTicket.getFrom_station()), mydatabase.getData(activeTicket.getTo_station()), "Dadar")) {
                                                        station_check = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (!station_check) {
                                            postNotification("Hi " + sharedPreferences_user.getString("user_name", "User")
                                                    + "!You don't have a ticket for Dadar. Please make sure you have the appropriate " +
                                                    "ticket", NOTIFICATION_DADAR);
                                        }
                                    }
                                }


                                @Override
                                public void onExitedRegion(Region region) {
                                    //40741 into 10000
                                    if (region.getMajor().equals(10000)) {
                                        station_check = false;
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();
                                        postNotification("Exited Churchgate Activation zone", NOTIFICATION_CHURCHGATE);
                                    }
                                    //21685 into 16182
                                    if (region.getMajor().equals(16182)) {
                                        station_check = false;
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();
                                        postNotification("Exited Borivali station Activation zone", NOTIFICATION_BORIVALI);
                                    }
                                    //29430 into 13869
                                    if (region.getMajor().equals(13869)) {
                                        station_check = false;
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();
                                        postNotification("Exited Dadar station Activation zone", NOTIFICATION_DADAR);
                                    }
                                }
                            });

//                    postNotification("Count: " + size + "Latest UUID:" + beacon.getProximityUUID() + " Major:" + beacon.getMinor()
//                            + " Minor:" + beacon.getMinor());
                            Log.e("Check", "Ranged UUID:" + beacon.getProximityUUID() + " Major:" + beacon.getMinor()
                                    + " Minor:" + beacon.getMinor());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beaconManager.connect(new ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager
                        .startRangingAndDiscoverDevice(ALL_BEACONS_REGION);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        notificationManager.cancel(NOTIFICATION_ID);
//        beaconManager.connect(new ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                beaconManager.startMonitoring(regions[0]);
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(NOTIFICATION_BORIVALI);
        notificationManager.cancel(NOTIFICATION_CHURCHGATE);
        notificationManager.cancel(NOTIFICATION_DADAR);
        beaconManager.disconnect();
        super.onDestroy();
    }

    private void postNotification(String msg, int notificationid) {
        Intent notifyIntent = new Intent(this, SplashScreen.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.octo_logo_notify_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.octo_logo_notify_icon)
                .setColor(getResources().getColor(android.R.color.white))
                .setContentTitle("Octo")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg));

        Notification mynotification = notification.build();

        notificationManager.notify(notificationid, mynotification);
    }

//    class ActivateTicket extends AsyncTask<Void, Void, Void> {
//
//        JSONObject jsonObject;
//        // String message = "";
//        boolean check = false;
//        String ticket_number = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "activateTickets"));
//                Log.e("Check!", sharedPreferences_user.getString("user_id", "0"));
//                String id = sharedPreferences_user.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
//                Log.e("Check", "z");
//                params.add(new BasicNameValuePair("ticketid", myVerificationCode.getCode_id()));
//                Log.e("Check", "y");
//
//                Log.e("Check", "a");
//                httpClient = new DefaultHttpClient();
//                Log.e("Check", "b");
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                Log.e("Check", "c");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//                Log.e("Check", "d");
//
//                httpResponse = httpClient.execute(httpPost);
//                Log.e("Check", "e");
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("ResponseActivate", result);
//
//                jsonObject = new JSONObject(result);
//                Log.e("Check", "f");
////                message = jsonObject.getString("message");
////                Log.e("Check", message);
//            } catch (Exception e) {
//                Log.e("Check", "Exception!");
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
//                    check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
//                            from_station, to_station, ticket_type, no_of_tickets,
//                            ticket_category, ticket_period, ticket_amount,
//                            purchased_date, activated_date, activated_station,
//                            valid_date, proof_document, photo, validated_count, imei_device);
//
//                    ticket_number = ticket_no;
//                }
//                verificationCodeDatabase.deleteVerificationCode(myVerificationCode.getCode_id());
//                if (check) {
//                    count = 0;
//                    postNotification("Ticket " + ticket_number + " is activated successfully", NOTIFICATION_ACTIVATION);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void activateTicket() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                parseActiveTicket(response);
                Intent intent = new Intent("UpdateTickets");
                sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "activateTickets");
                Log.e("Check", "UserID:" + sharedPreferences_user.getString("user_id", "0"));
                String id = sharedPreferences_user.getString("user_id", "0");
                params.put("userid", id);
                params.put("ticketid", myVerificationCode.getCode_id());
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
        boolean check = false;
        String ticket_number = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            Log.e("MsgConetnthasrespnonse", msgcontent.has("responseInfo") + "");
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

                check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
                        from_station, to_station, ticket_type, no_of_tickets,
                        ticket_category, ticket_period, ticket_amount,
                        purchased_date, activated_date, activated_station,
                        valid_date, proof_document, photo, validated_count, imei_device);

                ticket_number = ticket_no;
            }
            verificationCodeDatabase.deleteVerificationCode(myVerificationCode.getCode_id());
            if (check) {
                count = 0;
                postNotification("Ticket " + ticket_number + " is activated successfully", NOTIFICATION_ACTIVATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyToActivate(VerificationCode verificationCode, int notificationid) {
        Intent notifyIntent = new Intent(this, SplashScreen.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String message;
        if (verificationCode.getFrom_station_name().equals(verificationCode.getTo_station_name())) {
            message = "Ticket for " + mydatabase.getStationName(verificationCode.getFrom_station_name())
                    + " can be activated!";
        } else {
            message = "Ticket from " + mydatabase.getStationName(verificationCode.getFrom_station_name())
                    + " to "
                    + mydatabase.getStationName(verificationCode.getTo_station_name())
                    + " can be activated!";
        }
        Intent nextIntent = new Intent();
        nextIntent.setAction("octo.activateticket");
        nextIntent.putExtra("notificationId", notificationid);
        nextIntent.putExtra("code", verificationCode);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);

        Intent dismissIntent = new Intent();
        dismissIntent.setAction("octo.dismissnotification");
        dismissIntent.putExtra("notificationId", notificationid);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

        PendingIntent pendingIntent = PendingIntent.getActivities(
                this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.octo_logo_notify_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.octo_logo_notify_icon)
                .setColor(ContextCompat.getColor(this, android.R.color.white))
                .setContentTitle("Octo")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_delete, "Later", dismissPendingIntent)
                .addAction(android.R.drawable.ic_dialog_alert, "Activate", nextPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message));

        Notification mynotification = notification.build();

        notificationManager.notify(notificationid, mynotification);
    }

}
//Ranged UUID:f94dbb23-2266-7822-3782-57beac0952ac Major:44848 Minor:44848
//Ranged UUID:f94dbb23-2266-7822-3782-57beac0952ac Major:3545 Minor:3545
//Ranged UUID:f94dbb23-2266-7822-3782-57beac0952ac Major:44766 Minor:44766


//// Borivali - SN:0117C55349F4 EddySTone Major 16182
//// Churchgate - SN: 0117C554C987 Eddystone Major 10000