package com.trail.octo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Identity extends Activity {

    RadioGroup radioGroup;
    RadioButton radioButton_pancard, radioButton_dl, radioButton_voterid, radioButton_aadharcard,
            radioButton_passport;
    ImageView imageView;
    Button button;

    Bitmap pan_card = null, driving_license = null, aadhar_card = null, voter_id = null, passport = null;
    SharedPreferences sharedPreferences;

    String encodedmessage = "";

    private final static int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_identity);
        radioButton_pancard = (RadioButton) findViewById(R.id.radioButton_pancard);
        radioButton_dl = (RadioButton) findViewById(R.id.radioButton_dl);
        radioButton_voterid = (RadioButton) findViewById(R.id.radioButton_voterid);
        radioButton_aadharcard = (RadioButton) findViewById(R.id.radioButton_aadharcard);
        radioButton_passport = (RadioButton) findViewById(R.id.radioButton_passport);

        imageView = (ImageView) findViewById(R.id.imageView_photo);
        button = (Button) findViewById(R.id.button_buy);

        sharedPreferences = getSharedPreferences("docs_data", MODE_PRIVATE);
        loaddata();

        if (pan_card == null) {
            radioButton_pancard.setEnabled(false);
        }
        if (driving_license == null) {
            radioButton_dl.setEnabled(false);
        }
        if (aadhar_card == null) {
            radioButton_aadharcard.setEnabled(false);
        }
        if (voter_id == null) {
            radioButton_voterid.setEnabled(false);
        }
        if (passport == null) {
            radioButton_passport.setEnabled(false);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select the Document",
                            Toast.LENGTH_SHORT).show();
                } else if (encodedmessage.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please capture the Photo",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Identity.this, PurchaseTickets.class);
                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_pancard) {
                        intent.putExtra("document", "PAN Card");
                    } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_aadharcard) {
                        intent.putExtra("document", "Aadhar Card");
                    } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_dl) {
                        intent.putExtra("document", "Driving License");
                    } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_passport) {
                        intent.putExtra("document", "Passport");
                    } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_voterid) {
                        intent.putExtra("document", "Voter ID");
                    }
                    intent.putExtra("image", "" + encodedmessage);
                    startActivity(intent);
                }
            }
        });
    }

    public void loaddata() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Check", "ActivityResult");
        Bitmap image = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                image = (Bitmap) data.getExtras().get("data");
                image = Bitmap.createScaledBitmap(image, 100, 100, true);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                byte[] objects = bytes.toByteArray();

                encodedmessage = Base64.encodeToString(objects, Base64.DEFAULT);
                imageView.setImageBitmap(image);
            }
        }
    }

    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Log.e("Check", "Go To Settings");
                Toast.makeText(getApplicationContext(), "OCTO requires this Permission to upload Images!", Toast.LENGTH_LONG).show();
                final Intent i = new Intent();
                i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + Identity.this.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);// Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                Log.e("Check", "Requesting");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e("Check", "Already Granted");
            //Already Permission Granted
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "Octo requires Camera Permission to buy a Ticket!", Toast.LENGTH_SHORT);
                    requestCameraPermission();
                }
                break;
        }
    }
}