package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.adapters.ComplaintListAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.Complaint;
import com.data.tickets.VolleyRequestData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfComplaints extends Home {

    public static final String TAG = "ListOfComplaints";
//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    SharedPreferences sharedPreferences;
    PopupWindow popupWindow;
    View view;

    ListView listView;
    ArrayList<Complaint> complaintArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_complaints);
        super.onCreateDrawer();
        complaintArrayList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);

        listView = (ListView) findViewById(R.id.listView_complaints);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            public void run() {
//                new GetComplaints().execute();
                getComplaints();
            }
        }, 500);
    }

//    class GetComplaints extends AsyncTask<Void,Void,Void> {
//
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
//                params.add(new BasicNameValuePair("actiontype", "getComplaints"));
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
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
//
//            if(message.equals("Success")){
//                try {
//                    JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                    JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//
//                    for(int i = 0;i<responseInfo.length();i++){
//                        Complaint complaint = new Complaint();
//                        JSONObject object = responseInfo.getJSONObject(i);
//                        JSONObject supports = object.getJSONObject("supports");
//
//                        String id = supports.getString("id");
//                        String customer_name = supports.getString("customer_name");
//                        String logged_date = supports.getString("logged_date");
//                        String replied_date = supports.getString("replied_date");
//                        String replied_by = supports.getString("replied_by");
//                        String query_text = supports.getString("query_text");
//                        String replied_text = supports.getString("replied_text");
//                        String status = supports.getString("status");
//
//                        complaint.setComplaint_id(id);
//                        complaint.setCustomer_name(customer_name);
//                        complaint.setLogged_date(logged_date);
//                        complaint.setReplied_date(replied_date);
//                        complaint.setReplied_by(replied_by);
//                        complaint.setQuery_text(query_text);
//                        complaint.setReplied_text(replied_text);
//                        complaint.setStatus(status);
//                        complaintArrayList.add(complaint);
//                    }
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            ComplaintListAdapter complaintListAdapter = new ComplaintListAdapter(getApplicationContext(),complaintArrayList);
//            listView.setAdapter(complaintListAdapter);
//            popupWindow.dismiss();
//        }
//    }

    public void getComplaints(){
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
                    if(message.equals("Success")){
                        parseData(""+response);
                    } else{
                        notifyUser(message+"");
                    }
                    ComplaintListAdapter complaintListAdapter = new ComplaintListAdapter(getApplicationContext(),complaintArrayList);
                    listView.setAdapter(complaintListAdapter);
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
                params.put("actiontype", "getComplaints");
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

    public void parseData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");

            for(int i = 0;i<responseInfo.length();i++){
                Complaint complaint = new Complaint();
                JSONObject object = responseInfo.getJSONObject(i);
                JSONObject supports = object.getJSONObject("supports");

                String id = supports.getString("id");
                String customer_name = supports.getString("customer_name");
                String logged_date = supports.getString("logged_date");
                String replied_date = supports.getString("replied_date");
                String replied_by = supports.getString("replied_by");
                String query_text = supports.getString("query_text");
                String replied_text = supports.getString("replied_text");
                String status = supports.getString("status");

                complaint.setComplaint_id(id);
                complaint.setCustomer_name(customer_name);
                complaint.setLogged_date(logged_date);
                complaint.setReplied_date(replied_date);
                complaint.setReplied_by(replied_by);
                complaint.setQuery_text(query_text);
                complaint.setReplied_text(replied_text);
                complaint.setStatus(status);
                complaintArrayList.add(complaint);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

}
//
//'userid' => '14',
//        'actiontype' => 'getComplaints',

// {"message":"Success","msgcontent":{"requestParam":{"actiontype":"getComplaints","userid":"57"},
// "responseInfo":[
// {"supports":{"id":"3","customer_id":"33","customer_name":"Sarath","support_type":"complaint","logged_date":"2015-09-14 12:35:58","replied_date":null,"replied_by":"0","query_text":"Some problem","replied_text":null,"status":"open"}}]}}
