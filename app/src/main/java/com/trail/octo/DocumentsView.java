package com.trail.octo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class DocumentsView extends Activity {
    LinearLayout documentview_layout_pan_card, documentview_layout_driving_license,
            documentview_layout_aadhar_card, documentview_layout_voter_id,documentview_layout_passport;
    ImageView imageView_document;
    Bitmap pan_card = null, driving_license = null, aadhar_card = null, voter_id = null, passport = null;
    Bitmap empty_image = null;
    SharedPreferences sharedPreferences;
    Bitmap current_image = null;

    PopupWindow popupWindow;
    View view;
    TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_view);
        empty_image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_ticket_doc);

        sharedPreferences = getSharedPreferences("docs_data", MODE_PRIVATE);
        documentview_layout_pan_card = (LinearLayout) findViewById(R.id.documentview_layout_pan_card);
        documentview_layout_driving_license = (LinearLayout) findViewById(R.id.documentview_layout_driving_license);
        documentview_layout_aadhar_card = (LinearLayout) findViewById(R.id.documentview_layout_aadhar_card);
        documentview_layout_voter_id = (LinearLayout) findViewById(R.id.documentview_layout_voter_id);
        documentview_layout_passport = (LinearLayout) findViewById(R.id.documentview_layout_passport);

        imageView_document = (ImageView) findViewById(R.id.imageView_document);
        byte[] decodedString;
        String encodedmessage = "";

        encodedmessage = sharedPreferences.getString("pancard", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            pan_card = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences.getString("drivinglicense", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            driving_license = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences.getString("voterid", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            voter_id = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences.getString("aadharcard", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            aadhar_card = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        encodedmessage = sharedPreferences.getString("passport", "Empty");
        if (!(encodedmessage.equals("Empty") || encodedmessage.isEmpty())) {
            decodedString = Base64.decode(encodedmessage, Base64.DEFAULT);
            passport = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        documentview_layout_pan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentview_layout_pan_card.setBackgroundColor(Color.parseColor("#F2E715"));
                documentview_layout_aadhar_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_voter_id.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_driving_license.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_passport.setBackgroundColor(Color.parseColor("#ffffff"));

                if(pan_card!=null) {
                    imageView_document.setImageBitmap(pan_card);
                    current_image = pan_card;
                }
                else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
            }
        });

        documentview_layout_driving_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentview_layout_pan_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_aadhar_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_voter_id.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_driving_license.setBackgroundColor(Color.parseColor("#F2E715"));
                documentview_layout_passport.setBackgroundColor(Color.parseColor("#ffffff"));

                if(driving_license!=null) {
                    imageView_document.setImageBitmap(driving_license);
                    current_image = driving_license;
                }
                else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
            }
        });

        documentview_layout_voter_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentview_layout_pan_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_aadhar_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_voter_id.setBackgroundColor(Color.parseColor("#F2E715"));
                documentview_layout_driving_license.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_passport.setBackgroundColor(Color.parseColor("#ffffff"));

                if(voter_id!=null) {
                    imageView_document.setImageBitmap(voter_id);
                    current_image = voter_id;
                }
                else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
            }
        });

        documentview_layout_aadhar_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentview_layout_pan_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_aadhar_card.setBackgroundColor(Color.parseColor("#F2E715"));
                documentview_layout_voter_id.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_driving_license.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_passport.setBackgroundColor(Color.parseColor("#ffffff"));

                if(aadhar_card!=null) {
                    imageView_document.setImageBitmap(aadhar_card);
                    current_image = aadhar_card;
                }
                else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
            }
        });

        documentview_layout_passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentview_layout_pan_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_aadhar_card.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_voter_id.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_driving_license.setBackgroundColor(Color.parseColor("#ffffff"));
                documentview_layout_passport.setBackgroundColor(Color.parseColor("#F2E715"));

                if(passport!=null) {
                    imageView_document.setImageBitmap(passport);
                    current_image = passport;
                }
                else {
                    imageView_document.setImageBitmap(empty_image);
                    current_image = null;
                }
            }
        });

        imageView_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_image!=null){
                    LayoutInflater inflater = (LayoutInflater) getBaseContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.popup_touch_image, null);
                    touchImageView = (TouchImageView) view.findViewById(R.id.touch_imageview);
                    popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT, true);
                    popupWindow.setFocusable(false);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);
                    touchImageView.setImageBitmap(current_image);
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 40);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(popupWindow!=null) {
            if(popupWindow.isShowing()) {
                Log.e("PopupCheck", "Not null");
                popupWindow.dismiss();
                popupWindow = null;
            }
            else
                super.onBackPressed();
        }
        else
            super.onBackPressed();
    }
}
