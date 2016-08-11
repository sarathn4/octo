package com.trail.octo;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapters.NavigationExpandableListAdapter;
import com.database.ActiveTicketDatabase;
import com.database.ExpiredTicketDatabase;
import com.database.VerificationCodeDatabase;

public class Transactions extends FragmentActivity implements ActionBar.TabListener {

    ActiveTicketFragment activeTicketFragment;
    ExpiredTicketFragment expiredTicketFragment;
    VerificationCodeFragment verificationCodeFragment;
    ViewPager viewPager;

    NavigationExpandableListAdapter navigationExpandableListAdapter;
    ExpandableListView expandableListView;
    ActiveTicketDatabase activeTicketDatabase;
    ExpiredTicketDatabase expiredTicketDatabase;
    VerificationCodeDatabase verificationCodeDatabase;
    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    RelativeLayout profileBox;
    ImageView profile;
    TextView userName, email;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        expiredTicketDatabase = new ExpiredTicketDatabase(this);
        verificationCodeDatabase = new VerificationCodeDatabase(this);
        activeTicketDatabase = new ActiveTicketDatabase(this);
        onCreateDrawer();
        activeTicketFragment = new ActiveTicketFragment();
        expiredTicketFragment = new ExpiredTicketFragment();
        verificationCodeFragment = new VerificationCodeFragment();

        viewPager = (ViewPager) findViewById(R.id.viewpager_transaction);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(40, 0, 40, 0);
        viewPager.setPageMargin(10);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);

        ActionBar.Tab tab = actionBar.newTab();
        tab.setText("Active Tickets");
        tab.setTabListener(this);
        actionBar.addTab(tab);

        ActionBar.Tab tab1 = actionBar.newTab();
        tab1.setText("Verification Code");
        tab1.setTabListener(this);
        actionBar.addTab(tab1);

        ActionBar.Tab tab2 = actionBar.newTab();
        tab2.setText("Expired Tickets");
        tab2.setTabListener(this);
        actionBar.addTab(tab2);

        //viewPager.setCurrentItem(1);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = activeTicketFragment;
                    break;
                case 1:
                    fragment = verificationCodeFragment;
                    break;
                case 2:
                    fragment = expiredTicketFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
