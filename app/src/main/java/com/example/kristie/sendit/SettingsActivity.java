package com.example.kristie.sendit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Kristie on 11/19/17.
 */

public class SettingsActivity extends AppCompatActivity {

    private RadioButton rd1;
    private RadioButton rd2;
    private RadioButton rd3;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rd1 = (RadioButton) findViewById(R.id.radioNone);
        rd2 = (RadioButton) findViewById(R.id.radioVibrate);
        rd3 = (RadioButton) findViewById(R.id.radioSound);
    }



    public void logout(View view){

        Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        SettingsActivity.this.startActivity(myIntent);
        finish();
    }

}
