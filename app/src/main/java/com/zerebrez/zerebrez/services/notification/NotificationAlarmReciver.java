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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zerebrez.zerebrez.services.database.DataHelper;
import com.zerebrez.zerebrez.ui.activities.ContentActivity;

/**
 * Created by Jorge Zepeda Tinoco on 01/06/18.
 * jorzet.94@gmail.com
 */

public class NotificationAlarmReciver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

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
