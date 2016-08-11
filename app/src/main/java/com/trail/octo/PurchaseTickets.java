package com.trail.octo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.database.Mydatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseTickets extends Home {

    public static final String TAG = "GetRates";

    Spinner spinner_ticket_type, spinner_source, spinner_desination, spinner_category;
    RadioGroup radioGroup_period;
    RadioButton radioButton_monthly, radioButton_quarterly;
    TextView textView_price_per_ticket, textView_total_price;
    LinearLayout button_plus, button_minus, button_no_of_tickets;
    TextView textView_no_of_tickets;
    Button button_buy;

//    HttpClient httpClient;
//    HttpPost httpPost;
//    HttpResponse httpResponse;

    String ticket_type = "Platform";
    String source = "";
    String destination = "Churchgate";
    String ticket_category = "I Class";
    String period = "Monthly";

    int no_of_tickets = 1;
    int price_per_ticket = 10;
    int total_price = 10;

    String ticket_types[];
    String ticket_categories[];
    ArrayList<String> stations;
    String[] destination_stations;
    Mydatabase mydatabase;

    String selected_source = "";
    String first_monthly = "0";
    String second_monthly = "0";
    String first_quarterly = "0";
    String second_quarterly = "0";

    String first_single = "0";
    String second_single = "0";

    PopupWindow popupWindow;
    View view;

    String document = "";
    String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Octo", "1");
        setContentView(R.layout.activity_purchase_tickets);
        Log.e("Octo", "2");
        super.onCreateDrawer();
        Log.e("Octo", "3");

        document = getIntent().getStringExtra("document");
        Log.e("document", document + "");

        image = getIntent().getStringExtra("image");
        Log.e("image", "" + image);

        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT, true);
        mydatabase = new Mydatabase(this);
        stations = mydatabase.getStationsList();

        destination_stations = getResources().getStringArray(R.array.destination_stations);
        ticket_types = getResources().getStringArray(R.array.ticket_type);
        ticket_categories = getResources().getStringArray(R.array.ticket_category);

        spinner_ticket_type = (Spinner) findViewById(R.id.spinner_ticket_type);
        spinner_source = (Spinner) findViewById(R.id.spinner_source_station_list);
        spinner_desination = (Spinner) findViewById(R.id.spinner_destination_station_list);
        spinner_category = (Spinner) findViewById(R.id.spinner_category);

        radioGroup_period = (RadioGroup) findViewById(R.id.radiogroup_period);
        radioButton_monthly = (RadioButton) findViewById(R.id.radioButton_monthly);
        radioButton_quarterly = (RadioButton) findViewById(R.id.radioButton_quarterly);

        button_plus = (LinearLayout) findViewById(R.id.button_plus);
        button_minus = (LinearLayout) findViewById(R.id.button_minus);
        button_no_of_tickets = (LinearLayout) findViewById(R.id.button_count);
        button_buy = (Button) findViewById(R.id.button_buyticket);

        textView_no_of_tickets = (TextView) findViewById(R.id.textView_no_of_tickets);
        textView_price_per_ticket = (TextView) findViewById(R.id.textView_price_per_ticket);
        textView_total_price = (TextView) findViewById(R.id.textView_total_price);

        ArrayAdapter<String> arrayAdapter_ticket_type = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.simple_spinner_title_item, ticket_types);
        arrayAdapter_ticket_type.setDropDownViewResource(R.layout.simple_list_item);
        spinner_ticket_type.setAdapter(arrayAdapter_ticket_type);

        ArrayAdapter<String> arrayAdapter_stations_list = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.simple_spinner_title_item, stations);
        arrayAdapter_stations_list.setDropDownViewResource(R.layout.simple_list_item);
        spinner_source.setAdapter(arrayAdapter_stations_list);

        ArrayAdapter<String> arrayAdapter_destination_list = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.simple_spinner_title_item, destination_stations);
        arrayAdapter_destination_list.setDropDownViewResource(R.layout.simple_list_item);
        spinner_desination.setAdapter(arrayAdapter_destination_list);

        ArrayAdapter<String> arrayAdapter_ticket_category = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.simple_spinner_title_item, ticket_categories);
        arrayAdapter_ticket_category.setDropDownViewResource(R.layout.simple_list_item);
        spinner_category.setAdapter(arrayAdapter_ticket_category);

        spinner_ticket_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    radioButton_monthly.setSelected(false);
                    radioButton_quarterly.setSelected(false);
                    ticket_type = "Platform";
                    Log.e("PosCheck", position + "");
                    radioButton_quarterly.setEnabled(false);
                    radioButton_monthly.setEnabled(false);
                    spinner_desination.setEnabled(false);
                    spinner_category.setEnabled(false);
                    radioGroup_period.setEnabled(false);
                    price_per_ticket = 10;
                    setRates(price_per_ticket);
                    spinner_desination.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    spinner_category.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                } else if (position == 1) {
                    ticket_type = "Unreserved Ticket";
                    radioButton_monthly.setSelected(false);
                    radioButton_quarterly.setSelected(false);
                    if (source.equals(destination))
                        Toast.makeText(getApplicationContext(), "Please select different source & " +
                                "destination", Toast.LENGTH_SHORT).show();
                    else {
                        Log.e("PosCheck", position + "");
                        radioButton_quarterly.setEnabled(false);
                        radioButton_monthly.setEnabled(false);
                        spinner_desination.setEnabled(true);
                        spinner_category.setEnabled(true);
                        radioGroup_period.setEnabled(false);
                        spinner_desination.setBackgroundColor(getResources().getColor(android.R.color.white));
                        spinner_category.setBackgroundColor(getResources().getColor(android.R.color.white));
                        if (selected_source.equals(stations.get(spinner_source.getSelectedItemPosition()))) {
                            if (ticket_type.equals("MonthlyPass")) {
                                switch (ticket_category) {
                                    case "I Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(first_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(first_quarterly);
                                        }
                                        break;
                                    case "II Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(second_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(second_quarterly);
                                        }
                                        break;
                                }
                            } else if (ticket_type.equals("Unreserved Ticket")) {
                                if (ticket_category.equals("I Class"))
                                    price_per_ticket = Integer.parseInt(first_single);
                                else if (ticket_category.equals("II Class"))
                                    price_per_ticket = Integer.parseInt(second_single);
                            }
                            setRates(price_per_ticket);
                        } else
//                            new GetRates().execute();
                            getRates();
                    }
                } else if (position == 2) {
                    no_of_tickets = 1;
                    radioButton_monthly.setSelected(true);
                    ticket_type = "MonthlyPass";
                    textView_no_of_tickets.setText(no_of_tickets + "");
                    if (source.equals(destination))
                        Toast.makeText(getApplicationContext(), "Please select different source & " +
                                "destination", Toast.LENGTH_SHORT).show();
                    else {
                        Log.e("PosCheck", position + "");
                        radioButton_quarterly.setEnabled(true);
                        radioButton_monthly.setEnabled(true);
                        spinner_desination.setEnabled(true);
                        spinner_category.setEnabled(true);
                        radioGroup_period.setEnabled(true);
                        no_of_tickets = 1;
                        spinner_desination.setBackgroundColor(getResources().getColor(android.R.color.white));
                        spinner_category.setBackgroundColor(getResources().getColor(android.R.color.white));
                        if (selected_source.equals(stations.get(spinner_source.getSelectedItemPosition()))) {
                            if (ticket_type.equals("MonthlyPass")) {
                                switch (ticket_category) {
                                    case "I Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(first_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(first_quarterly);
                                        }
                                        break;
                                    case "II Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(second_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(second_quarterly);
                                        }
                                        break;
                                }
                            } else if (ticket_type.equals("Unreserved Ticket")) {
                                if (ticket_category.equals("I Class"))
                                    price_per_ticket = Integer.parseInt(first_single);
                                else if (ticket_category.equals("II Class"))
                                    price_per_ticket = Integer.parseInt(second_single);
                            }
                            setRates(price_per_ticket);
                        } else
                            getRates();
//                            new GetRates().execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.equals(destination) && (!(ticket_type.equals("Platform"))))
                    Toast.makeText(getApplicationContext(), "Please selct different source & " +
                            "destination", Toast.LENGTH_SHORT).show();
                else {
                    if (ticket_type.equals("MonthlyPass")) {
                        no_of_tickets = 1;
                        Toast.makeText(getApplicationContext(), "Seasonal ticket can be purchased" +
                                " only for 1 Traveller", Toast.LENGTH_SHORT).show();
                    } else {
                        if (no_of_tickets < 5)
                            ++no_of_tickets;
                        textView_no_of_tickets.setText(no_of_tickets + "");
                        setRates(price_per_ticket);
                    }
                }

            }
        });
        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.equals(destination) && (!(ticket_type.equals("Platform"))))
                    Toast.makeText(getApplicationContext(), "Please select different source & " +
                            "destination", Toast.LENGTH_SHORT).show();
                else {
                    if (ticket_type.equals("MonthlyPass")) {
                        no_of_tickets = 1;
                        Toast.makeText(getApplicationContext(), "Seasonal ticket can be purchased" +
                                " only for 1 Traveller", Toast.LENGTH_SHORT).show();
                    } else {
                        if (no_of_tickets > 1)
                            --no_of_tickets;
                        textView_no_of_tickets.setText(no_of_tickets + "");
                        setRates(price_per_ticket);
                    }
                }
            }
        });

        spinner_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                source = stations.get(position);
                Log.e("Source", source);
                if (!(spinner_ticket_type.getSelectedItemPosition() == 0)) {
                    Log.e("Source", "Not 0");
                    Log.e("Destination", destination);
                    if ((source.equals(destination))) {
                        Log.e("Source&Destination", destination);
                        Toast.makeText(getApplicationContext(), "Please select different stations" +
                                " for source and destination", Toast.LENGTH_SHORT).show();
                    } else if (!(source.equals(selected_source))) {
                        Log.e("SelectedSource", selected_source);
                        getRates();
//                        new GetRates().execute();
                    } else
                        setRates(price_per_ticket);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ticket_category = ticket_categories[position];
                if (source.equals(destination))
                    Toast.makeText(getApplicationContext(), "Please select different source & " +
                            "destination", Toast.LENGTH_SHORT).show();
                else {
                    if (!(spinner_ticket_type.getSelectedItemPosition() == 0)) {
                        if (!source.equals(selected_source)) {
                            getRates();
//                            new GetRates().execute();
                        } else {
                            if (ticket_type.equals("MonthlyPass")) {
                                switch (ticket_category) {
                                    case "I Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(first_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(first_quarterly);
                                        }
                                        break;
                                    case "II Class":
                                        if (period.equals("Monthly")) {
                                            price_per_ticket = Integer.parseInt(second_monthly);
                                        } else if (period.equals("Quarterly")) {
                                            price_per_ticket = Integer.parseInt(second_quarterly);
                                        }
                                        break;
                                }
                            } else if (ticket_type.equals("Unreserved Ticket")) {
                                if (ticket_category.equals("I Class"))
                                    price_per_ticket = Integer.parseInt(first_single);
                                else if (ticket_category.equals("II Class"))
                                    price_per_ticket = Integer.parseInt(second_single);
                            }
                            setRates(price_per_ticket);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radioGroup_period.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!(spinner_ticket_type.getSelectedItemPosition() == 0)) {

                    switch (checkedId) {
                        case R.id.radioButton_monthly:
                            period = "Monthly";
                            if (source.equals(destination))
                                Toast.makeText(getApplicationContext(), "Please selct different source & " +
                                        "destination", Toast.LENGTH_SHORT).show();
                            else {
                                if (ticket_category.equals("I Class")) {
                                    price_per_ticket = Integer.parseInt(first_monthly);
                                } else if (ticket_category.equals("II Class")) {
                                    price_per_ticket = Integer.parseInt(second_monthly);
                                }
                                setRates(price_per_ticket);
                            }
                            break;
                        case R.id.radioButton_quarterly:
                            period = "Quarterly";
                            if (source.equals(destination))
                                Toast.makeText(getApplicationContext(), "Please selct different source & " +
                                        "destination", Toast.LENGTH_SHORT).show();
                            else {
                                if (ticket_category.equals("I Class")) {
                                    price_per_ticket = Integer.parseInt(first_quarterly);
                                } else if (ticket_category.equals("II Class")) {
                                    price_per_ticket = Integer.parseInt(second_quarterly);
                                }
                                setRates(price_per_ticket);
                            }
                            break;
                    }
                }
            }
        });

        button_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ticket_type.equals("Platform")) {
                    if (source.equals(destination))
                        Toast.makeText(getApplicationContext(), "Please select different source & " +
                                "destination", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(PurchaseTickets.this, PaymentWebViewActivity.class);
                        intent.putExtra("from_station", mydatabase.getData(stations.get(spinner_source.getSelectedItemPosition())));

                        intent.putExtra("ticket_type", ticket_type);
                        if ((ticket_type.equals("Unreserved Ticket")) || (ticket_type.equals("MonthlyPass"))) {
                            intent.putExtra("to_station", "1");
                            intent.putExtra("ticket_category", ticket_category);
                            intent.putExtra("no_of_tickets", "" + no_of_tickets);
                            if (ticket_type.equals("MonthlyPass"))
                                intent.putExtra("ticket_period", period);
                        }
                        intent.putExtra("ticket_amount", "1");
                        intent.putExtra("upload_photo", image + "");
                        intent.putExtra("proof_document", "" + document);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(PurchaseTickets.this, PaymentWebViewActivity.class);
                    intent.putExtra("from_station", mydatabase.getData(stations.get(spinner_source.getSelectedItemPosition())));
                    intent.putExtra("no_of_tickets", "" + no_of_tickets);
                    intent.putExtra("ticket_type", ticket_type);
                    if ((ticket_type.equals("Unreserved Ticket")) || (ticket_type.equals("MonthlyPass"))) {
                        intent.putExtra("to_station", "1");
                        intent.putExtra("ticket_category", ticket_category);
                        if (ticket_type.equals("MonthlyPass"))
                            intent.putExtra("ticket_period", period);
                    }
                    intent.putExtra("ticket_amount", "1");
                    intent.putExtra("upload_photo", image + "");
                    intent.putExtra("proof_document", "" + document);
                    startActivity(intent);
                }
            }
        });
    }

    public void setRates(int price) {
        Log.e("setRates", price + "");
        price_per_ticket = price;
        total_price = no_of_tickets * price_per_ticket;
        textView_price_per_ticket.setText("Rs." + price_per_ticket + ".00/Ticket");
        textView_total_price.setText("Rs." + total_price + ".00");
    }

//    class GetRates extends AsyncTask<Void, Void, Void> {
//
//        JSONObject jsonObject;
//        String message;
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
//                Log.e("Check", "getRates");
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("actiontype", "getRates"));
//                params.add(new BasicNameValuePair("fromStation", mydatabase.getData(stations.get(spinner_source.getSelectedItemPosition()))));
//                params.add(new BasicNameValuePair("toStation", "1"));
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
//            try {
//                JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
//                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
//
//                for (int i = 0; i < responseInfo.length(); i++) {
//                    JSONObject infojsonObject = responseInfo.getJSONObject(i);
//                    JSONObject rates = infojsonObject.getJSONObject("rates");
//
//                    switch (i) {
//                        case 0:
//                            first_monthly = rates.getString("rate");
//                            break;
//                        case 1:
//                            second_monthly = rates.getString("rate");
//                            break;
//                        case 2:
//                            first_quarterly = rates.getString("rate");
//                            break;
//                        case 3:
//                            second_quarterly = rates.getString("rate");
//                            break;
//                        case 4:
//                            first_single = rates.getString("rate");
//                            break;
//                        case 5:
//                            second_single = rates.getString("rate");
//                            break;
//                    }
//                }
//                switch(spinner_ticket_type.getSelectedItemPosition()){
//                    case 0:
//                        ticket_type = "Platform";
//                        break;
//                    case 1:
//                        ticket_type = "Unreserved Ticket";
//                        break;
//                    case 2:
//                        ticket_type = "MonthlyPass";
//                        break;
//                }
//                selected_source = stations.get(spinner_source.getSelectedItemPosition());
//                if (ticket_type.equals("MonthlyPass")) {
//                    Log.e("Check","MP");
//                    ticket_category = ticket_categories[spinner_category.getSelectedItemPosition()];
//                    Log.e("Check","category "+ticket_category);
//                    if (radioGroup_period.getCheckedRadioButtonId() == R.id.radioButton_monthly)
//                        period = "Monthly";
//                    else if (radioGroup_period.getCheckedRadioButtonId() == R.id.radioButton_quarterly)
//                        period = "Quarterly";
//                    Log.e("Check","Period "+period);
//                    if (ticket_category.equals("I Class") && period.equals("Monthly"))
//                        price_per_ticket = Integer.parseInt(first_monthly);
//                    else if (ticket_category.equals("I Class") && period.equals("Quarterly"))
//                        price_per_ticket = Integer.parseInt(first_quarterly);
//                    else if (ticket_category.equals("II Class") && period.equals("Monthly"))
//                        price_per_ticket = Integer.parseInt(second_monthly);
//                    else if (ticket_category.equals("II Class") && period.equals("Quarterly"))
//                        price_per_ticket = Integer.parseInt(second_quarterly);
//                    Log.e("Check","price "+price_per_ticket);
//                } else if (ticket_type.equals("Unreserved Ticket")) {
//                    ticket_category = ticket_categories[spinner_category.getSelectedItemPosition()];
//                    if (ticket_category.equals("I Class"))
//                        price_per_ticket = Integer.parseInt(first_single);
//                    else if (ticket_category.equals("II Class"))
//                        price_per_ticket = Integer.parseInt(second_single);
//                }
//                setRates(price_per_ticket);
//                popupWindow.dismiss();
//            } catch (Exception e) {
//                popupWindow.dismiss();
//                e.printStackTrace();
//            }
//        }
//    }

    public void getRates() {
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
                    parseRates(response);
                    Log.e("Check", message);
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
                params.put("actiontype", "getRates");
                params.put("fromStation", "" + mydatabase.getData(stations.get(spinner_source.getSelectedItemPosition())));
                params.put("toStation", "1");
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

    public void parseRates(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");

            for (int i = 0; i < responseInfo.length(); i++) {
                JSONObject infojsonObject = responseInfo.getJSONObject(i);
                JSONObject rates = infojsonObject.getJSONObject("rates");

                switch (i) {
                    case 0:
                        first_monthly = rates.getString("rate");
                        break;
                    case 1:
                        second_monthly = rates.getString("rate");
                        break;
                    case 2:
                        first_quarterly = rates.getString("rate");
                        break;
                    case 3:
                        second_quarterly = rates.getString("rate");
                        break;
                    case 4:
                        first_single = rates.getString("rate");
                        break;
                    case 5:
                        second_single = rates.getString("rate");
                        break;
                }
            }
            switch (spinner_ticket_type.getSelectedItemPosition()) {
                case 0:
                    ticket_type = "Platform";
                    break;
                case 1:
                    ticket_type = "Unreserved Ticket";
                    break;
                case 2:
                    ticket_type = "MonthlyPass";
                    break;
            }
            selected_source = stations.get(spinner_source.getSelectedItemPosition());
            if (ticket_type.equals("MonthlyPass")) {
                Log.e("Check", "MP");
                ticket_category = ticket_categories[spinner_category.getSelectedItemPosition()];
                Log.e("Check", "category " + ticket_category);
                if (radioGroup_period.getCheckedRadioButtonId() == R.id.radioButton_monthly)
                    period = "Monthly";
                else if (radioGroup_period.getCheckedRadioButtonId() == R.id.radioButton_quarterly)
                    period = "Quarterly";
                Log.e("Check", "Period " + period);
                if (ticket_category.equals("I Class") && period.equals("Monthly"))
                    price_per_ticket = Integer.parseInt(first_monthly);
                else if (ticket_category.equals("I Class") && period.equals("Quarterly"))
                    price_per_ticket = Integer.parseInt(first_quarterly);
                else if (ticket_category.equals("II Class") && period.equals("Monthly"))
                    price_per_ticket = Integer.parseInt(second_monthly);
                else if (ticket_category.equals("II Class") && period.equals("Quarterly"))
                    price_per_ticket = Integer.parseInt(second_quarterly);
                Log.e("Check", "price " + price_per_ticket);
            } else if (ticket_type.equals("Unreserved Ticket")) {
                ticket_category = ticket_categories[spinner_category.getSelectedItemPosition()];
                if (ticket_category.equals("I Class"))
                    price_per_ticket = Integer.parseInt(first_single);
                else if (ticket_category.equals("II Class"))
                    price_per_ticket = Integer.parseInt(second_single);
            }
            setRates(price_per_ticket);
        } catch (Exception e) {
            popupWindow.dismiss();
            e.printStackTrace();
        }
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}
// {"message":"Success","msgcontent":{"requestParam":{"actiontype":"getRates","fromStation":"16","toStation":"1"},"responseInfo":[]}}
// {"message":"Success",
// "msgcontent":
// {"requestParam":
// {"actiontype":"getRates","fromStation":"9","toStation":"1"},
// "responseInfo":[{"rates":
// {"id":"29","from_station_id":"9","to_station_id":"1","rate_type":"I Class","rate_period":"Monthly","rate":"485","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"rates":{"id":"30","from_station_id":"9","to_station_id":"1","rate_type":"II Class","rate_period":"Monthly","rate":"130","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"rates":{"id":"31","from_station_id":"9","to_station_id":"1","rate_type":"I Class","rate_period":"Quarterly","rate":"1330","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"rates":{"id":"32","from_station_id":"9","to_station_id":"1","rate_type":"II Class","rate_period":"Quarterly","rate":"360","status":"A","createdon":"0000-00-00 00:00:00"}}]}}


//{"message":"Success","msgcontent":{"requestParam":{"actiontype":"getRates","fromStation":"21","toStation":"1"},"responseInfo":[
// {"rates":{"id":"20","from_station_id":"1","to_station_id":"21","rate_type":"I Class","rate_period":"Monthly","rate":"745","status":"A","createdon":"2015-10-19 00:00:00"}},
// {"rates":{"id":"47","from_station_id":"1","to_station_id":"21","rate_type":"II Class","rate_period":"Monthly","rate":"215","status":"A","createdon":"2015-10-18 00:00:00"}},
// {"rates":{"id":"74","from_station_id":"1","to_station_id":"21","rate_type":"I Class","rate_period":"Quarterly","rate":"2035","status":"A","createdon":"2015-10-16 00:00:00"}},
// {"rates":{"id":"101","from_station_id":"1","to_station_id":"21","rate_type":"II Class","rate_period":"Quarterly","rate":"590","status":"A","createdon":"2015-10-15 00:00:00"}},
// {"rates":{"id":"128","from_station_id":"1","to_station_id":"21","rate_type":"I Class","rate_period":"Single","rate":"165","status":"A","createdon":"2015-10-15 00:00:00"}},
// {"rates":{"id":"155","from_station_id":"1","to_station_id":"21","rate_type":"II Class","rate_period":"Single","rate":"15","status":"A","createdon":"2015-10-12 00:00:00"}}]}}
