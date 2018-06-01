package com.zerebrez.zerebrez.services.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationService : Service() {

    companion object {
        const val TAG : String = "NotificationService"
        const val NOTIFICATION_SERVICE_BR = "com.zerebrez.zerebrez.services.notification.NotificationService"
        const val IT_IS_TIME_TO_NOTIFY = "it_is_time_to_notify"
    }

    var bi = Intent(NotificationService.NOTIFICATION_SERVICE_BR)

    override fun onCreate() {
        super.onCreate()


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}