// AlarmReceiver.kt
package com.example.wakuwakuschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received")

        // NotificationActivity を起動するための Intent を作成
        val notificationIntent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("ALARM_MESSAGE", "アラームが鳴りました！")
        }

        // Activity を起動
        context.startActivity(notificationIntent)
    }
}
