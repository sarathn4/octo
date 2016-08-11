package com.trail.octo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.beacon.BeaconFinderService;
import com.crashlytics.android.Crashlytics;
import com.data.tickets.VolleyRequestData;
import com.database.Mydatabase;
import com.database.RouteDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jaalee.sdk.BeaconManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//355819060361361

    public static final String TAG = "SendMail";
    public static final int REQUEST_IMEI_NUMBER = 1000;

    private static final int REQUEST_ENABLE_BT = 1234;
    SharedPreferences sharedPreferences;
    Mydatabase mydatabase;
    RouteDatabase routeDatabase;
    boolean login_status = false;
    private BeaconManager beaconManager;

    private static final int REQUEST_CHECK_SETTINGS = 10;
    GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        Log.e("Check", "onCreate");
        beaconManager = new BeaconManager(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mydatabase = new Mydatabase(this);
        routeDatabase = new RouteDatabase(this);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        login_status = sharedPreferences.getBoolean("login_status", false);
        String imei_number = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getIMEIPermission();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        Log.e("Permission Check", "No:" + permissionCheck);
        if (permissionCheck == 0) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    getSystemService(Context.TELEPHONY_SERVICE);
            imei_number = telephonyManager.getDeviceId();
            Log.e("IMEI", "" + imei_number);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            checkBluetooth();
        } else {
            call();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Check", "onStart");
//        checkBluetooth();
    }

    public void checkBluetooth() {
        Log.e("Check", "checkBTE");
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
            if (!login_status) {
                Log.e("Check", "" + login_status);
//                new DataLoader().execute();
                loadStations();
            } else {
                //connectToService();
                Log.e("Check", "" + login_status);
                Intent intent = new Intent(SplashScreen.this, Home.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
                if (!login_status) {
                    Log.e("Check", "" + login_status);
//                    new DataLoader().execute();
                    loadStations();
                } else {
                    //connectToService();
                    Log.e("Check", "" + login_status);
                    Intent intent = new Intent(SplashScreen.this, Home.class);
                    startActivity(intent);
                }
            } else {
                checkBluetooth();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            Log.e("Check", "Result Ok");
                            checkBluetooth();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            Toast.makeText(getApplicationContext(), "OCTO cannot function without" +
                                    " these must permissions", Toast.LENGTH_SHORT).show();
                            Log.e("Check", "Result Cancelled");
                            requestLocationAndBle();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        Log.e("Check", "Connect To Service");
        Intent intent = new Intent(this, BeaconFinderService.class);
        startService(intent);
    }

    public void loadStations() {
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
                    parseStationData(response);
                    Log.e("Check", message);
                    pDialog.hide();
//                    notifyUser(message+"");
                    loadRoutes();
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.hide();
                    notifyUser("Something went wrong.Trying again. Please wait");
                    loadStations();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                notifyUser("Something went wrong.Trying again. Please wait");
                loadStations();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getStations");
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

    public void parseStationData(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject msgContent = jsonObject.getJSONObject("msgcontent");
        JSONArray responseInfo = msgContent.getJSONArray("responseInfo");

        for (int i = 0; i < responseInfo.length(); i++) {
            JSONObject station_item = responseInfo.getJSONObject(i);
            JSONObject stations = station_item.getJSONObject("stations");
            String station_id = stations.getString("id");
            String station_name = stations.getString("station_name");
            String station_code = stations.getString("station_code");
            String division_id = stations.getString("division_id");
            if (division_id.equals("1")) {
                mydatabase.insertStation(station_id, station_name, station_code);
                Log.e("Station Name", station_name);
            }
        }
    }

    public void loadRoutes() {
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
                    parseRoutesData(response);
                    pDialog.hide();

                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.hide();
                    notifyUser("Something went wrong.Trying again. Please wait");
                    loadRoutes();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                notifyUser("Something went wrong.Trying again. Please wait");
                loadRoutes();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getRoutes");
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

    public void parseRoutesData(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject msgContent = jsonObject.getJSONObject("msgcontent");
        JSONArray responseInfo = msgContent.getJSONArray("responseInfo");

        for (int i = 0; i < responseInfo.length(); i++) {
            JSONObject routeObject = responseInfo.getJSONObject(i);
            JSONObject routes = routeObject.getJSONObject("routes");
            String id = routes.getString("id");
            String route_name = routes.getString("route_name");
            String from_station = routes.getString("station_start");
            String to_station = routes.getString("station_end");
            String inter_stations = routes.getString("inter_stations");
            routeDatabase.insertRoute(id, route_name, from_station, to_station, inter_stations);
            Log.e("id", id);
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    public void call() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e("Permission", "Already Granted");
            requestLocationAndBle();
        }
    }

    public void requestLocationAndBle() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());
        builder.setNeedBle(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Log.e("Check", "Status Success");
                        checkBluetooth();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            Log.e("Check", "Resolution Required");
                            status.startResolutionForResult(
                                    SplashScreen.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        Log.e("Check", "Status Change unavailable");
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Check", "Given");
//                    requestLocationAndBle();
                    checkBluetooth();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("Check", "Not Given");
                    requestLocationAndBle();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_IMEI_NUMBER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "Octo requires this Permission to function properly!", Toast.LENGTH_SHORT);
                    getIMEIPermission();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void getIMEIPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_IMEI_NUMBER);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}

//{"message":"Success",
// "msgcontent":{"requestParam":{"actiontype":"getRoutes"},
// "responseInfo":[
// {"routes":{"id":"14","route_name":"1-28","station_start":"1","station_end":"28","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23::24::25::26::27::28","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"15","route_name":"1-27","station_start":"1","station_end":"27","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23::24::25::26::27","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"16","route_name":"1-26","station_start":"1","station_end":"26","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23::24::25::26","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"17","route_name":"1-25","station_start":"1","station_end":"25","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23::24::25","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"18","route_name":"1-24","station_start":"1","station_end":"24","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23::24","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"19","route_name":"1-23","station_start":"1","station_end":"23","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22::23","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"20","route_name":"1-22","station_start":"1","station_end":"22","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21::22","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"21","route_name":"1-21","station_start":"1","station_end":"21","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20::21","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"22","route_name":"1-20","station_start":"1","station_end":"20","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19::20","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"23","route_name":"1-19","station_start":"1","station_end":"19","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18::19","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"24","route_name":"1-18","station_start":"1","station_end":"18","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17::18","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"25","route_name":"1-17","station_start":"1","station_end":"17","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16::17","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"26","route_name":"1-16","station_start":"1","station_end":"16","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15::16","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"27","route_name":"1-15","station_start":"1","station_end":"15","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14::15","status":"A","createdon":"2015-10-10 00:00:00"}},
// {"routes":{"id":"28","route_name":"1-14","station_start":"1","station_end":"14","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13::14","status":"A","createdon":"2015-10-10 00:00:00"}},{"routes":{"id":"29","route_name":"1-13","station_start":"1","station_end":"13","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12::13","status":"A","createdon":"2015-10-10 00:00:00"}},{"routes":{"id":"30","route_name":"1-12","station_start":"1","station_end":"12","inter_stations":"1::2::3::4::5::6::7::8::9::10::11::12","status":"A","createdon":"2015-10-10 00:00:00"}},{"routes":{"id":"31","route_name":"1-11","station_start":"1","station_end":"11","inter_stations":"1::2::3::4::5::6::7::8::9::10::11","status":"A","createdon":"2015-10-10 00:00:00"}},{"routes":{"id":"32","route_name":"1-10","station_start":"1","station_end":"10","i

//{"message":"Success",
// "msgcontent":{"requestParam":
// {"actiontype":"getStations"},"responseInfo":[
// {"stations":{"id":"38","station_name":"Addadar","station_code":"DDR","city_id":"1","division_id":"4","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"45","station_name":"Andhaaeri","station_code":"ADH","city_id":"1","division_id":"7","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"16","station_name":"Andheri","station_code":"ADH","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"41","station_name":"Bandddra","station_code":"BA","city_id":"1","division_id":"5","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"12","station_name":"Bandra","station_code":"BA","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"53","station_name":"Bhayaaaandar","station_code":"BYR","city_id":"1","division_id":"10","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"24","station_name":"Bhayandar","station_code":"BYR","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"50","station_name":"Borivaaali","station_code":"BVI","city_id":"1","division_id":"9","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"21","station_name":"Borivali","station_code":"BVI","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"3","station_name":"Charni Road","station_code":"CYR","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"32","station_name":"Charnrti Road","station_code":"CYR","city_id":"1","division_id":"2","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"1","station_name":"Churchgate","station_code":"CCG","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"30","station_name":"Churdfchgate","station_code":"CCG","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"9","station_name":"Dadar","station_code":"DDR","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"51","station_name":"Dahisaaar","station_code":"DIC","city_id":"1","division_id":"9","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"22","station_name":"Dahisar","station_code":"DIC","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"37","station_name":"Elphfvdinstone Road","station_code":"EPR","city_id":"1","division_id":"4","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"8","station_name":"Elphinstone Road","station_code":"EPR","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"47","station_name":"Goregaaaon","station_code":"GMN","city_id":"1","division_id":"8","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"18","station_name":"Goregaon","station_code":"GMN","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"4","station_name":"Grant Road","station_code":"GTR","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"33","station_name":"Granwet Road","station_code":"GTR","city_id":"1","division_id":"3","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"46","station_name":"Jogesaaahwari","station_code":"JOS ","city_id":"1","division_id":"8","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"17","station_name":"Jogeshwari","station_code":"JOS ","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"49","station_name":"Kandiaavali","station_code":"KILE","city_id":"1","division_id":"9","status":"A","createdon":"0000-00-00 00:00:00"}},{"stations":{"id":"20","station_name":"Kandivali","station_code":"KILE","city_id":"1","division_id":"1","status":"A","createdon":"0000-00-00 00: