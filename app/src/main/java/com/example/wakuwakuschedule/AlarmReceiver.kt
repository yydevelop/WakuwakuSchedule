package com.example.wakuwakuschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // メッセージを受け取る
        val message = intent.getStringExtra("alarm_message") ?: "No message"

        // NotificationActivityを起動し、メッセージを渡す
        val activityIntent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alarm_message", message)
        }
        context.startActivity(activityIntent)
    }
}
