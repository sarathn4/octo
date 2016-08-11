package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VolleyRequestData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetDevice extends Activity {

    public static final String TAG = "ResetDevice";
    TextView textView_generate;
    EditText editText_otp;
    Button button_reset;

    SharedPreferences sharedPreferences;

    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_device);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        textView_generate = (TextView) findViewById(R.id.reset_textView_generate);
        editText_otp = (EditText) findViewById(R.id.reset_editText_otp);
        button_reset = (Button) findViewById(R.id.reset_button_reset);

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_otp.getText().toString().trim().equals("")) {
                    notifyUser("Please enter valid OTP");
                } else {
                    resetDevice();
                }
            }
        });

        textView_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTP();
            }
        });
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public void resetDevice() {
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
                    if (message.equals("Success")) {
                        pDialog.hide();
                        notifyUser("" + message);
                        startActivity(new Intent(ResetDevice.this, Home.class));
                    } else if (message.equals("Error")) {
                        JSONObject msgContent = jsonObject.getJSONObject("msgcontent");
                        String responseInfo = msgContent.getString("responseInfo");
                        pDialog.hide();
                        notifyUser("" + responseInfo + ".Please try again");
                    } else {
                        pDialog.hide();
                    }
                } catch (JSONException e) {
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
                params.put("actiontype", "confirmresetIEMI");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("imei_device", "" + telephonyManager.getDeviceId());
                params.put("otp", editText_otp.getText().toString().trim());
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

    public void generateOTP() {
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
                    if (message.equals("Success")) {
                        notifyUser("Request is forwarded to Admin! You will receive OTP within 2 Hours.");
                    } else {
                        notifyUser("" + message);
                    }
                    Log.e(TAG, "got");
                    pDialog.hide();
                } catch (JSONException e) {
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
                params.put("actiontype", "resetIEMI");
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
}


//{"message":"Success","msgcontent":{"requestParam":{"actiontype":"resetIEMI","userid":"57"},"responseInfo":"Reset IEMI Request sent to Admin"}}
//{"message":"Error","msgcontent":{"requestParam":{"otp":"123456","actiontype":"confirmresetIEMI","imei_device":"355819060361361","userid":"57"},"responseInfo":"Invalid OTP"}}
