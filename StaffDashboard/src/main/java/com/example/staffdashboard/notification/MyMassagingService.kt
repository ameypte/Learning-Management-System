package com.example.staffdashboard

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.staffdashboard.notification.receivers.LectureNotificationReceiver
import com.example.staffdashboard.notification.receivers.PracticalNotificationReceiver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Calendar

class MyMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            if (type != null) {
                if (type.contains("Lec", true)) {
                    val subjectTeacher = remoteMessage.data["subject_teacher"]
                    val subjectCode = remoteMessage.data["subject_code"]
                    val subjectTitle = remoteMessage.data["subject_title"]
                    val year = remoteMessage.data["year"]
                    val day = remoteMessage.data["day"]
                    val startTime = remoteMessage.data["start_time"] // 12:00 AM, 10:50 AM, 11:40 PM
                    val endTime = remoteMessage.data["end_time"]
                    val branch = remoteMessage.data["branch"]
                    scheduleLectureNotification(
                        type,
                        subjectTeacher,
                        subjectCode,
                        year,
                        branch,
                        subjectTitle,
                        startTime,
                        endTime,
                        day
                    )
                } else if (type.contains("Pra", true)) {
                    val batch1 = remoteMessage.data["batch1"]
                    val batch2 = remoteMessage.data["batch2"]
                    val batch3 = remoteMessage.data["batch3"]
                    val subjectTeacher1 = remoteMessage.data["subject_teacher1"]
                    val subjectTeacher2 = remoteMessage.data["subject_teacher2"]
                    val subjectTeacher3 = remoteMessage.data["subject_teacher3"]
                    val subjectCode1 = remoteMessage.data["subject_code1"]
                    val subjectCode2 = remoteMessage.data["subject_code2"]
                    val subjectCode3 = remoteMessage.data["subject_code3"]
                    val subjectTitle1 = remoteMessage.data["subject_title1"]
                    val subjectTitle2 = remoteMessage.data["subject_title2"]
                    val subjectTitle3 = remoteMessage.data["subject_title3"]
                    val year = remoteMessage.data["year"]
                    val day = remoteMessage.data["day"]
                    val startTime = remoteMessage.data["start_time"] // 12:00 AM, 10:50 AM, 11:40 PM
                    val endTime = remoteMessage.data["end_time"]
                    val branch = remoteMessage.data["branch"]
                    schedulePracticalNotification(
                        type,
                        batch1,
                        batch2,
                        batch3,
                        subjectTeacher1,
                        subjectTeacher2,
                        subjectTeacher3,
                        subjectCode1,
                        subjectCode2,
                        subjectCode3,
                        subjectTitle1,
                        subjectTitle2,
                        subjectTitle3,
                        year,
                        branch,
                        startTime,
                        endTime,
                        day
                    )


                } else if (type.contains("Curriculum", true) || type.contains("Note",true)) {
                    val title = remoteMessage.data["title"]
                    val message = remoteMessage.data["body"]
                    showNotification(title,message)
                }
            }
        }
//        showNotification("Time Table", "Your Time Table has been updated")
    }

    @SuppressLint("SimpleDateFormat")
    private fun schedulePracticalNotification(
        type: String,
        batch1: String?,
        batch2: String?,
        batch3: String?,
        subjectTeacher1: String?,
        subjectTeacher2: String?,
        subjectTeacher3: String?,
        subjectCode1: String?,
        subjectCode2: String?,
        subjectCode3: String?,
        subjectTitle1: String?,
        subjectTitle2: String?,
        subjectTitle3: String?,
        year: String?,
        branch: String?,
        startTime: String?,
        endTime: String?,
        day: String?
    ) {
        createNotificationChannel()

        val sdf = SimpleDateFormat("hh:mm a")
        val calendar = Calendar.getInstance().apply {
            time = sdf.parse(startTime)
        }
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day))
        calendar.set(Calendar.SECOND, 0)

        // Set the notification to trigger on the specified day and time
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, PracticalNotificationReceiver::class.java)
        intent.putExtra("title", "Practical: $year ($branch)")
        intent.putExtra(
            "text",
            "$batch1: $subjectTitle1 by $subjectTeacher1\n$batch2: $subjectTitle2 by $subjectTeacher2\n$batch3: $subjectTitle3 by $subjectTeacher3\n$startTime - $endTime"
        )
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun scheduleLectureNotification(
        type: String?,
        subjectTeacher: String?,
        subjectCode: String?,
        year: String?,
        branch: String?,
        subjectTitle: String?,
        startTime: String?,
        endTime: String?,
        day: String?
    ) {
        createNotificationChannel()

        val sdf = SimpleDateFormat("hh:mm a")
        val calendar = Calendar.getInstance().apply {
            time = sdf.parse(startTime)
        }
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day))
        calendar.set(Calendar.SECOND, 0)

        // Set the notification to trigger on the specified day and time
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, LectureNotificationReceiver::class.java)
        intent.putExtra("title", "Lecture: $subjectTitle")
        intent.putExtra("text", "$year from $startTime to $endTime on $day")
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    private fun getDayOfWeek(day: String?): Int {
        return when (day) {
            "Monday" -> Calendar.MONDAY
            "Tuesday" -> Calendar.TUESDAY
            "Wednesday" -> Calendar.WEDNESDAY
            "Thursday" -> Calendar.THURSDAY
            "Friday" -> Calendar.FRIDAY
            "Saturday" -> Calendar.SATURDAY
            "Sunday" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }


    private fun createNotificationChannel() {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val channelDescription = "This is the default notification channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.profile)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(Color.RED)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object {
        private const val TAG = "com.example.staffdashboard.MyMessagingService"
    }
}
