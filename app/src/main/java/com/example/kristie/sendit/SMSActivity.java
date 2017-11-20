package com.example.kristie.sendit;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import org.json.JSONArray;

import java.security.acl.Permission;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SMSActivity extends AppCompatActivity {

    public String sPhone, sSms;
    private EditText etPhone, etSms;
    private String sp = "my_shared_preferences";

    private Button bStart, bCancel, bTimeSelect, bPhone;

    private int getHour, getMinute;

    static final int TIME_DIALOG_ID = 1;
    private static final int REQUEST_CODE = 1;

    Calendar c;
    public int year, month, day, hour, minute;
    private int mHour, mMinute;

    private AlarmManager aManager;
    private PendingIntent pIntent;
    public SharedPreferences sharedPreferences;
    public JSONArray emailArray;
    private Map<String, String> smsData = new HashMap<String, String>();
    public SMSActivity() {
        // Assign current Date and Time Values to Variables
        c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsactivity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");

                requestSmsSendPermission();

            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");

                requestSmsReceivePermission();

            }
        }

        c = Calendar.getInstance();
        getHour = c.get(Calendar.HOUR);
        getMinute = c.get(Calendar.MINUTE);

        etPhone = (EditText) findViewById(R.id.etPhone);
        etSms = (EditText) findViewById(R.id.etSms);

        bStart = (Button) findViewById(R.id.bStart);
        bCancel = (Button) findViewById(R.id.bCancel);
        bTimeSelect = (Button) findViewById(R.id.bTime);
        bPhone = (Button) findViewById(R.id.bCPhone);

        //contact
        bPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //start schedule
        bStart.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sPhone = etPhone.getText().toString();
                sSms = etSms.getText().toString();

                //if (sPhone.length() > 0 && sSms.length() > 0)
                //    sendSMS(sPhone, sSms);


                etSms.getText().clear();

                Intent i = new Intent(SMSActivity.this, AlarmService.class);
                i.putExtra("exPhone", sPhone);
                i.putExtra("exSmS", sSms);

                smsData.put("Phone", sPhone);
                smsData.put("Message", sSms);



                pIntent = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
                Toast.makeText(getApplicationContext(), "Sms scheduled! ", Toast.LENGTH_SHORT).show();


                onSaveClickedSP(v);
                send15minNotification(hour, minute);
                sendsentNotification(hour, minute);

                Intent intent = new Intent(SMSActivity.this, SucessSMSActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //set time to send
        bTimeSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(TIME_DIALOG_ID);
            }
        });

        //Cancel schedule
        bCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                aManager.cancel(pIntent);
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestSmsSendPermission() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }
    private void requestSmsReceivePermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    public void onSaveClickedSP(View view){
        sharedPreferences = getSharedPreferences(sp, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("receiver", sPhone.toString());
        editor.putString("time", hour+":"+minute);
        editor.apply();
    }

    //Choose phone in contact and set edit text
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, i);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = i.getData();
                String[] projection = {Phone.NUMBER, Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                etPhone.setText(number);
            }
        }
    }

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    // Set the Selected Date in Select date Button
                    bTimeSelect.setText(hour + ":" + minute);
                }
            };

    // Method automatically gets Called when you call showDialog()  method
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
        }
        return null;
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendSMS(String phoneNumber, String message) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS sent",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(getBaseContext(), "Generic failure",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(getBaseContext(), "No service",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(getBaseContext(), "Null PDU",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(getBaseContext(), "Radio off",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SENT));

                //---when the SMS has been delivered---
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS delivered",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "SMS not delivered",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            }
        }
}

