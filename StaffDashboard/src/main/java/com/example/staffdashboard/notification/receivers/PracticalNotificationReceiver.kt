package com.example.staffdashboard.notification.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.staffdashboard.R
import com.example.staffdashboard.StaffDashboard

class PracticalNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        val title = intent.getStringExtra("title") ?: return
        val text = intent.getStringExtra("text") ?: return

        createNotificationChannel(context)

        // Create the intent that will be triggered when the user clicks on the "Attend" button
        val attendIntent = Intent(context, StaffDashboard::class.java)
        val attendPendingIntent =
            PendingIntent.getService(
                context,
                0,
                attendIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        // Create the intent that will be triggered when the user clicks on the "Suspend" button
        val suspendIntent = Intent(context, StaffDashboard::class.java)
        val suspendPendingIntent =
            PendingIntent.getService(
                context,
                0,
                suspendIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val soundUrl = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.pikachu)

        val notification = NotificationCompat.Builder(context, "default_channel_id")
            .setSmallIcon(R.drawable.profile)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setSound(soundUrl)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.baseline_message_24, "Attend", attendPendingIntent)
            .addAction(R.drawable.baseline_close_24, "Suspend", suspendPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Default Channel"
        val descriptionText = "Default Channel for notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("default_channel_id", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}