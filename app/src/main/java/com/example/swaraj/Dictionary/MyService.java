package com.example.swaraj.Dictionary;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import static com.example.swaraj.Dictionary.DictionaryMainActivity.wordcombimelist;

public class MyService extends Service {
    NotificationCompat.Builder notification;
    public static final int uniqueid=848;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyService.this, "service started", Toast.LENGTH_SHORT).show();
                notification= new NotificationCompat.Builder(getApplicationContext());
                notification.setAutoCancel(true);
                notification.setColor(000);
                notification.setSmallIcon(R.drawable.a);
                SharedPreferences prf= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Toast.makeText(getApplicationContext(), prf.getString("example_list",""), Toast.LENGTH_SHORT).show();
                notification.setSound(Uri.parse(prf.getString("notifications_new_message_ringtone","xxx")));

                StringBuffer buffer=new StringBuffer();
                for(int j=1; j<= Integer.parseInt(prf.getString("example_list", ""));j++)
                {

                    buffer.append(wordcombimelist.get(new Random().nextInt(wordcombimelist.size()))+"   ");



                }
                notification.setContentText(buffer);
                notification.setContentTitle("Dictionary");

                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.notify(uniqueid,notification.build());
            }
        },3000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
