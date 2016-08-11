package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VolleyRequestData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NewComplaint extends Home {
    public static final String TAG = "SendFeedback";

    EditText editText;
    Button button;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    SharedPreferences sharedPreferences;
    PopupWindow popupWindow;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        super.onCreateDrawer();

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);

        editText = (EditText) findViewById(R.id.editText_feedback);
        button = (Button) findViewById(R.id.button_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter your complaint!", Toast.LENGTH_SHORT).show();
                else
//                    new SendFeedback().execute();
                    sendFeedback();
            }
        });
    }

//    class SendFeedback extends AsyncTask<Void, Void, Void> {
//        String result = "";
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
//                Log.e("Check", "Staring register");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "setSupport"));
//                params.add(new BasicNameValuePair("passenger_id", sharedPreferences.getString("passenger_id", "0")));
//                params.add(new BasicNameValuePair("query_text", editText.getText().toString()));
//                params.add(new BasicNameValuePair("support_type", "complaint"));
//
//                Log.e("Check", "Params built");
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//                Log.e("Check", "Staring execution");
//                httpResponse = httpClient.execute(httpPost);
//                Log.e("Check", "Executed");
//                result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Response", result);
//                jsonObject = new JSONObject(result);
//                message = jsonObject.getString("message");
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
//            popupWindow.dismiss();
//            if (message.equals("Support Created")) {
//                Toast.makeText(getApplicationContext(), "Your complaint has been received! Sorry for the inconvenience. We will do our best!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(),Home.class);
//                startActivity(intent);
//            }
//            else
//                Toast.makeText(getApplicationContext(), "Error! Sorry for the inconvenience! Please try again!", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void sendFeedback() {
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
                    if (message.equals("Support Created")) {
                        notifyUser("Your complaint has been received! Sorry for the inconvenience. We will do our best!");
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    } else
                        notifyUser("Error! Sorry for the inconvenience! Please try again!");

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
                params.put("actiontype", "setSupport");
                params.put("passenger_id", sharedPreferences.getString("user_id", "0"));
                params.put("query_text", editText.getText().toString().trim());
                params.put("support_type", "complaint");
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