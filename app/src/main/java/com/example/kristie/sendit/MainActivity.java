package com.example.kristie.sendit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Message;
import android.media.FaceDetector;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private String userEmail;

    private Button messageButton;
    private Button emailButton;
    private Button fbButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        messageButton = (Button) findViewById(R.id.messageButton);
        emailButton = (Button) findViewById(R.id.email_button);
        fbButton = (Button) findViewById(R.id.facebookButton);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, SMSActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MailSenderActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = openFacebook(MainActivity.this);
                startActivity(facebookIntent);

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            // No user is signed in
        }

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userEmail = firebaseUser.getEmail();
                }
            }
        };

    }

    public static Intent openFacebook(Context context){
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana",0);

            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
        } catch (Exception e) {

            return new Intent (Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));

        }
    }

    private void addDrawerItems() {

        String[] osArray = { "Welcome!" , "Organizer", "Contacts", "History", "Scheduled", "Settings" };
        int[] drawableIds = {R.drawable.user, R.drawable.menu, R.drawable.contact, R.drawable.history, R.drawable.scheduled, R.drawable.settings};

        CustomAdapter mAdapter = new CustomAdapter(this,  osArray, drawableIds);

        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == 0) {
                    //Intent ProfileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    //startActivity(ProfileIntent);
                } else if (id == 1) {
                    Intent MainIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(MainIntent);
                } else if (id == 2) {
                    //Intent SongsIntent = new Intent(MainActivity.this, activity2.class);
                    //startActivity(SongsIntent);
                } else if (id == 3) {
                    //Intent CardIntent = new Intent(MainActivity.this, activity3.class);
                    //startActivity(CardIntent);
                } else if (id == 4) {
                    Intent ScheduledIntent = new Intent(MainActivity.this, ScheduledActivity.class);
                    startActivity(ScheduledIntent);
                } else if (id == 5) {
                    Intent SettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(SettingsIntent);
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Send It");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("clicketh", Integer.toString(id));

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

}
