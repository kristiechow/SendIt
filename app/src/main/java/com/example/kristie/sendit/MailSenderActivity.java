package com.example.kristie.sendit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.Bind;

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

    private String sContact;
    private String sSubject;
    private String sBody;

    private Calendar c;
    private AlarmManager aManager;
    private PendingIntent pIntent;

    private static final int REQUEST_CODE = 1;
    static final int TIME_DIALOG_ID=1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

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

                pIntent = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                aManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
                Toast.makeText(getApplicationContext(), "Sms scheduled! ",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

                    GMailSender sender = new GMailSender("nayib.asis@gmail.com", "lenaBem@n54a");
                    sender.sendMail(subject, emailMessage, "nayib.asis@gmail.com",
                            person);
                } catch (Exception e){
                    Log.e("FAIL_MAIL", e.getMessage(), e);
                }
            }
        }).start();
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

}
