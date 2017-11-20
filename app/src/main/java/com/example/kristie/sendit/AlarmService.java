package com.example.kristie.sendit;

import android.*;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


public class AlarmService extends Service {


		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@RequiresApi(api = Build.VERSION_CODES.M)
        @Override
		public int onStartCommand(Intent i, int flags, int startId) {
			// TODO Auto-generated method stub

            Log.d("SHOOK", "YES1");
			String SPhone = i.getStringExtra("exPhone");
			String SSms = i.getStringExtra("exSmS");

            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.d("SHOOK", "YES1.5");

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(SPhone, null, SSms, null, null);
                Log.d("SHOOK", "YES2");
            }
            else {
                Toast.makeText(AlarmService.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
            }
			
			return START_STICKY;
		}
		
}
