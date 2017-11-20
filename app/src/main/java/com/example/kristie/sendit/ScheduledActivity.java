package com.example.kristie.sendit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kristie on 11/19/17.
 */

public class ScheduledActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    public static final String FIREBASE_CHILD_SCHEDULED_EMAIL = "scheduledEmail";
    private ArrayList<String> mEmails = new ArrayList<>();
    private String email_id;
    private Firebase mRef;
    private DatabaseReference mReff;
    private FirebaseAuth mAuth;
    private ListView mListView;
    private DatabaseReference mScheduledEmailReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled);
        ButterKnife.bind(this);

        Firebase.setAndroidContext(this);
        mScheduledEmailReference = FirebaseDatabase.getInstance().getReference().child("scheduledEmails");
        mAuth = FirebaseAuth.getInstance();
        mListView = (ListView) findViewById(R.id.scheduled_list_view);
        mRef = new Firebase("https://sendit-2134c.firebaseio.com/");


        ListView lview = new ListView(this);


        Firebase.setAndroidContext(this);
        FirebaseListAdapter<EmailObject> Adapter = new FirebaseListAdapter<EmailObject>(this, EmailObject.class, android.R.layout.two_line_list_item, mScheduledEmailReference) {
            @Override
            protected void populateView(View v, EmailObject emailObject, int i) {
              ((TextView)v.findViewById(android.R.id.text1)).setText("To: " + emailObject.getsContact());
              ((TextView)v.findViewById(android.R.id.text2)).setText("Subject: " + emailObject.getsSubject());

            }
        };
        lview.setAdapter(Adapter);
        setContentView(lview);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}


