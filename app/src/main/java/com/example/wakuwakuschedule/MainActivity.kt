package com.example.wakuwakuschedule

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "WakuwakuSchedule"
    }

    private lateinit var permissionStatusTextView: TextView
    private lateinit var requestPermissionButton: Button
    private lateinit var setAlarmButton: Button

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissionsStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionStatusTextView = findViewById(R.id.permission_status_text_view)
        requestPermissionButton = findViewById(R.id.request_permission_button)
        setAlarmButton = findViewById(R.id.set_alarm_button)

        checkPermissionsStatus()

        requestPermissionButton.setOnClickListener {
            requestMissingPermissions()
        }

        setAlarmButton.setOnClickListener {
            scheduleAlarm(30 * 1000L) // 30秒後にアラーム設定
        }
    }

    private fun checkPermissionsStatus() {
        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add("通知の権限")
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            permissionsNeeded.add("他のアプリの上に表示する権限")
        }

        if (permissionsNeeded.isEmpty()) {
            permissionStatusTextView.text = "すべての必要な権限が付与されています。"
        } else {
            permissionStatusTextView.text = "以下の権限が不足しています: ${permissionsNeeded.joinToString(", ")}"
        }
    }

    private fun requestMissingPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            requestPermissionsLauncher.launch(intent)
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
}
