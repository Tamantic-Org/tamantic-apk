package com.dicoding.tamantic.data.reminder

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class WaterReminderWorker: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = intent?.getParcelableExtra<Notification>(NOTIFICATION)
        val notificationId = intent?.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(notificationId!!, notification)
    }

    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION = "notification"
    }
}