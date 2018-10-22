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
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

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

    /*
     * Tags
     */
    public static final String TAG = "NotificationScheduler";

    /*
     * Time variables
     */
    private static final int DAILY_REMINDER_REQUEST_CODE = 100;
    private static final int DELAY_VIBRATOR = 500;
    private static final int TIME_VIBRATOR_1 = 400;
    private static final int TIME_VIBRATOR_2 = 300;

    /*
     * Notification variables
     */
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "zerebrez_channel_notification";
    private static CharSequence name = "ZEREBREZ";
    private static NotificationChannel mChannel;

    /*
     * This method creates a PendingIntent with the AlarmManager according to selected time
     */
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

    /*
     * this method cancel the current PendingIntent of AlarmManager
     */
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

    /*
     * This method shows the notification
     */
    public static void showNotification(Context context) {

        // intent two vibrations with its time and delay
        Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v1 != null;
        v1.vibrate(TIME_VIBRATOR_1);
        try {
            Thread.sleep(DELAY_VIBRATOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vibrator v2 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v2 != null;
        v2.vibrate(TIME_VIBRATOR_2);

        // creates Uri of sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ContentActivity.class), 0);


        // build NotificationCompat
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.zerebrez_main_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.zerebrez_main_icon))
                        .setContentTitle("Zerebrez")
                        .setContentText("Es tiempo de estudiar")
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(defaultSoundUri)
                        .setContentIntent(contentIntent);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
        }
    }

}