package com.trail.octo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//import org.apache.commons.codec.binary.Base64;

public class SignUp extends Activity {

    public static final String TAG = "SignUp";

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    EditText name, email, password, confirm_password, pincode, mobile_no, dob;
    TextView textView_login;
    Spinner city_name;
    List<String> city_list;
    Button sign_up;
    PopupWindow popupWindow;
    View view;
    Pattern pattern_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        pattern_name = Pattern.compile("^[a-zA-Z]+$");
        findElement();

        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, LoginActivity.class));
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!(name.getText().toString().trim().equals("")))
                        Toast.makeText(getApplicationContext(), "Wow! Thats a good name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(dob);
            }
        });
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDate(dob);
                }
            }
        });
        city_list = new ArrayList<String>();
        city_list.add("Select city");
        city_list.add("Mumbai");
        city_list.add("Bangalore");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_title_item, city_list);
        arrayAdapter.setDropDownViewResource(R.layout.simple_list_item);
        city_name.setAdapter(arrayAdapter);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equals("")) {
                    name.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                }
//                else if (!(pattern_name.matcher(name.getText().toString().trim()).matches())) {
//                    name.requestFocus();
//                    Toast.makeText(getApplicationContext(), "Invalid name. Please try again", Toast.LENGTH_SHORT).show();
//                }

                else if (mobile_no.getText().toString().trim().equals("")) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().length() != 10) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Mobile no should be 10 digits. Please try again", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.PHONE.matcher(mobile_no.getText().toString().trim()).matches())) {
                    mobile_no.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid Mobile number.Please try again", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().equals("")) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())) {
                    email.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid Email. Please try again.", Toast.LENGTH_SHORT).show();
                } else if (dob.getText().toString().trim().equals("")) {
                    dob.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please select your DOB", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().trim().equals("")) {
                    password.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_SHORT).show();
                } else if (confirm_password.getText().toString().trim().equals("")) {
                    confirm_password.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!(confirm_password.getText().toString().trim().equals(password.getText().toString().trim()))) {
                    confirm_password.requestFocus();
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (city_name.getSelectedItemPosition() == 0)
                    Toast.makeText(getApplicationContext(), "Please select city", Toast.LENGTH_SHORT).show();
                else if (pincode.getText().toString().trim().equals("")) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please enter Pincode", Toast.LENGTH_SHORT).show();
                } else if (pincode.getText().toString().trim().length() != 6) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Pincode should be 6 digits. Please try again", Toast.LENGTH_SHORT).show();
                } else if (!(pincode.getText().toString().trim().matches("^[0-9]+$"))) {
                    pincode.requestFocus();
                    Toast.makeText(getApplicationContext(), "Invalid Pincode.Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Check", "Clicked register");
//                    new RegisterUser().execute();
                    signUp();
                }
            }
        });
    }

    public void findElement() {
        name = (EditText) findViewById(R.id.sign_up_editText_name);
        email = (EditText) findViewById(R.id.sign_up_editText_email);
        password = (EditText) findViewById(R.id.sign_up_editText_password);
        confirm_password = (EditText) findViewById(R.id.sign_up_editText_confirm_password);
        pincode = (EditText) findViewById(R.id.sign_up_editText_pincode);
        mobile_no = (EditText) findViewById(R.id.sign_up_editText_mobile_number);
        dob = (EditText) findViewById(R.id.sign_up_editText_dob);

        city_name = (Spinner) findViewById(R.id.sign_up_spinner_city_name);

        sign_up = (Button) findViewById(R.id.sign_up_button_sign_up);
        textView_login = (TextView) findViewById(R.id.sign_up_textView_login);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);
    }

    public void setDate(final EditText editText) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        final int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ++monthOfYear;
                editText.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

//    class RegisterUser extends AsyncTask<Void, Void, Void> {
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
//                Log.e("Check", "Staring register");
//                Calendar calendar = Calendar.getInstance();
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "addpassenger"));
//                params.add(new BasicNameValuePair("fullname", name.getText().toString()));
//                params.add(new BasicNameValuePair("email_id", email.getText().toString()));
//                params.add(new BasicNameValuePair("mobile", mobile_no.getText().toString()));
//                params.add(new BasicNameValuePair("password", password.getText().toString()));
//                params.add(new BasicNameValuePair("address", "Mumbai"));
//                params.add(new BasicNameValuePair("city_name", "Mumbai"));
//                params.add(new BasicNameValuePair("pincode", pincode.getText().toString()));
//                params.add(new BasicNameValuePair("created_datetime", calendar.getTime() + ""));
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
//
//                if (message.equals("Created")) {
//                    Toast.makeText(getApplicationContext(), "Account created successfully.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(SignUp.this, UploadDocs.class);
//                    startActivity(intent);
//                } else
//                    Toast.makeText(getApplicationContext(), message + ".Please try again.", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void signUp() {
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
                        notifyUser("Account created successfully.");
                        Intent intent = new Intent(SignUp.this, UploadDocs.class);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), message + ".Please try again.", Toast.LENGTH_SHORT).show();

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
                Calendar calendar = Calendar.getInstance();
                params.put("actiontype", "addpassenger");
                params.put("fullname", name.getText().toString());
                params.put("email_id", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("password", password.getText().toString());
                params.put("address", "Mumbai");
                params.put("city_name", "Mumbai");
                params.put("pincode", pincode.getText().toString());
                params.put("created_datetime", calendar.getTime() + "");
                params.put("dob", dob.getText().toString().trim() + "");
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