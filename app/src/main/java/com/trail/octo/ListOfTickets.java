package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.ActiveTicketListAdapter;
import com.adapters.ExpiredTicketListAdapter;
import com.adapters.VerificationCodeListAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.ActiveTicket;
import com.data.tickets.ExpiredTicket;
import com.data.tickets.VerificationCode;
import com.data.tickets.VolleyRequestData;
import com.database.ActiveTicketDatabase;
import com.database.ExpiredTicketDatabase;
import com.database.VerificationCodeDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfTickets extends Home {

    public static final String TAG = "ListOfTickets";
    ListView listView_tickets;
    TextView textView_empty;
    VerificationCodeDatabase verificationCodeDatabase;
    ActiveTicketDatabase activeTicketDatabase;
    ExpiredTicketDatabase expiredTicketDatabase;

    ArrayList<VerificationCode> verificationCodes;
    ArrayList<ActiveTicket> activeTickets;
    ArrayList<ExpiredTicket> expiredTickets;

    String type = "";
    String validity = "";
    SharedPreferences sharedPreferences;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    SwipeRefreshLayout swipeRefreshLayout;
    ActiveTicketListAdapter activeTicketListAdapter;
    ExpiredTicketListAdapter expiredTicketListAdapter;
    VerificationCodeListAdapter verificationCodeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tickets);
        super.onCreateDrawer();
        type = getIntent().getStringExtra("type");
        validity = getIntent().getStringExtra("validity");
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        listView_tickets = (ListView) findViewById(R.id.listView_tickets_list);
        textView_empty = (TextView) findViewById(R.id.textView_empty);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (validity.equals("verification_code")) {
//                    new GetVerificationCodes().execute();
                    getVerificationCodes();
                    verificationCodes = verificationCodeDatabase.getData(type);
                    if (verificationCodes.isEmpty())
                        textView_empty.setVisibility(View.VISIBLE);
                    else
                        textView_empty.setVisibility(View.GONE);
                    verificationCodeListAdapter.notifyDataSetChanged();
                } else if (validity.equals("active")) {
//                    new GetActiveTickets().execute();
                    getActiveTickets();
                    activeTickets = activeTicketDatabase.getData(type);
                    if (activeTickets.isEmpty())
                        textView_empty.setVisibility(View.VISIBLE);
                    else
                        textView_empty.setVisibility(View.GONE);
                    activeTicketListAdapter.notifyDataSetChanged();
                } else if (validity.equals("expired")) {
//                    new GetExpiredTickets().execute();
                    getExpiredTickets();
                    expiredTickets = expiredTicketDatabase.getData(type);
                    if (expiredTickets.isEmpty())
                        textView_empty.setVisibility(View.VISIBLE);
                    else
                        textView_empty.setVisibility(View.GONE);
                    expiredTicketListAdapter.notifyDataSetChanged();
                }
            }
        });
        if (validity.equals("active")) {
            View view = getLayoutInflater().inflate(R.layout.empty_list, null);
            addContentView(view, listView_tickets.getLayoutParams());
            listView_tickets.setEmptyView(view);
            activeTicketDatabase = new ActiveTicketDatabase(this);
            activeTickets = activeTicketDatabase.getData(type);
            if (activeTickets.isEmpty())
                textView_empty.setVisibility(View.VISIBLE);
            else
                textView_empty.setVisibility(View.GONE);
            activeTicketListAdapter = new ActiveTicketListAdapter(getApplicationContext(), activeTickets);
            listView_tickets.setAdapter(activeTicketListAdapter);

            listView_tickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListOfTickets.this, ActiveTicketView.class);
                    intent.putExtra("activeticket", activeTickets.get(position));
                    startActivity(intent);
                }
            });
        }
        if (validity.equals("expired")) {
            View view = getLayoutInflater().inflate(R.layout.empty_list, null);
            addContentView(view, listView_tickets.getLayoutParams());
            listView_tickets.setEmptyView(view);
            expiredTicketDatabase = new ExpiredTicketDatabase(this);
            expiredTickets = expiredTicketDatabase.getData(type);
            if (expiredTickets.isEmpty())
                textView_empty.setVisibility(View.VISIBLE);
            else
                textView_empty.setVisibility(View.GONE);

            expiredTicketListAdapter = new ExpiredTicketListAdapter(getApplicationContext(), expiredTickets);
            listView_tickets.setAdapter(expiredTicketListAdapter);

            listView_tickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListOfTickets.this, ExpiredTicketView.class);
                    intent.putExtra("expiredtickets", expiredTickets.get(position));
                    startActivity(intent);
                }
            });
        }
        if (validity.equals("verification_code")) {
            View view = getLayoutInflater().inflate(R.layout.empty_list, null);
            addContentView(view, listView_tickets.getLayoutParams());
            listView_tickets.setEmptyView(view);

            verificationCodeDatabase = new VerificationCodeDatabase(this);
            verificationCodes = verificationCodeDatabase.getData(type);
            if (verificationCodes.isEmpty())
                textView_empty.setVisibility(View.VISIBLE);
            else
                textView_empty.setVisibility(View.GONE);

            verificationCodeListAdapter = new VerificationCodeListAdapter
                    (getApplicationContext(), verificationCodes);
            listView_tickets.setAdapter(verificationCodeListAdapter);
            listView_tickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListOfTickets.this, VerificationCodeView.class);
                    intent.putExtra("verificationcode", verificationCodes.get(position));
                    startActivity(intent);
                }
            });
        }
    }

//    class GetActiveTickets extends AsyncTask<Void, Void, Void> {
//
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                Log.e("Check", "Started");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getActiveTickets"));
//                String id = sharedPreferences.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                if (msgcontent.has("responseInfo")) {
//                    JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//                    activeTicketDatabase.deleteData();
//                    activeTicketListAdapter.notifyDataSetChanged();
//                    for (int i = 0; i < responseInfo.length(); i++) {
//                        JSONObject activatedTicket = responseInfo.getJSONObject(i);
//                        String ticket_id = activatedTicket.getString("id");
//                        String ticket_no = activatedTicket.getString("ticket_number");
//                        String ticket_code = activatedTicket.getString("ticket_code");
//                        String from_station = activatedTicket.getString("from_station");
//                        String to_station = activatedTicket.getString("to_station");
//                        String ticket_type = activatedTicket.getString("ticket_type");
//                        String no_of_tickets = activatedTicket.getString("no_of_tickets");
//                        String ticket_category = activatedTicket.getString("ticket_category");
//                        String ticket_period = activatedTicket.getString("ticket_period");
//                        String ticket_amount = activatedTicket.getString("ticket_amount");
//                        String purchased_date = activatedTicket.getString("purchased_on");
//                        String activated_date = activatedTicket.getString("activated_on");
//                        String activated_station = activatedTicket.getString("activated_station_code");
//                        String valid_date = activatedTicket.getString("valid_till");
//                        String proof_document = activatedTicket.getString("proof_document");
//                        String photo = activatedTicket.getString("upload_photo");
//                        String validated_count = activatedTicket.getString("validated_count");
//                        String imei_device = activatedTicket.getString("imei_device");
//
//                        boolean check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
//                                from_station, to_station, ticket_type,no_of_tickets,
//                                ticket_category, ticket_period, ticket_amount,
//                                purchased_date, activated_date, activated_station,
//                                valid_date,proof_document,photo,validated_count,imei_device);
//                        Log.e("InsertionCheck", check + "");
//                    }
//                    if (validity.equals("active")) {
//                        activeTickets.clear();
//                        activeTickets = activeTicketDatabase.getData(type);
//                        activeTicketListAdapter.notifyDataSetChanged();
//                    }
//                    swipeRefreshLayout.setRefreshing(false);
//                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//        }
//    }

    public void getActiveTickets() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    parseActiveTickets(response);
                    pDialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    sendBroadcast();
                } catch (Exception e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                    pDialog.hide();
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                swipeRefreshLayout.setRefreshing(false);
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getActiveTickets");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
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

    public void parseActiveTickets(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            if (msgcontent.has("responseInfo")) {
                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
                activeTicketDatabase.deleteData();
                activeTicketListAdapter.notifyDataSetChanged();
                for (int i = 0; i < responseInfo.length(); i++) {
                    JSONObject activatedTicket = responseInfo.getJSONObject(i);
                    String ticket_id = activatedTicket.getString("id");
                    String ticket_no = activatedTicket.getString("ticket_number");
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

                    boolean check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
                            from_station, to_station, ticket_type, no_of_tickets,
                            ticket_category, ticket_period, ticket_amount,
                            purchased_date, activated_date, activated_station,
                            valid_date, proof_document, photo, validated_count, imei_device);
                    Log.e("InsertionCheck", check + "");
                }
                if (validity.equals("active")) {
                    activeTickets.clear();
                    activeTickets = activeTicketDatabase.getData(type);
                    activeTicketListAdapter.dataSetChanges(activeTickets);
                }

                activeTickets.clear();
                activeTickets = activeTicketDatabase.getData(type);
                activeTicketListAdapter.dataSetChanges(activeTickets);
                swipeRefreshLayout.setRefreshing(false);
                notifyUser("Updated");
            }
        } catch (Exception e) {
            Log.e("Check", "Exception");
            e.printStackTrace();
        }
    }

//    class GetExpiredTickets extends AsyncTask<Void, Void, Void> {
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                Log.e("Check", "Started");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getExpiredTickets"));
//                String id = sharedPreferences.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                if (msgcontent.has("responseInfo")) {
//                    JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//                    expiredTicketDatabase.deleteData();
//                    expiredTicketListAdapter.notifyDataSetChanged();
//                    for (int i = 0; i < responseInfo.length(); i++) {
//                        JSONObject expiredTicket = responseInfo.getJSONObject(i);
//                        String ticket_id = expiredTicket.getString("id");
//                        String ticket_no = expiredTicket.getString("ticket_code");
//                        String ticket_code = expiredTicket.getString("ticket_code");
//                        String from_station = expiredTicket.getString("from_station");
//                        String to_station = expiredTicket.getString("to_station");
//                        String ticket_type = expiredTicket.getString("ticket_type");
//                        String no_of_tickets = expiredTicket.getString("no_of_tickets");
//                        String ticket_category = expiredTicket.getString("ticket_category");
//                        String ticket_period = expiredTicket.getString("ticket_period");
//                        String ticket_amount = expiredTicket.getString("ticket_amount");
//                        String purchased_date = expiredTicket.getString("purchased_on");
//                        String activated_date = expiredTicket.getString("activated_on");
//                        String activated_station = expiredTicket.getString("activated_station_code");
//                        String valid_date = expiredTicket.getString("valid_till");
//                        String expired_date = expiredTicket.getString("expiredDate");
//                        String proof_document = expiredTicket.getString("proof_document");
//                        String photo = expiredTicket.getString("upload_photo");
//                        String imei_device = expiredTicket.getString("imei_device");
//
//                        boolean check = expiredTicketDatabase.insertExpiredTicket(ticket_id, ticket_no, ticket_code,
//                                from_station, to_station, ticket_type,no_of_tickets,
//                                ticket_category, ticket_period, ticket_amount,
//                                purchased_date, activated_date, activated_station,
//                                valid_date, expired_date,proof_document,photo,imei_device);
//                        Log.e("InsertionCheck", check + "");
//                    }
//                    if (validity.equals("expired")) {
//                        expiredTickets.clear();
//                        expiredTickets = expiredTicketDatabase.getData(type);
//                        expiredTicketListAdapter.notifyDataSetChanged();
//                    }
//                    swipeRefreshLayout.setRefreshing(false);
//                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//        }
//    }

    public void getExpiredTickets() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    parseExpiredTickets(response);
                    pDialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    sendBroadcast();
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                swipeRefreshLayout.setRefreshing(false);
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getExpiredTickets");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
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

    public void parseExpiredTickets(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            if (msgcontent.has("responseInfo")) {
                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
                expiredTicketDatabase.deleteData();
                expiredTicketListAdapter.notifyDataSetChanged();
                for (int i = 0; i < responseInfo.length(); i++) {
                    JSONObject expiredTicket = responseInfo.getJSONObject(i);
                    String ticket_id = expiredTicket.getString("id");
                    String ticket_no = expiredTicket.getString("ticket_code");
                    String ticket_code = expiredTicket.getString("ticket_code");
                    String from_station = expiredTicket.getString("from_station");
                    String to_station = expiredTicket.getString("to_station");
                    String ticket_type = expiredTicket.getString("ticket_type");
                    String no_of_tickets = expiredTicket.getString("no_of_tickets");
                    String ticket_category = expiredTicket.getString("ticket_category");
                    String ticket_period = expiredTicket.getString("ticket_period");
                    String ticket_amount = expiredTicket.getString("ticket_amount");
                    String purchased_date = expiredTicket.getString("purchased_on");
                    String activated_date = expiredTicket.getString("activated_on");
                    String activated_station = expiredTicket.getString("activated_station_code");
                    String valid_date = expiredTicket.getString("valid_till");
                    String expired_date = expiredTicket.getString("expiredDate");
                    String proof_document = expiredTicket.getString("proof_document");
                    String photo = expiredTicket.getString("upload_photo");
                    String imei_device = expiredTicket.getString("imei_device");

                    boolean check = expiredTicketDatabase.insertExpiredTicket(ticket_id, ticket_no, ticket_code,
                            from_station, to_station, ticket_type, no_of_tickets,
                            ticket_category, ticket_period, ticket_amount,
                            purchased_date, activated_date, activated_station,
                            valid_date, expired_date, proof_document, photo, imei_device);
                    Log.e("InsertionCheck", check + "");
                }
                if (validity.equals("expired")) {
                    expiredTickets.clear();
                    expiredTickets = expiredTicketDatabase.getData(type);
                    expiredTicketListAdapter.dataSetChanges(expiredTickets);
                }

                expiredTickets.clear();
                expiredTickets = expiredTicketDatabase.getData(type);
                expiredTicketListAdapter.dataSetChanges(expiredTickets);
                swipeRefreshLayout.setRefreshing(false);
                notifyUser("Updated");
            }
        } catch (Exception e) {
            Log.e("Check", "Exception");
            e.printStackTrace();
        }
    }

//    class GetVerificationCodes extends AsyncTask<Void, Void, Void> {
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                Log.e("Check", "Started");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getInActiveTickets"));
//                String id = sharedPreferences.getString("user_id", "0");
//                params.add(new BasicNameValuePair("userid", id));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                // JSONObject requestParam = msgcontent.getJSONObject("requestParam");
//
//                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//                Log.e("CheckLength", responseInfo.length() + "");
//                verificationCodeDatabase.deleteData();
//                verificationCodeListAdapter.notifyDataSetChanged();
//                for (int i = 0; i < responseInfo.length(); i++) {
//                    String id = "";
//                    String ticket_no = "";
//                    String ticket_code = "";
//                    String from_station = "";
//                    String to_station = "";
//                    String ticket_type = "";
//                    String ticket_category = "";
//                    String ticket_period = "";
//                    String ticket_amount = "";
//                    String purchased_on = "";
//                    String no_of_tickets = "";
//                    JSONObject passengetTicket = responseInfo.getJSONObject(i);
////                    if(!(passengetTicket.getString("ticket_number").equals("")))
////                        ticket_no = passengetTicket.getString("ticket_number");
//                    if (!(passengetTicket.getString("from_station").equals("")))
//                        from_station = passengetTicket.getString("from_station");
//                    if (!(passengetTicket.getString("ticket_type").equals("")))
//                        ticket_type = passengetTicket.getString("ticket_type");
//                    if (!(passengetTicket.getString("ticket_amount").equals("")))
//                        ticket_amount = passengetTicket.getString("ticket_amount");
//                    if (!(passengetTicket.getString("ticket_code").equals("")))
//                        ticket_code = passengetTicket.getString("ticket_code");
//                    if (!(passengetTicket.getString("id").equals("")))
//                        id = passengetTicket.getString("id");
//                    if (!(passengetTicket.getString("purchased_on").equals("")))
//                        purchased_on = passengetTicket.getString("purchased_on");
//                    if (!(passengetTicket.getString("ticket_period").equals("")))
//                        ticket_period = passengetTicket.getString("ticket_period");
//                    if(!(passengetTicket.getString("no_of_tickets").equals("")))
//                        no_of_tickets = passengetTicket.getString("no_of_tickets");
//                    if (!(passengetTicket.getString("ticket_category").equals("")))
//                        ticket_category = passengetTicket.getString("ticket_category");
//                    if (!(passengetTicket.getString("to_station").equals("")))
//                        to_station = passengetTicket.getString("to_station");
//
//                    String proof_document = passengetTicket.getString("proof_document");
//                    String photo = passengetTicket.getString("upload_photo");
//
//                    boolean check = verificationCodeDatabase.insertStation(id, ticket_no, ticket_code,
//                            from_station, to_station, ticket_type, no_of_tickets,ticket_category,
//                            ticket_period, ticket_amount, purchased_on,proof_document,photo);
//
//                    if (check) {
//                        if (validity.equals("verification_code")) {
//                            verificationCodes.clear();
//                            verificationCodes = verificationCodeDatabase.getData(type);
//                            verificationCodeListAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
//                swipeRefreshLayout.setRefreshing(false);
//
//            } catch (Exception e) {
//                Log.e("Check", "Exception");
//                e.printStackTrace();
//            }
//        }
//    }

    public void getVerificationCodes() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    parseVerificationCodes(response);
                    pDialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    sendBroadcast();
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                swipeRefreshLayout.setRefreshing(false);
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getInActiveTickets");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
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

    public void parseVerificationCodes(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            // JSONObject requestParam = msgcontent.getJSONObject("requestParam");

            JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
            Log.e("CheckLength", responseInfo.length() + "");
            verificationCodeDatabase.deleteData();
            verificationCodeListAdapter.notifyDataSetChanged();
            for (int i = 0; i < responseInfo.length(); i++) {
                String id = "";
                String ticket_no = "";
                String ticket_code = "";
                String from_station = "";
                String to_station = "";
                String ticket_type = "";
                String ticket_category = "";
                String ticket_period = "";
                String ticket_amount = "";
                String purchased_on = "";
                String no_of_tickets = "";
                JSONObject passengetTicket = responseInfo.getJSONObject(i);
//                    if(!(passengetTicket.getString("ticket_number").equals("")))
//                        ticket_no = passengetTicket.getString("ticket_number");
                if (!(passengetTicket.getString("from_station").equals("")))
                    from_station = passengetTicket.getString("from_station");
                if (!(passengetTicket.getString("ticket_type").equals("")))
                    ticket_type = passengetTicket.getString("ticket_type");
                if (!(passengetTicket.getString("ticket_amount").equals("")))
                    ticket_amount = passengetTicket.getString("ticket_amount");
                if (!(passengetTicket.getString("ticket_code").equals("")))
                    ticket_code = passengetTicket.getString("ticket_code");
                if (!(passengetTicket.getString("id").equals("")))
                    id = passengetTicket.getString("id");
                if (!(passengetTicket.getString("purchased_on").equals("")))
                    purchased_on = passengetTicket.getString("purchased_on");
                if (!(passengetTicket.getString("ticket_period").equals("")))
                    ticket_period = passengetTicket.getString("ticket_period");
                if (!(passengetTicket.getString("no_of_tickets").equals("")))
                    no_of_tickets = passengetTicket.getString("no_of_tickets");
                if (!(passengetTicket.getString("ticket_category").equals("")))
                    ticket_category = passengetTicket.getString("ticket_category");
                if (!(passengetTicket.getString("to_station").equals("")))
                    to_station = passengetTicket.getString("to_station");

                String proof_document = passengetTicket.getString("proof_document");
                String photo = passengetTicket.getString("upload_photo");

                boolean check = verificationCodeDatabase.insertStation(id, ticket_no, ticket_code,
                        from_station, to_station, ticket_type, no_of_tickets, ticket_category,
                        ticket_period, ticket_amount, purchased_on, proof_document, photo);

                if (check) {
                    if (validity.equals("verification_code")) {
                        verificationCodes.clear();
                        verificationCodes = verificationCodeDatabase.getData(type);
                        verificationCodeListAdapter.dataSetChanges(verificationCodes);
                    }
                }
            }

            verificationCodes.clear();
            verificationCodes = verificationCodeDatabase.getData(type);
            verificationCodeListAdapter.dataSetChanges(verificationCodes);
            notifyUser("Updated");
            swipeRefreshLayout.setRefreshing(false);

        } catch (Exception e) {
            Log.e("Check", "Exception");
            e.printStackTrace();
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public void sendBroadcast(){
        Intent intent = new Intent("UpdateTickets");
        sendBroadcast(intent);
    }
}
//{"message":null,
// "msgcontent":
// {"requestParam":
// {"actiontype":"getInActiveTickets","userid":"10","passengerid":"9"},
// "responseInfo":[

// {"id":"2","from_station":"9","to_station":"0","ticket_type":"Platform","ticket_category":null,
// "ticket_period":null,"ticket_code":"2RD01D","ticket_amount":"10","purchased_on":"2015-08-18 07:08:49",
// "status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},

// {"id":"3","from_station":"12","to_station":"0","ticket_type":"Platform","ticket_category":null,
// "ticket_period":null,"ticket_code":"0P00A1","ticket_amount":"10","purchased_on":"2015-08-18 07:08:31",
// "status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},

// {"id":"4","from_station":"22","to_station":"3","ticket_type":"","ticket_category":"II Class","ticket_period":null,
// "ticket_code":"C02D2U","ticket_amount":"10","purchased_on":"2015-08-18 09:08:44",
// "status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},

// {"id":"5","from_station":"16","to_station":"22","ticket_type":"","ticket_category":"I Class","ticket_period":null,
// "ticket_code":"2DA101","ticket_amount":"10","purchased_on":"2015-08-18 09:08:38","status_date":"0000-00-00 00:00:00",
// "inactivatedDate":"0000-00-00 00:00:00"},

// {"id":"6","from_station":"16","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,
// "ticket_code":"H0D102","ticket_amount":"10","purchased_on":"2015-08-18 10:08:59","status_date":"0000-00-00 00:00:00",
// "inactivatedDate":"0000-00-00 00:00:00"},

// {"id":"7","from_station":"12","to_station":"9","ticket_type":"","ticket_category":"I Class","ticket_period":null,
// "ticket_code":"A01UB0","ticket_amount":"20","purchased_on":"2015-08-18 10:08:45","status_date":"0000-00-00 00:00:00",
// "inactivatedDate":"0000-00-00 00:00:00"},
//
// {"id":"8","from_station":"16","to_station":"9","ticket_type":"","ticket_category":"II Class","ticket_period":null,
// "ticket_code":"22HAU0","ticket_amount":"10","purchased_on":"2015-08-18 10:08:20","status_date":"0000-00-00 00:00:00",
// "inactivatedDate":"0000-00-00 00:00:00"},
//
// {"id":"9","from_station":"22","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,
// "ticket_code":"0CD00P","ticket_amount":"10","purchased_on":"2015-08-18 10:08:04","status_date":"0000-00-00 00:00:00",
// "inactivatedDate":"0000-00-00 00:00:00"},
//
// {"id":"10","from_station":"22","to_station":"16","ticket_type":"",
// "ticket_category":"II Class","ticket_period":null,"ticket_code":"200C1U","ticket_amount":"20",
// "purchased_on":"2015-08-18 10:08:20","status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},
//
// {"id":"11","from_station":"16","to_station":"24","ticket_type":"MonthlyPass","ticket_category":"II Class",
// "ticket_period":"Monthly","ticket_code":"10A2MH","ticket_amount":"20","purchased_on":"2015-08-18 10:08:36",
// "status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},
//
// {"id":"12","from_station":"22","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_code":"021C00","ticket_amount":"20","purchased_on":"2015-08-18 11:08:41","status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},{"id":"13","from_station":"24","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_code":"Y0BR00","ticket_amount":"0","purchased_on":"2015-08-18 11:08:05","status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},{"id":"14","from_station":"1","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_code":"1GC20C","ticket_amount":"10","purchased_on":"2015-08-18 11:08:47","status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},{"id":"15","from_station":"1","to_station":"22","ticket_type":"","ticket_category":"II Class","ticket_period":null,"ticket_code":"020C2G","ticket_amount":"20","purchased_on":"2015-08-18 14:08:53","status_date":"0000-00-00 00:00:00","inactivatedDate":"0000-00-00 00:00:00"},{"id":"16","from_station":"1","to_station":"12","ticket_type":"MonthlyPass","ticket_catego


///////ACTIVETICKETS\\\\
// {"message":null,
// "msgcontent":{"requestParam":{"actiontype":"getActiveTickets","userid":"10","passengerid":"9"},"responseInfo":[
// {"id":"2","from_station":"9","to_station":"0","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_number":"DDRP01002","ticket_code":"2RD01D","ticket_amount":"10","purchased_on":"2015-08-18 07:08:49","activated_on":"2015-08-18 23:27:42","activated_station_code":"0","valid_till":"2015-08-19 01:27:42","status_date":"2015-08-18 23:27:42","activatedDate":"2015-08-18 23:27:42"}]}}

//EXPIREDTICKETS\\\\
//{"message":null,
// "msgcontent":
// {"requestParam":{"actiontype":"getExpiredTickets","userid":"10","passengerid":"9"},
// "responseInfo":[
// {"id":"5","from_station":"16","to_station":"22","ticket_type":"","ticket_category":"I Class","ticket_period":null,"ticket_code":"2DA101","ticket_amount":"10","purchased_on":"2015-08-18 09:08:38","activated_on":"0000-00-00 00:00:00","activated_station_code":"0","valid_till":"0000-00-00 00:00:00","status_date":"2015-08-19 02:11:26","expiredDate":"2015-08-19 02:11:26"}
//,{"id":"8","from_station":"16","to_station":"9","ticket_type":"","ticket_category":"II Class","ticket_period":null,"ticket_code":"22HAU0","ticket_amount":"10","purchased_on":"2015-08-18 10:08:20","activated_on":"0000-00-00 00:00:00","activated_station_code":"0","valid_till":"0000-00-00 00:00:00","status_date":"2015-08-19 02:11:26","expiredDate":"2015-08-19 02:11:26"}]}}
