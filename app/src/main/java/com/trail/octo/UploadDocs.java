package com.trail.octo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class UploadDocs extends Activity {

    int REQUEST_CAMERA = 0;
    int SELECT_FILE = 1;
    boolean check = true;

    private final static int PERMISSION_REQUEST_CAMERA = 1000;
    private final static int PERMISSION_REQUEST_READ_EXTERAL_STORAGE = 1001;

    String user_name = "";
    String file_name = "doc_file.png";
    SharedPreferences sharedPreferences, sharedPreferences_docs;
    String file_type = "ProfilePhoto";
    String encodedmessage = "";
    boolean pan = false, aadhar = false, driving = false, voter = false, passport = false;
    ImageView imageView_camera, imageView_gallery, imageView_profilepic;
    LinearLayout linearLayout_pan, linearLayout_driving, linearLayout_voter, linearLayout_aadhar, linearLayout_passport;

    Button button_submit;

    ImageView upload_docs_pan_select, upload_docs_driving_select, upload_docs_aadhar_select, upload_docs_voter_select,
            upload_docs_passport_select;

    ImageView popup_imageView_camera, popup_imageView_gallery;
    PopupWindow popupWindow_select_source, popupWindow;
    View view, view_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_docs);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        user_name = sharedPreferences.getString("user_name", "");

        sharedPreferences_docs = getSharedPreferences("docs_data", MODE_PRIVATE);

        upload_docs_pan_select = (ImageView) findViewById(R.id.upload_docs_pan_select);
        upload_docs_driving_select = (ImageView) findViewById(R.id.upload_docs_driving_select);
        upload_docs_aadhar_select = (ImageView) findViewById(R.id.upload_docs_aadhar_select);
        upload_docs_voter_select = (ImageView) findViewById(R.id.upload_docs_voter_select);
        upload_docs_passport_select = (ImageView) findViewById(R.id.upload_docs_passport_select);

        if (sharedPreferences_docs.getString("pancard", "").equals(""))
            pan = true;
        else
            upload_docs_pan_select.setVisibility(View.VISIBLE);
        if (sharedPreferences_docs.getString("drivinglicense", "").equals(""))
            driving = true;
        else
            upload_docs_driving_select.setVisibility(View.VISIBLE);
        if (sharedPreferences_docs.getString("voterid", "").equals(""))
            voter = true;
        else
            upload_docs_voter_select.setVisibility(View.VISIBLE);
        if (sharedPreferences_docs.getString("aadharcard", "").equals(""))
            aadhar = true;
        else
            upload_docs_aadhar_select.setVisibility(View.VISIBLE);
        if (sharedPreferences_docs.getString("passport", "").equals(""))
            passport = true;
        else
            upload_docs_passport_select.setVisibility(View.VISIBLE);

        if (savedInstanceState != null)
            file_type = savedInstanceState.getString("file_type");
        imageView_camera = (ImageView) findViewById(R.id.upload_docs_camera);
        imageView_gallery = (ImageView) findViewById(R.id.upload_docs_gallery);
        imageView_profilepic = (ImageView) findViewById(R.id.upload_docs_profile_pic);

        linearLayout_aadhar = (LinearLayout) findViewById(R.id.upload_docs_aadhar);
        linearLayout_driving = (LinearLayout) findViewById(R.id.upload_docs_driving);
        linearLayout_pan = (LinearLayout) findViewById(R.id.upload_docs_pan);
        linearLayout_voter = (LinearLayout) findViewById(R.id.upload_docs_voter);
        linearLayout_passport = (LinearLayout) findViewById(R.id.upload_docs_passport);

        button_submit = (Button) findViewById(R.id.upload_docs_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }
            }
        });
        imageView_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askCameraPermission()) {
                    file_type = "profilepic";

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });

        imageView_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askGalleryPermission()) {
                    file_type = "profilepic";

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                }
            }
        });
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.loading_popup_window, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        view_select = layoutInflater.inflate(R.layout.popup_image_capture, null);
        popup_imageView_camera = (ImageView) view_select.findViewById(R.id.popup_imageView_camera);
        popup_imageView_gallery = (ImageView) view_select.findViewById(R.id.popup_imageView_gallary);

        popupWindow_select_source = new PopupWindow(view_select, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow_select_source.setFocusable(false);
        popupWindow_select_source.setBackgroundDrawable(new BitmapDrawable());
        popupWindow_select_source.setOutsideTouchable(true);

        popup_imageView_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askGalleryPermission()) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                }
            }
        });
        popup_imageView_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askCameraPermission()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });
        linearLayout_driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driving) {
                    file_type = "drivinglicense";
                    popupWindow_select_source.showAtLocation(view_select, Gravity.CENTER, 0, 40);
                } else
                    Toast.makeText(getApplicationContext(), "You have already uploaded the Driving license", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayout_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pan) {
                    file_type = "pancard";
                    popupWindow_select_source.showAtLocation(view_select, Gravity.CENTER, 0, 40);
                } else
                    Toast.makeText(getApplicationContext(), "You have already uploaded the Pan Card", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayout_aadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aadhar) {
                    file_type = "aadharcard";
                    popupWindow_select_source.showAtLocation(view_select, Gravity.CENTER, 0, 40);
                } else
                    Toast.makeText(getApplicationContext(), "You have already uploaded the Aadhar Card", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayout_voter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voter) {
                    file_type = "voterid";
                    popupWindow_select_source.showAtLocation(view_select, Gravity.CENTER, 0, 40);
                } else
                    Toast.makeText(getApplicationContext(), "You have already uploaded the Voter id", Toast.LENGTH_SHORT).show();
            }
        });

        linearLayout_passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passport) {
                    file_type = "passport";
                    popupWindow_select_source.showAtLocation(view_select, Gravity.CENTER, 0, 40);
                } else
                    Toast.makeText(getApplicationContext(), "You have already uploaded the Passport", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Check", "ActivityResult");
        Bitmap image = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                image = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                byte[] objects = bytes.toByteArray();

                encodedmessage = Base64.encodeToString(objects, Base64.DEFAULT);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                Log.e("Check", "uri obtained");
                file_name = selectedImageUri.getLastPathSegment();
                String[] projection = {MediaStore.MediaColumns.DATA};

                Log.e("Check", "Projection Obtained");
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                Bitmap temp;
                Log.e("Check", "Path " + selectedImagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                temp = BitmapFactory.decodeFile(selectedImagePath, options);

                Log.e("Check", "Decoded");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] objects = byteArrayOutputStream.toByteArray();

                encodedmessage = Base64.encodeToString(objects, Base64.DEFAULT);
                image = temp;
            }
            SharedPreferences sharedPreferences = getSharedPreferences("docs_data", MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(file_type, encodedmessage);
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), DocumentUploadPreview.class);
            intent.putExtra("file_type", file_type);
            intent.putExtra("file_name", file_name);
            //intent.putExtra("encodedmessage",encodedmessage);

            startActivity(intent);
//            popupWindow_document_preview.showAtLocation(view_preview, Gravity.CENTER, 0, 40);
//            popup_imageView_document.setImageBitmap(image);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_type", file_type);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow_select_source != null) {
            if (popupWindow_select_source.isShowing())
                popupWindow_select_source.dismiss();
//        } else if (!(sharedPreferences.getString("user_name", "User").equals("User"))) {
//            Log.e("Username","User"+sharedPreferences.getString("user_name", "User"));
//            Intent intent = new Intent(getApplicationContext(), Home.class);
//            startActivity(intent);
            else {
                if (user_name.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }
            }
        } else {
            if (user_name.equals("")) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }
    }

    public boolean askCameraPermission() {
        check = false;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(UploadDocs.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadDocs.this,
                    Manifest.permission.CAMERA)) {
                Log.e("Permission", "Explain here");
                Toast.makeText(getApplicationContext(), "OCTO requires this Permission to upload Images!", Toast.LENGTH_LONG).show();
                final Intent i = new Intent();
                i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + UploadDocs.this.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                Log.e("Permission", "Requesting");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true;
        }
        return check;
    }

    public boolean askGalleryPermission() {
        check = false;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(UploadDocs.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadDocs.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e("Permission", "Explain here");
                Toast.makeText(getApplicationContext(), "OCTO requires this Permission to upload Images!", Toast.LENGTH_LONG).show();
                final Intent i = new Intent();
                i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + UploadDocs.this.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.
                Log.e("Permission", "Requesting");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXTERAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true;
        }
        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    check = true;
                } else {
                    check = false;
                    Log.e("Permission", "Denied");
                    Toast.makeText(getApplicationContext(),
                            "OCTO cant proceed without this Permission. Please enable in Settings", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_READ_EXTERAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    check = true;
                } else {
                    check = false;
                    Log.e("Permission", "Denied");
                    Toast.makeText(getApplicationContext(),
                            "OCTO cant proceed without this Permission. Please enable in Settings", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}