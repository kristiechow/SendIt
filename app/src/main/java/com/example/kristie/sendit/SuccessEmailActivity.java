package com.example.kristie.sendit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nayibasiselizalde on 11/19/17.
 */

public class SuccessEmailActivity extends Activity {
    //TODO: Edit and return buttons

    @Bind(R.id.success_message) TextView successMessage;
    @Bind(R.id.edit_email_button) Button editEmail;
    @Bind(R.id.returnto_email_button) Button returnTo;

    public SharedPreferences sharedPreferences;
    private String sp = "my_shared_preferences";
    private String name;
    private String time;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_email);
        ButterKnife.bind(this);

        setText();

        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessEmailActivity.this, ScheduledActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onLoadClickedSP(){
        sharedPreferences = getSharedPreferences(sp, 0);
        name = sharedPreferences.getString("receiver", "");
        time = sharedPreferences.getString("time", "");
    }

    public void setText(){
        onLoadClickedSP();
        successMessage.setText("Your email to " + name + " will be delivered at " + time);
    }
}
