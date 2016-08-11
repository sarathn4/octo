package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VolleyRequestData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UpdateProfile extends Home {

    public static final String TAG = "UpdateProfile";

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    Button button;
    EditText password;
    EditText name, confirm_password, pincode, mobile_no;
    Spinner city_name;
    Pattern pattern_name;

    PopupWindow popupWindow;
    View view;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        super.onCreateDrawer();
        pattern_name = Pattern.compile("^[a-zA-Z]+$");

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        findElement();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equals("")) {
                    name.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                } else if (!(pattern_name.matcher(name.getText().toString().trim()).matches())) {
                    name.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid name. Please try again", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().trim().equals("")) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().length() != 10) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Mobile no should be 10 digits. Please try again", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.PHONE.matcher(mobile_no.getText().toString().trim()).matches())) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid Mobile number.Please try again", Toast.LENGTH_SHORT).show();
                } else if (pincode.getText().toString().trim().equals("")) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Pincode", Toast.LENGTH_SHORT).show();
                } else if (pincode.getText().toString().trim().length() != 6) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Pincode should be 6 digits. Please try again", Toast.LENGTH_SHORT).show();
                } else if (!(pincode.getText().toString().trim().matches("^[0-9]+$"))) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid Pincode.Please try again", Toast.LENGTH_SHORT).show();
                } else if (!(password.getText().toString().equals(confirm_password.getText().toString()))) {
                    password.requestFocus();
                    Toast.makeText(getApplicationContext(), "Passwords do not match. Please try again", Toast.LENGTH_SHORT).show();
                } else
                    updateProfile();
//                    new UpdateData().execute();
//                if((password.getText().toString().equals(confirm_password.getText().toString())))
//                    new UpdateData().execute();
//                else
//                    Toast.makeText(getApplicationContext(),"Passwords do not match. Please try again.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void findElement() {
        name = (EditText) findViewById(R.id.updateprofile_editText_name);
        password = (EditText) findViewById(R.id.updateprofile_editText_password);
        confirm_password = (EditText) findViewById(R.id.updateprofile_editText_confirm_password);
        pincode = (EditText) findViewById(R.id.updateprofile_editText_pincode);
        mobile_no = (EditText) findViewById(R.id.updateprofile_editText_mobile_number);
        city_name = (Spinner) findViewById(R.id.updateprofile_spinner_city_name);
        button = (Button) findViewById(R.id.updateprofile_button);

        ArrayList<String> city_list = new ArrayList<String>();
        city_list.add("Mumbai");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, city_list);
        city_name.setAdapter(arrayAdapter);
        name.setText(sharedPreferences.getString("user_name", "User"));
        //Log.e("Check", user.getUser_name());
        //Log.e("Check", user.getAddress());
        pincode.setText(sharedPreferences.getString("pincode", "000000"));
        //Log.e("Check", user.getPincode());
        mobile_no.setText(sharedPreferences.getString("mobile_no", "0000000000"));
        //Log.e("Check", user.getMobile_no());

        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);
    }

//    class UpdateData extends AsyncTask<Void, Void, Void> {
//
//        String result = "";
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
//                params.add(new BasicNameValuePair("actiontype", "updatepassenger"));
//                if ((password.getText().toString().equals(confirm_password.getText().toString()))) {
//                    if (!(password.getText().toString().equals("")))
//                        params.add(new BasicNameValuePair("password", password.getText().toString()));
//                }
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//
//                if (!(mobile_no.getText().toString().equals("")))
//                    params.add(new BasicNameValuePair("mobile", mobile_no.getText().toString()));
//                else
//                    params.add(new BasicNameValuePair("mobile", sharedPreferences.getString("mobile_no", "0000000000")));
//
//                if (!(name.getText().toString().equals("")))
//                    params.add(new BasicNameValuePair("fullname", name.getText().toString()));
//                else
//                    params.add(new BasicNameValuePair("fullname", sharedPreferences.getString("user_name", "User")));
//
//                params.add(new BasicNameValuePair("city_name", "Mumbai"));
//
//                if (!(pincode.getText().toString().equals("")))
//                    params.add(new BasicNameValuePair("pincode", pincode.getText().toString()));
//                else
//                    params.add(new BasicNameValuePair("pincode", sharedPreferences.getString("pincode", "000000")));
//
//                Log.e("Check", "Params built");
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                Log.e("Check", "Staring execution");
//
//                httpResponse = httpClient.execute(httpPost);
//                Log.e("Check", "Executed");
//                result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//                Log.e("Response", result);
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
//                JSONObject jsonObject = new JSONObject(result);
//                String message = jsonObject.getString("message");
//                popupWindow.dismiss();
//                if (message.equals("Updated Profile")) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_APPEND);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("user_name", name.getText().toString());
//                    editor.putString("default_city", "Mumbai");
//                    editor.putString("default_city_id", "1");
//                    editor.putString("mobile_no", mobile_no.getText().toString());
//                    editor.putString("pincode", pincode.getText().toString());
//                    editor.commit();
//
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(UpdateProfile.this, Home.class);
//                    startActivity(intent);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void updateProfile() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                parseProfileData(response);
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
                params.put("actiontype", "updatepassenger");
                if ((password.getText().toString().equals(confirm_password.getText().toString()))) {
                    if (!(password.getText().toString().equals("")))
                        params.put("password", password.getText().toString());
                }
                params.put("userid", sharedPreferences.getString("user_id", "0"));

                if (!(mobile_no.getText().toString().equals("")))
                    params.put("mobile", mobile_no.getText().toString());
                else
                    params.put("mobile", sharedPreferences.getString("mobile_no", "0000000000"));

                if (!(name.getText().toString().equals("")))
                    params.put("fullname", name.getText().toString());
                else
                    params.put("fullname", sharedPreferences.getString("user_name", "User"));

                params.put("city_name", "Mumbai");

                if (!(pincode.getText().toString().equals("")))
                    params.put("pincode", pincode.getText().toString());
                else
                    params.put("pincode", sharedPreferences.getString("pincode", "000000"));

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

    public void parseProfileData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            popupWindow.dismiss();
            if (message.equals("Updated Profile")) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_APPEND);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", name.getText().toString());
                editor.putString("default_city", "Mumbai");
                editor.putString("default_city_id", "1");
                editor.putString("mobile_no", mobile_no.getText().toString());
                editor.putString("pincode", pincode.getText().toString());
                editor.commit();
                notifyUser("" + message);
                Intent intent = new Intent(UpdateProfile.this, Home.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}