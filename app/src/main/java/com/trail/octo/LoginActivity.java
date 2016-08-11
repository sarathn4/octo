package com.trail.octo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String volleyTag = "activateCustomer";

    EditText username, password;
    Button login, signup;

    TextView textView_forgot_password;
    PopupWindow popupWindow, popupWindow_fp;
    View view, view_fp;
    SharedPreferences sharedPreferences;
    SharedPreferences docs_sharedPreferences;
    EditText editText_popup;
    Button button_popup_submit, button_popup_cancel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        docs_sharedPreferences = getSharedPreferences("docs_data", MODE_PRIVATE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, true);

        view_fp = inflater.inflate(R.layout.pop_up_forgot_password, null);
        popupWindow_fp = new PopupWindow(view_fp, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, true);


        username = (EditText) findViewById(R.id.login_editText_email);
        password = (EditText) findViewById(R.id.login_editText_password);
        textView_forgot_password = (TextView) findViewById(R.id.login_textView_forgot_password);
        editText_popup = (EditText) view_fp.findViewById(R.id.pop_up_fp_editText_email);
        button_popup_submit = (Button) view_fp.findViewById(R.id.pop_up_fp_button_submit);
        button_popup_cancel = (Button) view_fp.findViewById(R.id.pop_up_fp_button_cancel);

        login = (Button) findViewById(R.id.login_button_login);
        signup = (Button) findViewById(R.id.login_button_signup);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return login.performClick();
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(username.getText().toString().trim().equals("")) && !(password.getText().toString().trim().equals(""))) {
                    if (Patterns.EMAIL_ADDRESS.matcher(username.getText().toString().trim()).matches())
                        loginCheck();
                    else
                        Toast.makeText(getApplicationContext(), "Invalid email. Please try again", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Enter email id and password", Toast.LENGTH_SHORT).show();
            }
        });

        textView_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_fp.showAtLocation(view, Gravity.CENTER, 0, 40);
            }
        });

        button_popup_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_popup.getText().toString().trim().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter Email id", Toast.LENGTH_SHORT).show();
                else if (!((Patterns.EMAIL_ADDRESS.matcher(editText_popup.getText().toString().trim()).matches()))) {
                    Toast.makeText(getApplicationContext(), "Invalid Email. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    forgotPassword();
                }
            }
        });
        button_popup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_fp.dismiss();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        showExitAlert();
    }

    public void showExitAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Are you sure to exit?");

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void loginCheck() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);

                try {
                    parseLoginResponse(response);
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
                params.put("actiontype", "authpassenger");
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
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

    public void forgotPassword() {
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
                        Toast.makeText(getApplicationContext(), "Password is sent to your mail. Please use it to login", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
                        String responseInfo = msgcontent.getString("responseInfo");
                        Toast.makeText(getApplicationContext(), "" + responseInfo, Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "forgotpassword");
                params.put("username", editText_popup.getText().toString());
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

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public void parseLoginResponse(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);
        String message = jsonResponse.getString("message");
        Log.e("Check", "postEx Started");
        if (message.equals("Error")) {
            popupWindow.dismiss();
            Toast.makeText(getApplicationContext(), "Invalid Credentials.Please try again.", Toast.LENGTH_SHORT).show();
        } else if (message.equals("Passenger Logged in")) {
            try {
                Log.e("Check", "passenger details extraction");
                JSONObject msgcontent = jsonResponse.getJSONObject("msgcontent");
                JSONObject responseInfo = msgcontent.getJSONObject("responseInfo");
                JSONObject userdata = null;
                if (responseInfo.has("0"))
                    userdata = responseInfo.getJSONObject("0");
                else
                    userdata = responseInfo.getJSONObject("1");

                JSONObject users = userdata.getJSONObject("Passenger");
                String user_name = users.getString("fullname");
                String id = users.getString("userid");
                String email_id = users.getString("email_id");
                String mobile = users.getString("mobile");
                String address = users.getString("address");
                String city_id = users.getString("city_id");
                String city_name = users.getString("city_name");
                String pincode = users.getString("pincode");
                String passenger_id = users.getString("id");
                String dob = "";
                if (users.has("dob")) {
                    dob = users.getString("dob");
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("passenger_id", passenger_id);
                editor.putString("user_name", user_name);
                editor.putString("email_id", email_id);
                editor.putString("default_city", "Mumbai");
                editor.putString("default_city_id", "1");
                Log.e("CheckID", id);
                editor.putString("user_id", id);
                editor.putString("mobile_no", mobile);
                editor.putString("address", address);
                editor.putString("pincode", pincode);
                editor.putString("dob", dob);
                editor.putBoolean("login_status", true);

                editor.commit();
                if (!userdata.isNull("PassengerDocuments")) {
                    JSONObject passengerdocs = userdata.getJSONObject("PassengerDocuments");

                    String pancard = "";
                    String drivinglicense = "";
                    String voterid = "";
                    String passport = "";
                    String aadharcard = "";
                    String profilepic = "";
                    if (passengerdocs.has("pancard")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("pancard");
                        pancard = jsonObject.getString("document_file");
                    }
                    if (passengerdocs.has("drivinglicense")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("drivinglicense");
                        drivinglicense = jsonObject.getString("document_file");
                    }

                    if (passengerdocs.has("voterid")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("voterid");
                        voterid = jsonObject.getString("document_file");
                    }

                    if (passengerdocs.has("aadharcard")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("aadharcard");
                        aadharcard = jsonObject.getString("document_file");
                    }
                    if (passengerdocs.has("passport")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("passport");
                        passport = jsonObject.getString("document_file");
                    }
                    if (passengerdocs.has("profilepic")) {
                        JSONObject jsonObject = passengerdocs.getJSONObject("profilepic");
                        profilepic = jsonObject.getString("document_file");
                    }
                    SharedPreferences.Editor docs_editor = docs_sharedPreferences.edit();

                    docs_editor.putString("pancard", pancard);
                    docs_editor.putString("drivinglicense", drivinglicense);
                    docs_editor.putString("voterid", voterid);
                    docs_editor.putString("passport", passport);
                    docs_editor.putString("aadharcard", aadharcard);
                    docs_editor.putString("profilepic", profilepic);
                    docs_editor.commit();
                }

                Toast.makeText(getApplicationContext(), "Hi " + user_name + "! Welcome back!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Home.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), message + ".Pleasse try again later.", Toast.LENGTH_SHORT).show();
        }
    }

}

//{"message":"Passenger Logged in","msgcontent":{"requestParam":{"actiontype":"authpassenger","username":"srthnatesan@gmail.com","password":"df94ffd25eab310eb58b50c4d75b6166539457d6","passengerid":"9"},"responseInfo":{"users":{"username":"srthnatesan@gmail.com","id":"10"},"0":{"passengers":{"id":"9","userid":"10","fullname":"Sarath Natesan","email_id":"srthnatesan@gmail.com","mobile":"9035046301","address":"Salem","city_id":"0","city_name":"Mumbai","pincode":"636202","emailcode":"etm00r@niacl5g.san36a103oh4st9_1","verified":"1"}}}}}
//{"message":"Passenger Logged in","msgcontent":{"requestParam":{"actiontype":"authpassenger","username":"srthnatesan@gmail.com","password":"df94ffd25eab310eb58b50c4d75b6166539457d6","userid":"10","passengerid":null},"responseInfo":{"users":{"username":"srthnatesan@gmail.com","id":"10"},"0":{"Passenger":{"id":"9","userid":"10","fullname":"Sarath Natesan","email_id":"srthnatesan@gmail.com","mobile":"9035046301","address":"Salem","city_id":"0","city_name":"Mumbai","pincode":"636202","emailcode":"etm00r@niacl5g.san36a103oh4st9_1","verified":"1"},"PassengerDocument":[]}}}}