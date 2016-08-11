package com.trail.octo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VolleyRequestData;
import com.database.VerificationCodeDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentWebViewActivity extends Activity {
    public static final String TAG = "TicketInfo";

    //    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    WebView webView_payment_gateway;
    String url = "";
    String postData = "";
    PopupWindow popupWindow;
    View view;
    ProgressDialog progrDialog;
    String ticket_no = "";
    String no_of_tickets = "";
    SharedPreferences sharedPreferences;
    String from_station, to_station, ticket_type, ticket_category, ticket_period,
            ticket_amount, userid, passengerid, firstname, email, amount, phone;
    String proof_document, photo;
    VerificationCodeDatabase verificationCodeDatabase;
    String ticket_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);
        verificationCodeDatabase = new VerificationCodeDatabase(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        url = "http://goeticket.com/ticketingsystem/purchase/index/4/3";
        firstname = sharedPreferences.getString("user_name", "User");
        email = sharedPreferences.getString("email_id", "octo@octo.com");
        phone = sharedPreferences.getString("mobile_no", "1234567890");
        userid = sharedPreferences.getString("user_id", "0");
        passengerid = sharedPreferences.getString("passenger_id", "0");
        ;

        no_of_tickets = getIntent().getStringExtra("no_of_tickets");
        Log.e("No_of_tickets", no_of_tickets + "");
        ticket_type = getIntent().getStringExtra("ticket_type");
        from_station = getIntent().getStringExtra("from_station");
        ticket_amount = getIntent().getStringExtra("ticket_amount");
        photo = getIntent().getStringExtra("upload_photo");
        proof_document = getIntent().getStringExtra("proof_document");
        amount = ticket_amount;

        if (ticket_type.equals("Platform")) {
            to_station = "";
            ticket_category = "";
            ticket_period = "";
        } else if (ticket_type.equals("MonthlyPass")) {
            to_station = getIntent().getStringExtra("to_station");
            ticket_category = getIntent().getStringExtra("ticket_category");
            ticket_period = getIntent().getStringExtra("ticket_period");
        } else if (ticket_type.equals("Unreserved Ticket")) {
            to_station = getIntent().getStringExtra("to_station");
            ticket_category = getIntent().getStringExtra("ticket_category");
            ticket_period = "";
        }
        //2015-09-12 15--35--49
        Calendar calendar = Calendar.getInstance();
        String purchased_on = (calendar.getTime().getYear() + 1900) + "-" + (calendar.getTime().getMonth() + 1) + "-" +
                calendar.getTime().getDate() + " " + calendar.getTime().getHours() + "--" + calendar.getTime().getMinutes() +
                "--" + calendar.getTime().getSeconds();
        postData = "from_station=" + from_station + "&to_station=" + to_station + "&ticket_type=" + ticket_type + "" +
                "&ticket_category=" + ticket_category + "&ticket_period=" + ticket_period +
                "&ticket_amount=" + ticket_amount + "&" +
                "userid=" + userid + "&passengerid=" + passengerid + "&firstname=" + firstname + "&email=" + email + "&" +
                "amount=" + 11 + "&phone=" + phone + "&no_of_tickets=" + no_of_tickets +
                "&purchased_on=" + purchased_on + "" + "&proof_document=" + proof_document;
        Log.e("postData", postData);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        progrDialog = new ProgressDialog(this);
        webView_payment_gateway = (WebView) findViewById(R.id.webView_payment_gateway);
        //webView_payment_gateway.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView_payment_gateway.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView_payment_gateway.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.e("URL", url);
                    }

                    @Override
                    public void onLoadResource(WebView view, String url) {
                        super.onLoadResource(view, url);
                        Log.e("Loading URL", url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.e("Finished URL", url);
                        if (url.startsWith("http://goeticket.com/ticketingsystem/purchase/showsuccess?status=success")) {
//status=success&txnid=22669591941685e04d72&amount=1.0&ticketnumber=//mber=
                            ticket_no = url.subSequence(url.length() - 18, url.length()).toString();
                            if (ticket_no.contains("=")) {
                                ticket_no = ticket_no.replaceAll(".*=", "");
                            }
                            Log.e("Ticket No", ticket_no);
//                            new BuyTicket().execute();
                            getTicketInfo();
//                            Intent intent = new Intent(getApplicationContext(), TransanctionLog.class);
//                            startActivity(intent);
                        }
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                }
        );

//        byte[] data = Base64.decode(postData,Base64.DEFAULT);
//        String finalData = "";
//
//        try {
//            finalData = URLEncoder.encode(postData, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        webView_payment_gateway.postUrl(url, postData.getBytes());

//        webView_payment_gateway.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_payment_gateway.canGoBack()) {
//            webView_payment_gateway.goBack();
//            return true;
//        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

//    class BuyTicket extends AsyncTask<Void, Void, Void> {
//        JSONObject jsonObject;
//        String message = "";
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
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//                params.add(new BasicNameValuePair("actiontype", "getTicketInfo"));
//                params.add(new BasicNameValuePair("ticketnumber", ticket_no));
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
//            if (message.equals("Success")) {
//                try {
//                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                    //JSONObject requestParam= msgcontent.getJSONObject("requestParam");
//                    JSONObject passengetTicket = msgcontent.getJSONObject("responseInfo");
//
//
//                    //String ticket_no = passengetTicket.getString("ticket_number");
//                    String from_station = passengetTicket.getString("from_station");
//                    String ticket_type = passengetTicket.getString("ticket_type");
//                    String ticket_amount = passengetTicket.getString("ticket_amount");
//                    String ticket_code = passengetTicket.getString("ticket_code");
//                    String id = passengetTicket.getString("id");
//                    String no_of_tickets = passengetTicket.getString("no_of_tickets");
//                    String purchased_on = passengetTicket.getString("purchased_on");
//                    String ticket_period = passengetTicket.getString("ticket_period");
//
//                    String ticket_category = passengetTicket.getString("ticket_category");
//                    String to_station = passengetTicket.getString("to_station");
//                    String proof_document = passengetTicket.getString("proof_document");
//                    //String photo = passengetTicket.getString("upload_photo");
//
//                    boolean check = verificationCodeDatabase.insertStation(id, ticket_no, ticket_code, from_station, to_station, ticket_type, no_of_tickets, ticket_category,
//                            ticket_period, ticket_amount, purchased_on, proof_document, photo);
//                    Log.e("InsertionCheck", check + "");
//                    ticket_id = id;
//
//                    new UploadImage().execute();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//        }
//    }

    public void getTicketInfo() {
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
                    if (message.equals("Success")) {
                        parseTicketInfo(response);
                    } else
                        notifyUser("" + message);
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
                params.put("actiontype", "getTicketInfo");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("ticketnumber", ticket_no);
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

    public void parseTicketInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            //JSONObject requestParam= msgcontent.getJSONObject("requestParam");
            JSONObject passengetTicket = msgcontent.getJSONObject("responseInfo");


            //String ticket_no = passengetTicket.getString("ticket_number");
            String from_station = passengetTicket.getString("from_station");
            String ticket_type = passengetTicket.getString("ticket_type");
            String ticket_amount = passengetTicket.getString("ticket_amount");
            String ticket_code = passengetTicket.getString("ticket_code");
            String id = passengetTicket.getString("id");
            String no_of_tickets = passengetTicket.getString("no_of_tickets");
            String purchased_on = passengetTicket.getString("purchased_on");
            String ticket_period = passengetTicket.getString("ticket_period");

            String ticket_category = passengetTicket.getString("ticket_category");
            String to_station = passengetTicket.getString("to_station");
            String proof_document = passengetTicket.getString("proof_document");
            //String photo = passengetTicket.getString("upload_photo");

            boolean check = verificationCodeDatabase.insertStation(id, ticket_no, ticket_code, from_station, to_station, ticket_type, no_of_tickets, ticket_category,
                    ticket_period, ticket_amount, purchased_on, proof_document, photo);
            Log.e("InsertionCheck", check + "");
            ticket_id = id;

            uplaodImage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showExitAlert();
    }

    public void showExitAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Are you sure to stop the payment and go back?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(PaymentWebViewActivity.this, PurchaseTickets.class);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

//    class UploadImage extends AsyncTask<Void, Void, Void> {
//        JSONObject jsonObject;
//        String message = "";
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Log.e("Check", "Uploading image");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//                params.add(new BasicNameValuePair("ticketid", "" + ticket_id));
//                params.add(new BasicNameValuePair("actiontype", "uploadTicketPhoto"));
//                params.add(new BasicNameValuePair("upload_photo", "" + photo));
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
//            if (message.equals("Created")) {
//                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(PaymentWebViewActivity.this, Home.class);
//                startActivity(intent);
//            } else {
//                new UploadImage().execute();
//            }
//        }
//    }

    public void uplaodImage() {
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
                    if (message.equals("Created")) {
                        notifyUser("Success");
                        Intent intent = new Intent(PaymentWebViewActivity.this, Home.class);
                        startActivity(intent);
                    } else {
                        uplaodImage();
                    }
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
                params.put("actiontype", "uploadTicketPhoto");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("ticketid", "" + ticket_id);
                params.put("upload_photo", "" + photo);
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

}

//from_station = <from_station_id>;
//to_station = <to_station_id>;
//ticket_type = <ticket_type>;
//ticket_category = <ticket_category>;
//ticket_period = <ticket_period>;
//ticket_amount =<ticket_amount>;
//userid = <userid>
//passengerid = <passengerid>
//firstname = <passengername>;
//email =  <passengeremail>
//amount = <amount>
//phone = <passengerphone>

//{"message":"Success",
// "msgcontent":
// {"requestParam":
// {"userid":"10","actiontype":"getTicketInfo","ticketnumber":"1021PF"},
// "responseInfo":
// {"id":"46","passengerid":"1","from_station":"19","to_station":"19","ticket_type":"Platform",
// "ticket_category":null,"ticket_period":null,"ticket_code":"1120PF","ticket_amount":"1",
// "purchased_on":"2015-08-26 06:08:26","status_date":"0000-00-00 00:00:00",
// "txnid":"7c15bc81d9133fd98d80","inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}


//{"message":"Success",
// "msgcontent":
// {"requestParam":
// {"userid":"10","actiontype":"getTicketInfo","ticketnumber":"1004UR"}
// ,"responseInfo":{"id":"47","passengerid":"9","from_station":"1","to_station":"2",
// "ticket_type":"Unreserved Ticket","ticket_category":"I Class","ticket_period":null,
// "ticket_code":"0R4U01","ticket_amount":"1","purchased_on":"2015-08-26 07:08:00",
// "status_date":"0000-00-00 00:00:00","txnid":"3ec57a113b8a81a0e9e7",
// "inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}

// {"message":"Error"
// ,"msgcontent":
// {"requestParam":
// {"userid":"52","actiontype":"getTicketInfo","ticketnumber":"1044PF"},
// "responseInfo":"Invalid Ticket"}}


// {"message":"Success","msgcontent":{"requestParam":{"userid":"52","actiontype":"getTicketInfo","ticketnumber":"CSMUMADH1046PF"},"responseInfo":{"id":"226","passengerid":"30","from_station":"16","to_station":"16","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"ticket_code":"SPMF1C","ticket_amount":"1","purchased_on":"2015-09-02 11:09:25","status_date":"0000-00-00 00:00:00","txnid":"3b002343d585b5c3f95a","inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}
//CSMUMADH1063UR

//{"message":"Created","msgcontent":{"requestParam":{"userid":"57","ticketid":"356","actiontype":"uploadTicketPhoto","upload_photo":"iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAA3NCSVQICAjb4U\/gAAAAW0lEQVQI\nmQFQAK\/\/AO\/49+fo6f36\/f\/8\/\/\/\/\/wH\/\/\/9FTU8zGhAjMj5lZ2MCAP8AJBcUFwT+08W84eboAjpO\nWRkhH9\/n6AYC\/BsVFAMjHx3m7fHp9PQAGS\/2DBRCpChzFqOESQAAAABJRU5ErkJggg==\n"},"responseInfo":null}}


//{"message":"Success","msgcontent":{"requestParam":{"userid":"57","actiontype":"getTicketInfo","ticketnumber":"CSMUMCCG1111PF"},"responseInfo":{"id":"360","passengerid":"33","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_code":"F1CUC1","ticket_amount":"11","purchased_on":"2016-06-15 12:38:16","proof_document":"pancard","upload_photo":"","status_date":"0000-00-00 00:00:00","txnid":"ba890848b80dd3f453f0","validated_count":"0","inactivatedDate":"0000-00-00 00:00:00","status":"Inactive"}}}
//{"message":"Created","msgcontent":{"requestParam":{"userid":"57","ticketid":"360","actiontype":"uploadTicketPhoto","upload_photo":"iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD\/gAIDAAAAA3NCSVQICAjb4U\/gAAAgAElEQVR4\nnMy96bNlx3En9svMWs6569u6GyQIgiAALpIoyRKkkTTSTMgjhe3w2P44\/uIIf\/Af4D\/JEXYoQuOY\n8Dg8UoxmLM9ESCNqo0hTEilAXEAAjWavb7nLObVl+sN57\/VrLKRIQCFndHTfe8+pPFW\/k5VbVVbT\n6ekp3kN29Ufee+XDyLMHoKbCQiCDMjXAAxUwAE1hYMcCGMCXzRgwgAAAChDAgD7L+vKqEUwElfjp\npXZ1dWIyNXRAm575LE2c5epZduN3vvpKV2z5xj30tK27emoB8yUzAxTTlb8jFS0EYmIzq1Y9+6t+\n+ekG4Xr5YKLLUQnQroZ6E5qpXQXkGqkKMmNX3z\/+qYk9y+QmENcD1isg3k\/6zBu87Nj0AujqgwCX\nkFiFyeXteuMx10+iD4VJSNSUiJgYgMGYmIgAVK03rrrLYVyPpF115eYj7Go8dPWGAZB72p9rMdSr\nq9cMBVCg3mB7LaR0ddWefSiu+FyDcv2S6AZYV89yMECu5ptWwEAMKmgExMv33K6k8f1gsZiamqqp\nkDh2RGRmqSUALCwspRUldfw+WbUbHdVn34rdkKxruv7K7+uM3pCa67Z29fUatWtxvikNeoOJ3MDI\nbvC8BKsBUEy6wBgwmKHxZdf0WTl\/H165ZSGZJiCmxmZNW+e63PJ0jxf\/tMG1Vrrut95A7T3T\/1rM\nr1HQD9Kk7+FAV7IwNadnOXzQK7+8+T2PvtYJVy9pul5hBiKwgwqgIIP5S+4AUACGydOu3OhxswbA\nsRMSwyVkAIKED+jW9Wu8qbDoxqjaFY7vEZ8P0wbX0OPZD++fDXZj0l2LzLX+vsm8gZhAMJiQXL9s\nOn34\/RvzfmrBl4r5\/UL7fvl\/lpj4gzHCh0xkfd+bbz+abXmG7Apr\/VC98ZTaDcV0RUFCaUVYhC5R\nn\/TvtYK3+tSIG1+2roCgTVAJQD\/YjWBiJiaQ8IffZzeg5ytV+p7b6RIpIZkEdrIbapcCef37ZDrs\nPT7CVfMf6vQwsYkZ7GafJ60aXbxkdVPWCACcGQQoCiU4BsEIk42ul5IlDCXU94pVlAggtzzJ6g+C\n6eYjJ0+rfsB4mNixmzTdhAhuwHT9tXPdWMfpBs\/eYFXrFXuKLo51\/MG9CBKYPlzq3m9YrsilBgDE\n4EnFX995abw9WgNVsICeGd9k7xy7ycP6YPbX1t3e97u9t09qOg2biW9i5Ng5dk1b0TI9yLNn4mat\nWZsgxpWsTVcnPpNpnhia2TVPun7wtb\/yw9TLZU+mf0zBBmNUwBEIMAMD0HIFoQIFJoC\/+SxCZYJB\nmhKuJPkp0Q2PiZ\/98ek3wpVNUNMJjijxUlkAAKrWqpWJJ\/md3JHp89RcTYlpUjTCHyDmRJdXn1Hw\nP1SvfSBYANqVs1MVXt4niQqwgJ7BwgDAA41AQqAPkd5LOdIP7tkEk5AQUdP2gfZBSESeGf9koa4l\nWj5MS9GNp9+85Vmf4O9IzrFBXYPa1G2DMZrCvWdgxADBFNcvjQGFWjGAaZKIG925bn7tbb4\/Xruy\nDNMEmabb9Pvk\/V8LF33oe7ju3rMm\/frzTavyg3lcRwgfeJsBBpoCac9eTZu1qrW2Sg1GgCDa1Ner\nTjAuHYtne+8Z8hRcAiJwI2r9EGkPEiZQJgH54Yh8GNGz3t+192A3LuHZq9cNbzp617jgqud2w\/ub\npqFjN6kDMfHsyRMTGatWKJQZ0Nqq6mVIoeAEFVy5akLgZ4ZpQAH5p9HfjTCT5dIVmATKsyeiSbLo\n7zgr6FkRuMn\/erLfDH1wZYJxIz9xzQTv80ifnQGqWuuVtR02wzP9uCa+MYOuf1eoam0ZWiFh4nXd\nQgiOYSDAMRUgPo1cmQD6wLTPFFFOco2rNzcBSqAfX9xuRn\/vo4k5riJ\/M7vpsk3PNbOqtVkjkGNX\ntDwL1vst6PulFCDQtRqeQsKiRZu2Vqu26SoRgAy4SweUCXDPBFx4L08ARDTJ2jXnqpWIPiAI\/2g0\nMb98NJGa5pajxMkfulZK7+3kMAzAUyH6QC98UsN\/9340bVfvqebWYMRP82k37PeHC82k3a\/V\/3vc\nrmt3zMym267THtf+PRGVViaH+XqmA6hag4RrUVJTM5vimwmpm6PGs17xJVie\/eTgTa7dxPGHTAGH\nS1efr+bsTV1+nRu6kZZsrZVSAAOuumXTXwI4Yjyr\/K6k+oZb5wX5fQm8SWmWq0FNoQWuUpJEVLVe\nz4bUUpAwMW3WzMyxM9i1ZE1mZ8JoMj5lcjafStaNfk1zYcoRP9Mp\/qDPN\/NQN3XEzdgYz5hza6ak\nAGqppaZhv4eZiJgSjOHgCT74cdh66Zx3U9rSMQDCjWyP3OD9gRnQH0BTgsTMrsVnmunXuACYot0J\nh8uQYLrg2V9DM4noM0h9uLH44VmU618mJ46stvpo82SxWOSUtdpmrDmnXMswFk3NtL57763tNrPv\nZrMwn8WTg9Xhat718WC1dpMeYQ+iBkDbZbaSHcxgCmtgf2lYbsyMKcS+RuJaH11\/KFre4w+\/30aT\n5quI6Yr1pPyepiau59EPUzQfSg0wVK2n5+dvvnl3u9\/269nB8mC73Tr26\/Xq3Qf3njx+Mu\/mT85P\nU65p3KX9mDTlfZ7PF9RazXmxXt65ffTZz770ydsnQQLJ9Shuvo2rr+xgDSRPXWhTtAIXP6B717nD\n9uFRtAENVFJ56itfzybc0ETv8Wt+JFJ87623v\/3md7939+0nT85qqeOYpItQdLP42Zc\/e\/7kdNwO\nIUR2ksf9sN3Vhgq0tmUKBD+WIY877+JssWKY1tZ17vlP3l4vFq9+9rO3bp2IEzWY3fSKbxDxZexB\njGvrRvLhodmzAD2bWSUr73NFrpX0D0B6ovqDbqip\/cGf\/\/EfffmP337ru\/thqNUYiMTOuUfbzTp2\nq+NjJiUnQpKGtB0GR\/7g6OjxsDlZLk9uH5d93m8vyHS\/25+fnTvnSq1xtbp9dLtK++Stk+dvPffq\n51797EufDo7NkHO+HGV7mmi2KVJ5v7\/CDiSwhqsMDyQ+9e8VoBtTsAFyE6zrC9cffpSI\/CkZxv34\np3\/2p\/\/hj778nW9\/a7Pd1VatFjNtxK226OLh0eG427SaG5r4oGqllKYK1Rii751mDd6F2MUQzHTY\nDaUUFxwRLw9OWOFnYdH1McTlavHCC8+9+uqrr7z8Oc\/eiwcaFDp5C9aatdqUW7ucKNep+g8cIHtY\nhRlAYAcD0MAE9TCQVXs60X48dG7AlFJ687tv\/qt\/\/a9e\/5vXt\/ucSrJmQG1NTeH4crEqSABn70NJ\nqZRiVwswIug9IMEzkVIGYCogEoFzBEwOXFzMO++X81UzFuaT48NXP\/fKaz\/\/2mde\/IzzDFwlheiG\nPrrWvJpLLkamUBgmwbsRjz07QW\/OOhfoqaP\/48YV17TZ7\/\/sz7\/yL3\/rtx6en6eUUGGWAHMMwbPr\nJYgBWB+utQy1ZAOi1IWnhcyIAEFKgFBWkBUBQSJ7D8IwjDYZNSL2PYXI0Xnxd+68+FM\/+4+WofzC\nL74Wwg0P8ebQrkfeAIaZQSegoNBcCxrQUplywwY1AJhyKQWIDmTNLoXzI4hVq+2NN9743X\/7b7\/6\n1a+lNOTcAIjUVR965o4QhRkw1UusGI4gLjjmECQ4EQ7iPAhWrFm7tCdy5dMIgURVU02ttVLhQqDQ\nzdZHru\/PtkMy\/9Lnf\/KlzzwP53
//{"message":null,"msgcontent":{"requestParam":{"actiontype":"activateTickets","userid":"57","ticketid":"360"},"responseInfo":[{"id":"350","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1103PF","ticket_code":"3MCCPC","ticket_amount":"11","purchased_on":"2016-06-14 15:01:32","proof_document":"","upload_photo":"","activated_on":"2016-06-14 15:02:46","activated_station_code":"0","valid_till":"2016-06-14 17:02:46","status_date":"2016-06-14 15:02:46","txnid":"2420fd6bd297f34471e1","validated_count":"0","activatedDate":"2016-06-14 15:02:46"},{"id":"351","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1104PF","ticket_code":"140GCC","ticket_amount":"11","purchased_on":"2016-06-14 15:31:32","proof_document":"","upload_photo":"","activated_on":"2016-06-14 15:33:38","activated_station_code":"0","valid_till":"2016-06-14 17:33:38","status_date":"2016-06-14 15:33:38","txnid":"34587c700ae9ab0431b4","validated_count":"0","activatedDate":"2016-06-14 15:33:38"},{"id":"352","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1105PF","ticket_code":"1UPFCC","ticket_amount":"11","purchased_on":"2016-06-14 15:40:43","proof_document":"pancard","upload_photo":"","activated_on":"2016-06-14 15:41:43","activated_station_code":"0","valid_till":"2016-06-14 17:41:43","status_date":"2016-06-14 15:41:43","txnid":"1c162732eda3d175b809","validated_count":"0","activatedDate":"2016-06-14 15:41:43"},{"id":"355","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1106PF","ticket_code":"GMMCF0","ticket_amount":"11","purchased_on":"2016-06-15 10:56:55","proof_document":"pancard","upload_photo":"","activated_on":"2016-06-15 12:02:13","activated_station_code":"0","valid_till":"2016-06-15 14:02:13","status_date":"2016-06-15 12:02:13","txnid":"4e1ff01801adac435c47","validated_count":"0","activatedDate":"2016-06-15 12:02:13"},{"id":"358","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1109PF","ticket_code":"G1MFCS","ticket_amount":"11","purchased_on":"2016-06-15 12:03:09","proof_document":"pancard","upload_photo":"","activated_on":"2016-06-15 12:26:14","activated_station_code":"0","valid_till":"2016-06-15 14:26:14","status_date":"2016-06-15 12:26:14","txnid":"8e46080c115d93181273","validated_count":"0","activatedDate":"2016-06-15 12:26:14"},{"id":"359","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1110PF","ticket_code":"S1C1M1","ticket_amount":"11","purchased_on":"2016-06-15 12:30:18","proof_document":"pancard","upload_photo":"","activated_on":"2016-06-15 12:32:00","activated_station_code":"0","valid_till":"2016-06-15 14:32:00","status_date":"2016-06-15 12:32:00","txnid":"634738b1311530ff12dc","validated_count":"0","activatedDate":"2016-06-15 12:32:00"},{"id":"360","from_station":"1","to_station":"1","ticket_type":"Platform","ticket_category":null,"ticket_period":null,"no_of_tickets":"1","ticket_number":"CSMUMCCG1111PF","ticket_code":"F1CUC1","ticket_amount":"11","purchased_on":"2016-06-15 12:38:16","proof_document":"pancard","upload_photo":"","activated_on":"2016-06-15 12:39:19","activated_station_code":"0","valid_till":"2016-06-15 14:39:19","status_date":"2016-06-15 12:39:19","txnid":"ba890848b80dd3f453f0","validated_count":"0","activatedDate":"2016-06-15 12:39:19"}]}}
