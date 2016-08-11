package com.trail.octo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.VolleyRequestData;

import java.util.HashMap;
import java.util.Map;


public class DocumentUploadPreview extends Activity {
    //    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;
    String file_name = "doc_file.png";
    SharedPreferences sharedPreferences, sharedPreferences_doc;
    String file_type = "ProfilePhoto";
    String encodedmessage = "";

    public static final String TAG = "DocumentUpload";
    ImageView imageView_document, imageView_select, imageView_cancel;
    PopupWindow popupWindow;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload_preview);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        sharedPreferences_doc = getSharedPreferences("docs_data", MODE_APPEND);

        file_name = getIntent().getStringExtra("file_name");
        file_type = getIntent().getStringExtra("file_type");
        //encodedmessage = getIntent().getStringExtra("encodedmessage");
        encodedmessage = sharedPreferences_doc.getString(file_type, "");
        byte[] decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        imageView_document = (ImageView) findViewById(R.id.imageView_doc_preview);
        imageView_cancel = (ImageView) findViewById(R.id.imageView_cancel);
        imageView_select = (ImageView) findViewById(R.id.imageView_select);
        imageView_document.setImageBitmap(image);

        imageView_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ImageUploader().execute();
                uploadImage();
            }
        });

        imageView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadDocs.class);
                startActivity(intent);
            }
        });
    }

//    class ImageUploader extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.e("Check", "Uploading");
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 40);
//        }
//
//        @Override
//        protected Void doInBackground(Void... param) {
//            try {
//                Log.e("Check", file_name + "-----" + file_type);
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                if (file_type.equals("ProfilePhoto"))
//                    params.add(new BasicNameValuePair("actiontype", "uploadProfilePhoto"));
//                else {
//                    params.add(new BasicNameValuePair("actiontype", "uploaddocuments"));
//                    params.add(new BasicNameValuePair("is_default", "0"));
//                    params.add(new BasicNameValuePair("document_name", file_type));
//                }
//                params.add(new BasicNameValuePair("userid", sharedPreferences.getString("user_id", "0")));
//
//                params.add(new BasicNameValuePair("document_file_name", file_name));
//                params.add(new BasicNameValuePair("document_file_content", encodedmessage));
//
//                Log.e("Check", "Params built");
//                httpClient = new DefaultHttpClient();
//                httpPost = new HttpPost("http://goeticket.com/ticketingsystem/passengers.json");
//
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//                Log.e("Check", "Staring execution");
//                httpResponse = httpClient.execute(httpPost);
//                Log.e("Check", "Executed");
//                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
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
//            popupWindow.dismiss();
//            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), UploadDocs.class);
//            startActivity(intent);
//        }
//    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void uploadImage() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    Log.e(TAG, "got");
                    pDialog.hide();
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), UploadDocs.class);
                    startActivity(intent);
                } catch (Exception e) {
                    SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                    editor.putString("" + file_type, "");
                    editor.commit();
                    e.printStackTrace();
                    pDialog.hide();
                    notifyUser("Something went wrong. Please try again.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                editor.putString("" + file_type, "");
                editor.commit();
                Log.e(TAG, "Error:" + error.toString());
                pDialog.hide();
                notifyUser("Something went wrong. Please try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("Check", file_name + "-----" + file_type);

                Map<String, String> params = new HashMap<String, String>();
                if (file_type.equals("ProfilePhoto"))
                    params.put("actiontype", "uploadProfilePhoto");
                else {
                    params.put("actiontype", "uploaddocuments");
                    params.put("is_default", "0");
                    params.put("document_name", "file_type");
                }
                params.put("userid", sharedPreferences.getString("user_id", "0"));
                params.put("document_file_name", file_name);
                params.put("document_file_content", "" + encodedmessage);
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
