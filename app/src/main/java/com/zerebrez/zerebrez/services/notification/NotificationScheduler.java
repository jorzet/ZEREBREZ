/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.services.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.zerebrez.zerebrez.R;
import com.zerebrez.zerebrez.ui.activities.ContentActivity;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jorge Zepeda Tinoco on 01/06/18.
 * jorzet.94@gmail.com
 */

public class NotificationScheduler {
    public static final int DAILY_REMINDER_REQUEST_CODE = 100;
    public static final String TAG = "NotificationScheduler";
    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "zerebrez_channel_notification";

    public static void setReminder(Context context,Class<?> cls,int hour, int min) {

        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        cancelReminder(context,cls);

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    public static void cancelReminder(Context context,Class<?> cls) {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert am != null;
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context) {

        Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v1 != null;
        v1.vibrate(400);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vibrator v2 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v2 != null;
        v2.vibrate(300);


        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ContentActivity.class), 0);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set the info for the views that show in the notification panel.
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.main_icon)  // the status icon
                    .setTicker("vamos a estudiar")  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle("Zerebrez")  // the label of the entry
                    .setContentText("Es tiempo de estudiar")  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .setChannelId(CHANNEL_ID)
                    .build();
        } else {
            // Set the info for the views that show in the notification panel.
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.main_icon)  // the status icon
                    .setTicker("vamos a estudiar")  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle("Zerebrez")  // the label of the entry
                    .setContentText("Es tiempo de estudiar")  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();
        }

        // Send the notification.
        NotificationManager mNM = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNM != null) {
            CharSequence name = "Zerebrez";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNM.createNotificationChannel(mChannel);
        }

        assert mNM != null;
        mNM.notify(NOTIFICATION_ID, notification);

    }

}