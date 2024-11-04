package com.example.wakuwakuschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WakuwakuSchedule", "Alarm received, displaying message")

        // メイン画面を開き、メッセージを表示
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("message", "アラームが鳴りました")
        }
        context.startActivity(mainIntent)
    }
}
