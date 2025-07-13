package com.yourname.goingdark

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val contentText = intent?.getStringExtra("contentText") ?: "Going Dark"

        val notification = NotificationCompat.Builder(this, "going_dark_channel")
            .setContentTitle("Going Dark")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_lock_notification)  // updated name here
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optionally add cleanup here if needed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

