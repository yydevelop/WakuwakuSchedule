package com.example.wakuwakuschedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "WakuwakuSchedule"
    }

    private lateinit var messageTextView: TextView

    private val requestExactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (hasExactAlarmPermission()) {
            Log.d(TAG, "Permission granted, scheduling alarm")
            scheduleAlarm(30 * 1000L) // 30秒後
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // メッセージを表示するTextViewを取得
        messageTextView = findViewById(R.id.messageTextView)

        // アラームを設定
        checkAndScheduleAlarm()
    }

    private fun checkAndScheduleAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (hasExactAlarmPermission()) {
                Log.d(TAG, "Exact alarm permission available, scheduling alarm")
                scheduleAlarm(30 * 1000L)
            } else {
                Log.d(TAG, "Requesting exact alarm permission")
                requestExactAlarmPermission()
            }
        } else {
            Log.d(TAG, "Scheduling alarm without exact alarm permission")
            scheduleAlarm(30 * 1000L)
        }
    }

    private fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            requestExactAlarmPermissionLauncher.launch(intent)
        }
    }

    private fun scheduleAlarm(delayInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(TAG, "Setting exact alarm for ${delayInMillis / 1000} seconds later")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + delayInMillis,
            pendingIntent
        )
    }

    // アラーム受信時にメッセージを更新
    fun updateMessage(message: String) {
        messageTextView.text = message
    }
}
