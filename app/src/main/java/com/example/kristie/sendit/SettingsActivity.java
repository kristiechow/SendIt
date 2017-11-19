package com.example.kristie.sendit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Kristie on 11/19/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void logout(View view){

        Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        SettingsActivity.this.startActivity(myIntent);
        finish();
    }

}
