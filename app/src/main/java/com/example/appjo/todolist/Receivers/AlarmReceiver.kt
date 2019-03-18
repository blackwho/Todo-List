package com.example.appjo.todolist.Receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.example.appjo.todolist.MainActivity
import com.example.appjo.todolist.R

class AlarmReceiver: BroadcastReceiver() {
    private var title: String? = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.extras != null){
            title = intent.extras!!.getString("title")
        }
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingNotificationIntent = PendingIntent.getActivity(context, 0,
            notificationIntent, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val channel = NotificationChannel("default", "Daily Notification", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Daily Notification"
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel)
            }
        }
        val notificationBuilder = NotificationCompat.Builder(context, "default")
        notificationBuilder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Complete Task")
            .setContentText(title)
            .setContentIntent(pendingNotificationIntent)

        if (notificationManager != null){
            notificationManager.notify(1, notificationBuilder.build())
        }

    }
}