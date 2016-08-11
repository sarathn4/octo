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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
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

public class Settings extends Home {

    public static final String TAG = "Settings";

    Spinner default_city, expired_tickets_validity;
    Button button_save;
    Switch switch_ticket_activation;
//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences_settings;
    SharedPreferences sharedPreferences_ticket_actiavtion;

    boolean activate;
    PopupWindow popupWindow;
    View view;

    String validity;
    String array_default_city[], array_expired_tickets_validity[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        sharedPreferences_settings = getSharedPreferences("settings", MODE_PRIVATE);
        sharedPreferences_ticket_actiavtion = getSharedPreferences("ticket_activation", MODE_PRIVATE);

        validity = sharedPreferences_settings.getString("validity", "1 month");
        activate = sharedPreferences_ticket_actiavtion.getBoolean("activate", false);

        Log.e("Validity", validity);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);

        array_default_city = getResources().getStringArray(R.array.default_city);
        array_expired_tickets_validity = getResources().getStringArray(R.array.expired_tickets_validity);

        default_city = (Spinner) findViewById(R.id.settings_spinner_city_name);
        expired_tickets_validity = (Spinner) findViewById(R.id.settings_spinner_expired_tickets_validity);
        switch_ticket_activation = (Switch) findViewById(R.id.switch_ticket_activation);

        button_save = (Button) findViewById(R.id.settings_button_save);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_title_item, array_default_city);
        cityAdapter.setDropDownViewResource(R.layout.simple_list_item);
        default_city.setAdapter(cityAdapter);

        ArrayAdapter<String> validityAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_title_item, array_expired_tickets_validity);
        validityAdapter.setDropDownViewResource(R.layout.simple_list_item);
        expired_tickets_validity.setAdapter(validityAdapter);
        expired_tickets_validity.setSelection(validityAdapter.getPosition(validity), true);
        Log.e("Validty Position", "" + validityAdapter.getPosition(validity));
        expired_tickets_validity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validity = array_expired_tickets_validity[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences_ticket_actiavtion.edit();
                editor.putBoolean("activate", switch_ticket_activation.isChecked());
                editor.commit();
//                new ChangeSettings().execute();
                changeSettings();
            }
        });
    }

//    class ChangeSettings extends AsyncTask<Void, Void, Void> {
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
//                params.add(new BasicNameValuePair("actiontype", "updatesettings"));
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//                params.add(new BasicNameValuePair("newcityid", "1"));
//                params.add(new BasicNameValuePair("newticketlimit", validity));
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
//            SharedPreferences.Editor editor = sharedPreferences_settings.edit();
//            editor.putString("validity", validity);
//            editor.commit();
//            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
//            popupWindow.dismiss();
//            Intent intent = new Intent(getApplicationContext(), Home.class);
//            startActivity(intent);
//        }
//    }

    public void changeSettings() {
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
                    SharedPreferences.Editor editor = sharedPreferences_settings.edit();
                    editor.putString("validity", validity);
                    editor.commit();
                    notifyUser("" + message);
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
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
                params.put("actiontype", "updatesettings");
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("newcityid", "1");
                params.put("newticketlimit", validity);

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

//'userid' = '10',
//        'newcityid' = '1',
//        'newticketlimit' = '1 month',
//        'actiontype' = 'updatesettings',