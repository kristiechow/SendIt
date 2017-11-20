package com.example.kristie.sendit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by nayibasiselizalde on 11/18/17.
 */

public class MailSenderActivity extends Activity {

    @Bind(R.id.email_recipient) EditText recipient;
    @Bind(R.id.email_subject) EditText emailSubject;
    @Bind(R.id.email_text) EditText emailText;
    @Bind(R.id.save_email_button) Button save;
    @Bind(R.id.set_email_timer) Button setTimer;
    @Bind(R.id.email_contacts) Button cont;
    @Bind(R.id.cancel_email_button) Button cancel;

    public int hour;
    public int minute;
    private int mHour;
    private int mMinute;

    private int getHour, getMinute;

    private String email, password;

    private String sContact;
    private String sSubject;
    private String sBody;
    private String sp = "my_shared_preferences";

    private Calendar c;
    private AlarmManager aManager;
    private PendingIntent pIntent;
    public SharedPreferences sharedPreferences;
    public JSONArray emailArray;

    private static final int REQUEST_CODE = 1;
    static final int TIME_DIALOG_ID=1;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mScheduledEmailReference;
    private Map<String, String> emailData = new HashMap<String, String>();
    public static final String FIREBASE_CHILD_SCHEDULED_EMAIL = "scheduledEmails";

    public MailSenderActivity() {
        // Assign current Date and Time Values to Variables
        c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        ButterKnife.bind(this);

        mScheduledEmailReference = FirebaseDatabase.getInstance().getReference().child(FIREBASE_CHILD_SCHEDULED_EMAIL);

        emailArray = new JSONArray();
        c = Calendar.getInstance();

        getHour = c.get(Calendar.HOUR);
        getMinute = c.get(Calendar.MINUTE);



        cont.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        setTimer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(TIME_DIALOG_ID);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if(!validateEmail()){
                    onSendFailure();
                    return;
                }

                sContact = recipient.getText().toString();
                sSubject = emailSubject.getText().toString();
                sBody = emailText.getText().toString();

                Intent i = new Intent(MailSenderActivity.this, AlarmServiceEmail.class);
                i.putExtra("contact", sContact);
                i.putExtra("subject", sSubject);
                i.putExtra("body", sBody);

                emailData.put("Subject", sSubject);
                emailData.put("Contact", sContact);
                emailData.put("Body", sBody);

                if (emailData.get("Subject").length() > 0 ){
                    editScheduledEmailToFirebase(emailData);
                }
                else {
                    saveScheduledEmailToFirebase(emailData);
                }

                pIntent = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                aManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
                Toast.makeText(getApplicationContext(), "Email scheduled! ",Toast.LENGTH_SHORT).show();

                onSaveClickedSP(v);
                send15minNotification(hour, minute);
                sendsentNotification(hour, minute);

                Intent intent = new Intent(MailSenderActivity.this, SuccessEmailActivity.class);
                startActivity(intent);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void saveScheduledEmailToFirebase(Map<String, String> map) {
        mScheduledEmailReference.push().setValue(map);
    }

    public void editScheduledEmailToFirebase(Map<String, String> map) {
        mScheduledEmailReference.push().setValue(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode, resultCode, i);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = i.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                String email = cursor.getString(numberColumnIndex);

                recipient.setText(email);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void send15minNotification(int hour, int minute){

        if (getHour-hour ==0 && minute-getMinute < 15){

        }
        else {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (minute < 15) {
                if (hour == 1) {
                    hour = 12;
                    minute = 60 - (15 - minute);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                } else {
                    hour = hour - 1;
                    minute = 60 - (15 - minute);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                }
            } else {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute - 15);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendsentNotification(int hour, int minute){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION2");
        notificationIntent.addCategory("android.intent.category.DEFAULT2");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    // Set the Selected Date in Select date Button
                    setTimer.setText(hour+":"+minute);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }

    public void sendMailHelper(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String person = recipient.getText().toString();
                    String emailMessage = emailText.getText().toString();
                    String subject = emailSubject.getText().toString();

                    sharedPreferences = getSharedPreferences(sp, 0);
                    email = sharedPreferences.getString("email","");
                    password = sharedPreferences.getString("password", "");


                    GMailSender sender = new GMailSender(email, password);
                    sender.sendMail(subject, emailMessage, email,
                            person);
                } catch (Exception e){
                    Log.e("FAIL_MAIL", e.getMessage(), e);
                }
            }
        }).start();
    }

    public void onSaveClickedSP(View view){
        sharedPreferences = getSharedPreferences(sp, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("receiver", recipient.getText().toString());
        editor.putString("time", hour+":"+minute);
        editor.apply();
    }

    public void storeJson(){
        JSONObject curr = new JSONObject();
        try {
            curr.put("recipient", recipient.getText().toString());
            curr.put("subject", emailSubject.getText().toString());
            curr.put("body", emailText.getText().toString());
            curr.put("hour", hour);
            curr.put("minute", minute);
        } catch (JSONException e){
            Log.e("Error", e.getMessage(), e);
        }
    }

    public void onSendSuccess(){
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSendFailure(){
        Toast.makeText(getBaseContext(), "Email failed to send", Toast.LENGTH_LONG).show();
    }

    public boolean validateEmail(){
        Boolean valid = true;

        String emailTo = recipient.getText().toString();
        String message = emailText.getText().toString();
        String subject = emailSubject.getText().toString();

        if (emailTo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailTo).matches()){
            recipient.setError("Enter a valid email address");
            valid = false;
        } else {
            recipient.setError(null);
        }

        if (message.isEmpty()){
            emailText.setError("Please type a message");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (subject.isEmpty()){
            emailSubject.setError("Please type a subject");
            valid = false;
        } else {
            emailSubject.setError(null);
        }

        if (setTimer.getText().toString().equals("Set Time")){
            setTimer.setError("Please select a time");
            valid = false;
        } else {
            setTimer.setError(null);
        }

        return valid;
    }

    public void setC(Calendar c) {
        this.c = c;
    }
}
