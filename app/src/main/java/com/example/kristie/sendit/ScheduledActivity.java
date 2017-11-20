package com.example.kristie.sendit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kristie on 11/19/17.
 */

public class ScheduledActivity extends AppCompatActivity{

    private static final String TAG = "SignupActivity";
    public static final String FIREBASE_CHILD_SCHEDULED_EMAIL = "scheduledEmail";
    private ArrayList<String> mEmails = new ArrayList<>();
    private Firebase mRef;
    private FirebaseAuth mAuth;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled);
        ButterKnife.bind(this);

        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        mListView = (ListView) findViewById(R.id.scheduled_list_view);
        mRef = new Firebase("https://sendit-2134c.firebaseio.com/scheduledEmail");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mEmails);
 //       mListView.setAdapter(arrayAdapter);

        ValueEventListener valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {

                    String subject = (String) childSnapShot.child("subject").getValue();
                    String contact = (String) childSnapShot.child("contact").getValue();
                    String body = (String) childSnapShot.child("body").getValue();

                    mEmails.add(subject);
                    mEmails.add(contact);
                    mEmails.add(body);

                }
            }

            final FirebaseListAdapter<EmailObject> mAdapter = new FirebaseListAdapter<EmailObject>(this, EmailObject.class, R.layout.activity_scheduled, mRef) {
                @Override
                protected void populateView(View view, EmailObject myObj, int position) {
                    //Set the value for the views
                    String amount = sContact.getAmount();

                }
            };
            mListView.setAdapter(mAdapter);

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
