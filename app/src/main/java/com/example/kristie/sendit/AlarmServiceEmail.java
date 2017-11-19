package com.example.kristie.sendit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by nayibasiselizalde on 11/19/17.
 */

public class AlarmServiceEmail extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        String contact = i.getStringExtra("contact");
        String subject = i.getStringExtra("subject");
        String body = i.getStringExtra("body");

        GMailSender gMailSender = new GMailSender("nayib.asis@gmail.com", "lenaBem@n54a");
        try {
            gMailSender.sendMail(subject, body, "nayib.asis@gmail.com", contact);
            Log.d("GMAIL", "WORKS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    // TODO: Get request for the user's email and password

}
