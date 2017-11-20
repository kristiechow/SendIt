package com.example.kristie.sendit;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by nayibasiselizalde on 11/19/17.
 */

public class AlarmServiceEmail extends Service {

    private String sp = "my_shared_preferences";
    public SharedPreferences sharedPreferences;
    private String email, password;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        String contact = i.getStringExtra("contact");
        String subject = i.getStringExtra("subject");
        String body = i.getStringExtra("body");

        sharedPreferences = getSharedPreferences(sp, 0);
        email = sharedPreferences.getString("email","");
        password = sharedPreferences.getString("password", "");

        GMailSender gMailSender = new GMailSender(email, password);
        try {
            gMailSender.sendMail(subject, body, email, contact);
            Log.d("GMAIL", "WORKS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    // TODO: Get request for the user's email and password

}
