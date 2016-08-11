package com.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VerificationCode;
import com.data.tickets.VolleyRequestData;
import com.database.ActiveTicketDatabase;
import com.database.VerificationCodeDatabase;
import com.trail.octo.R;
import com.trail.octo.SplashScreen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivateTicket extends BroadcastReceiver {

    public static final String TAG = "ActivateTicket";

    private NotificationManager notificationManager;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    TelephonyManager telephonyManager;
    VerificationCode verificationCode;
    VerificationCodeDatabase verificationCodeDatabase;
    ActiveTicketDatabase activeTicketDatabase;
    SharedPreferences sharedPreferences, sharedPreferences_user;

    int notificationid;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        sharedPreferences = context.getSharedPreferences("station", Context.MODE_PRIVATE);
        sharedPreferences_user = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        notificationid = intent.getIntExtra("notification_id", 0);
        verificationCode = intent.getParcelableExtra("code");
        verificationCodeDatabase = new VerificationCodeDatabase(context);
        activeTicketDatabase = new ActiveTicketDatabase(context);

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationid);
//        new ActivateCurrentTicket().execute();
        activateTicket();
    }

//    class ActivateCurrentTicket extends AsyncTask<Void, Void, Void> {
//
//        JSONObject jsonObject;
//        String message = "";
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
//                Log.e("Check", sharedPreferences_user.getString("user_id", "0"));
//                String id = sharedPreferences_user.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
//                params.add(new BasicNameValuePair("ticketid", verificationCode.getCode_id()));
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
//                    ticket_number = ticket_no;
//                }
//                verificationCodeDatabase.deleteVerificationCode(verificationCode.getCode_id());
//                if (check) {
//
//                    Intent dismissIntent = new Intent();
//                    dismissIntent.setAction("octo.dismissnotification");
//                    dismissIntent.putExtra("notificationId", notificationid);
//                    context.sendBroadcast(dismissIntent);
//
//                    postNotification("Ticket " + ticket_number + " is activated successfully", 1);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void postNotification(String msg, int notificationid) {
        Intent notifyIntent = new Intent(context, SplashScreen.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                context,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.octo_logo_notify_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.octo_logo_notify_icon)
                .setColor(context.getResources().getColor(android.R.color.white))
                .setContentTitle("Octo")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg));

        Notification mynotification = notification.build();

        notificationManager.notify(notificationid, mynotification);
    }

    public void activateTicket() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                parseActiveTicket(response);
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
            verificationCodeDatabase.deleteVerificationCode(verificationCode.getCode_id());
            if (check) {

                Intent dismissIntent = new Intent();
                dismissIntent.setAction("octo.dismissnotification");
                dismissIntent.putExtra("notificationId", notificationid);
                context.sendBroadcast(dismissIntent);

                postNotification("Ticket " + ticket_number + " is activated successfully", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
