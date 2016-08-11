package com.trail.octo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.adapters.NavigationExpandableListAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.application.AppController;
import com.data.tickets.ActiveTicket;
import com.data.tickets.VolleyRequestData;
import com.database.ActiveTicketDatabase;
import com.database.ExpiredTicketDatabase;
import com.database.Mydatabase;
import com.database.VerificationCodeDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.trail.octo.R.id.view_flipper_layout;

public class Home extends Activity {

    public static String TAG = "LoadTickets";
    ImageView purchaseticket, transanctionlog;
    ActiveTicketDatabase activeTicketDatabase;
    ExpiredTicketDatabase expiredTicketDatabase;
    VerificationCodeDatabase verificationCodeDatabase;

    ViewFlipper viewFlipper;
    View viewLoader;
    Animation slide_in_left, slide_out_left;
    Mydatabase mydatabase;
    SharedPreferences sharedPreferences, docs_sharedPreferences;
    DrawerLayout drawerLayout;
    RelativeLayout profileBox;
    ExpandableListView expandableListView;
    ImageView profile;
    TextView userName, email;
    ArrayList<ActiveTicket> activeTickets;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionBar;
    NavigationExpandableListAdapter navigationExpandableListAdapter;
    private float lastX;
    public BroadcastReceiver broadcastReceiver;

    String type = "ALL";
    String validity = "active";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        docs_sharedPreferences = getSharedPreferences("docs_data", MODE_PRIVATE);
        onCreateDrawer();
        mydatabase = new Mydatabase(this);
        expiredTicketDatabase = new ExpiredTicketDatabase(this);
        verificationCodeDatabase = new VerificationCodeDatabase(this);

        purchaseticket = (ImageView) findViewById(R.id.imageView_buyticket);
        transanctionlog = (ImageView) findViewById(R.id.imageView_transaction_log);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        purchaseticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Identity.class);
                startActivity(intent);
            }
        });
        transanctionlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Transactions.class);
                startActivity(intent);
            }
        });
        activeTicketDatabase = new ActiveTicketDatabase(this);
        addViews();

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent touchevent) {
                switch (touchevent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = touchevent.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float currentX = touchevent.getX();
                        // Handling left to right screen swap.
                        if (lastX < currentX) {
                            // If there aren't any other children, just break.
                            if (viewFlipper.getDisplayedChild() == 0)
                                break;
                            // Next screen comes in from left.
                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_from_left);
                            // Current screen goes out from right.
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_to_right);
                            // Display next screen.
                            viewFlipper.showNext();
                        }
                        if (lastX > currentX) {
                            // If there is a child (to the left), kust break.
                            if (viewFlipper.getDisplayedChild() == 1)
                                break;

                            // Next screen comes in from right.
                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_from_right);
                            // Current screen goes out from left.
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_to_left);
                            // Display previous screen.
                            viewFlipper.showPrevious();
                        }
                        break;

                }
                return true;
            }
        });
        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.left_in);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.left_out);
        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_left);
        if (activeTickets.size() > 1)
            viewFlipper.startFlipping();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("UpdateTickes", "Updating");
                if (intent.getAction().equals("UpdateTickets")) {
                    viewFlipper.removeAllViews();
                    addViews();
                }
            }
        };
    }

    public void onCreateDrawer() {
        try {
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Octo and purchase tickets easily");
            sendIntent.setType("text/plain");

            Bitmap profile_image = null;
            profileBox = (RelativeLayout) findViewById(R.id.profileBox);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            profile = (ImageView) findViewById(R.id.profile);
            userName = (TextView) findViewById(R.id.userName);
            email = (TextView) findViewById(R.id.id_email);

            userName.setText(sharedPreferences.getString("user_name", "User"));
            email.setText(sharedPreferences.getString("email_id", "octo@octo.com"));
            Log.e("Name", sharedPreferences.getString("user_name", "User"));
            byte[] decodedString;
            String encodedmessage = "";

            encodedmessage = sharedPreferences.getString("profilepic", "Empty");
            Log.e("Encodedmessage", encodedmessage);
            if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
                decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
                profile_image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            if (profile_image != null)
                profile.setImageBitmap(profile_image);
            actionBar = getActionBar();

            expandableListView = (ExpandableListView) findViewById(R.id.navList);
            expandableListView.setGroupIndicator(null);
            expandableListView.setVerticalFadingEdgeEnabled(true);
            navigationExpandableListAdapter = new NavigationExpandableListAdapter(this);
            expandableListView.setAdapter(navigationExpandableListAdapter);

            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    switch (groupPosition) {
                        case 0:
                            Intent home_intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(home_intent);
                            break;
                        case 1:
                            Intent profile_intent = new Intent(getApplicationContext(), UpdateProfile.class);
                            startActivity(profile_intent);
                            break;
                        case 2:
                            Intent document_intent = new Intent(getApplicationContext(), UploadDocs.class);
                            startActivity(document_intent);
                            break;
                        case 3:
                            Intent settings_intent = new Intent(getApplicationContext(), Settings.class);
                            startActivity(settings_intent);
                            break;
                        case 4:
                            Intent feedback_intent = new Intent(getApplicationContext(), Feedback.class);
                            startActivity(feedback_intent);
                            break;
                        case 5:
                            Intent reset_intent = new Intent(getApplicationContext(), ResetDevice.class);
                            startActivity(reset_intent);
                            break;
                        case 6:
                            return false;
                        case 7:
                            startActivity(sendIntent);
                            break;
                        case 8:
                            break;
                        case 9:
                            SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putBoolean("login_status", false);
                            editor.commit();

                            SharedPreferences sharedPreferences1 = getSharedPreferences("docs_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            editor1.clear();
                            editor1.commit();

                            activeTicketDatabase.deleteData();
                            expiredTicketDatabase.deleteData();
                            verificationCodeDatabase.deleteData();
                            Intent logout_intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(logout_intent);
                            break;
                    }
                    return true;
                }
            });
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    if (groupPosition == 6) {
                        if (childPosition == 0) {
                            Intent intent = new Intent(getApplicationContext(), NewComplaint.class);
                            startActivity(intent);
                        } else if (childPosition == 1) {
                            Intent intent = new Intent(getApplicationContext(), ListOfComplaints.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                    return false;
                }
            });


            mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                    drawerLayout, /* DrawerLayout object */
                    R.drawable.ic_hamburger, /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open, /* "open drawer" description */
                    R.string.drawer_close /* "close drawer" description */
            ) {
                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    actionBar.setTitle(getTitle());
                    //actionBar.setTitle("Home");
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    actionBar.setTitle("Profile");
                }
            };
            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(mDrawerToggle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addViews() {
        activeTickets = activeTicketDatabase.getData("ALL");

        for (final ActiveTicket activeTicket : activeTickets) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewLoader = (View) inflater.inflate(R.layout.view_filipper_ticket_view, null);
            LinearLayout linearLayout = (LinearLayout) viewLoader.findViewById(view_flipper_layout);
            LinearLayout destinationlayout = (LinearLayout) viewLoader.findViewById(R.id.view_flipper_layout_destination);
            if (activeTicket.getTicket_type().equals("Platform")) {
                linearLayout.setBackground(getResources().getDrawable(R.drawable.flipper_ticket_pf));
                destinationlayout.setVisibility(View.INVISIBLE);
            } else if (activeTicket.getTicket_type().equals("MonthlyPass")) {
                linearLayout.setBackground(getResources().getDrawable(R.drawable.flipper_ticket_st));
                destinationlayout.setVisibility(View.VISIBLE);
                TextView textView_destination = (TextView) viewLoader.findViewById(R.id.flipper_textView_destination);
                textView_destination.setText(mydatabase.getStationName(activeTicket.getTo_station()));
            } else if (activeTicket.getTicket_type().equals("Unreserved Ticket")) {
                linearLayout.setBackground(getResources().getDrawable(R.drawable.flipper_ticket_uts));
                destinationlayout.setVisibility(View.VISIBLE);
                TextView textView_destination = (TextView) viewLoader.findViewById(R.id.flipper_textView_destination);
                textView_destination.setText(mydatabase.getStationName(activeTicket.getTo_station()));
            }
            TextView textView_ticket_no = (TextView) viewLoader.findViewById(R.id.flipper_textView_ticketno);
            TextView textView_station = (TextView) viewLoader.findViewById(R.id.flipper_textView_source);
            TextView textView_date = (TextView) viewLoader.findViewById(R.id.flipper_textView_ticket_date);
            TextView textView_no_of_tickets = (TextView) viewLoader.findViewById(R.id.flipper_textView_no_of_tickets);
            textView_no_of_tickets.setText(activeTicket.getNo_of_tickets() + " TICKETS");

            textView_ticket_no.setText(activeTicket.getTicket_no());
            textView_station.setText(mydatabase.getStationName(activeTicket.getFrom_station()));
            textView_date.setText(getDateAndTime(activeTicket.getActivated_date()));
            viewLoader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActiveTicketView.class);
                    intent.putExtra("activeticket", activeTicket);
                    startActivity(intent);
                }
            });
            viewFlipper.addView(viewLoader);
        }

        if (activeTickets.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view_pf = (View) inflater.inflate(R.layout.empty_ticket_view_pf, null);
            viewFlipper.addView(view_pf);

            View view_uts = inflater.inflate(R.layout.empty_ticket_view_uts, null);
            viewFlipper.addView(view_uts);

            View view_st = inflater.inflate(R.layout.empty_ticket_view_st, null);
            viewFlipper.addView(view_st);
            //viewFlipper.startFlipping();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (String.valueOf(this.getComponentName().getShortClassName()).equals(".Home"))
                showExitAlert();
            else {
                super.onBackPressed();
            }
        }
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

    public String getDateAndTime(String date_time) {
        String splitDateAndTime = "";
        int index = date_time.indexOf(" ");
        String date = date_time.substring(0, index);
        String time = date_time.substring(index + 1, date_time.length());

        splitDateAndTime = changeDateFormat(date.trim()) + "|" + changeTimeFormat(time.trim());
        return splitDateAndTime;
    }

    public String changeDateFormat(String old_date) {
        String formatted_date = "";
        DateFormat writeFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = readFormat.parse(old_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_date = writeFormat.format(date);
        }
        return formatted_date;
    }

    public String changeTimeFormat(String old_time) {
        String formatted_time = "";
        DateFormat writeFormat = new SimpleDateFormat("hh:mm:ss aa");
        DateFormat readFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = readFormat.parse(old_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formatted_time = writeFormat.format(date);
        }
        return formatted_time.toUpperCase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("UpdateTickets"));
        getActiveTickets();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void getActiveTickets() {
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading Tickets...");
//        pDialog.setCancelable(false);
//        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyRequestData.requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    parseActiveTickets(response);
//                    pDialog.hide();
                    sendBroadcast();
                } catch (Exception e) {
                    e.printStackTrace();
//                    pDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error:" + error.toString());
//                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("actiontype", "getActiveTickets");
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

    public void parseActiveTickets(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject msgcontent = jsonObject.getJSONObject("msgcontent");
            if (msgcontent.has("responseInfo")) {
                JSONArray responseInfo = msgcontent.getJSONArray("responseInfo");
                activeTicketDatabase.deleteData();
                for (int i = 0; i < responseInfo.length(); i++) {
                    JSONObject activatedTicket = responseInfo.getJSONObject(i);
                    String ticket_id = activatedTicket.getString("id");
                    String ticket_no = activatedTicket.getString("ticket_number");
                    String ticket_code = activatedTicket.getString("ticket_code");
                    String from_station = activatedTicket.getString("from_station");
                    String to_station = activatedTicket.getString("to_station");
                    String ticket_type = activatedTicket.getString("ticket_type");
                    String no_of_tickets = activatedTicket.getString("no_of_tickets");
                    String ticket_category = activatedTicket.getString("ticket_category");
                    String ticket_period = activatedTicket.getString("ticket_period");
                    String ticket_amount = activatedTicket.getString("ticket_amount");
                    String purchased_date = activatedTicket.getString("purchased_on");
                    String activated_date = activatedTicket.getString("activated_on");
                    String activated_station = activatedTicket.getString("activated_station_code");
                    String valid_date = activatedTicket.getString("valid_till");
                    String proof_document = activatedTicket.getString("proof_document");
                    String photo = activatedTicket.getString("upload_photo");
                    String validated_count = activatedTicket.getString("validated_count");
                    String imei_device = activatedTicket.getString("imei_device");

                    boolean check = activeTicketDatabase.insertActiveTicket(ticket_id, ticket_no, ticket_code,
                            from_station, to_station, ticket_type, no_of_tickets,
                            ticket_category, ticket_period, ticket_amount,
                            purchased_date, activated_date, activated_station,
                            valid_date, proof_document, photo, validated_count, imei_device);
                    Log.e("InsertionCheck", check + "");
                }
                if (validity.equals("active")) {
                    activeTickets.clear();
                    activeTickets = activeTicketDatabase.getData(type);
                }

                activeTickets.clear();
                activeTickets = activeTicketDatabase.getData(type);
                notifyUser("Tickets are Updated");
            }
        } catch (Exception e) {
            Log.e("Check", "Exception");
            e.printStackTrace();
        }
    }

    public void sendBroadcast() {
        Intent intent = new Intent("UpdateTickets");
        sendBroadcast(intent);
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

}

//{"message":"Success",
// "msgcontent":{
// "requestParam":{"actiontype":"getRoutes"},
// "responseInfo":[
// {"routes":{"id":"1","route_name":"CCG-VR","station_start":"CCG","station_end":"VR","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA::BYR::NIG::BSR::NSP::VR","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"2","route_name":"CCG-NSP","station_start":"CCG","station_end":"NSP","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA::BYR::NIG::BSR::NSP","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"3","route_name":"CCG-BSR","station_start":"CCG","station_end":"BSR","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA::BYR::NIG::BSR","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"4","route_name":"CCG-NIG","station_start":"CCG","station_end":"NIG","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA::BYR::NIG","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"5","route_name":"CCG-BYR","station_start":"CCG","station_end":"BYR","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA::BYR","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"6","route_name":"CCG-MIRA","station_start":"CCG","station_end":"MIRA","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC::MIRA","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"7","route_name":"CCG-DIC","station_start":"CCG","station_end":"DIC","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI::DIC","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"8","route_name":"CCG-BVI","station_start":"CCG","station_end":"BVI","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE::BVI","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"9","route_name":"CCG-KILE","station_start":"CCG","station_end":"KILE","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD::KILE","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"10","route_name":"CCG-MDD","station_start":"CCG","station_end":"MDD","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN::MDD","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"11","route_name":"CCG-GMN","station_start":"CCG","station_end":"GMN","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS::GMN","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"12","route_name":"CCG-JOS","station_start":"CCG","station_end":"JOS","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH::JOS","status":"A","createdon":"0000-00-00 00:00:00"}},
// {"routes":{"id":"13","route_name":"CCG-ADH","station_start":"CCG","station_end":"ADH","inter_stations":"CCG::MEL::CYR::GTR::BCL::MX::PL::EPR::DDR::MRU::MM::BA::KHAR::STC::VLP::ADH","status":"A","createdon":"0000-00-00 00:00:00"}}]}}
