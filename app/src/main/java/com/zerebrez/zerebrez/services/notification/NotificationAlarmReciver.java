package com.zerebrez.zerebrez.services.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zerebrez.zerebrez.services.database.DataHelper;
import com.zerebrez.zerebrez.ui.activities.ContentActivity;

public class NotificationAlarmReciver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                DataHelper dataHelper = new DataHelper(context);
                String time = dataHelper.getNotificationTime();
                String[] times = time.split(":");
                int hour = Integer.parseInt(times[0]);
                int minute = Integer.parseInt(times[1]);
                NotificationScheduler.setReminder(context, NotificationAlarmReciver.class,
                        hour, minute);
                return;
            }
        }
        //Trigger the notification
        NotificationScheduler.showNotification(context, ContentActivity.class,
                "You have 5 unwatched videos", "Watch them now?");

    }
}
