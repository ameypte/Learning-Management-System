package com.example.staffdashboard

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"]
                val message = remoteMessage.data["message"]

                showNotification(title, message)
            }

        }
    }

    private fun showNotification(title: String?, message: String?) {
        // Create a notification channel
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val channelDescription = "This is the default notification channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        // Create a notification manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Create a notification intent
        val intent = Intent(this, StaffDashboard::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.profile)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "com.example.staffdashboard.MyMessagingService"
    }
}
