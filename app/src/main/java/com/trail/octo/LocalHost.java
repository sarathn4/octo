package com.trail.octo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.data.tickets.VolleyRequestData;

public class LocalHost extends Activity {

    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_host);
        editText = (EditText) findViewById(R.id.editText_url);
        button = (Button) findViewById(R.id.button_change_url);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    notifyUser("Please enter URL");
                } else {
                    VolleyRequestData.requestURL = editText.getText().toString().trim();
                    notifyUser("URL updated");
                    startActivity(new Intent(LocalHost.this, SplashScreen.class));
                }
            }
        });
    }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}