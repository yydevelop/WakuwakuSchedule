package com.example.wakuwakuschedule

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "WakuwakuSchedule"
    }

    private lateinit var permissionStatusTextView: TextView
    private lateinit var requestPermissionButton: Button
    private lateinit var setAlarmButton: Button
    private lateinit var alarmRecyclerView: RecyclerView
    private val alarmList = mutableListOf<String>()
    private lateinit var alarmAdapter: AlarmAdapter

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
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view)

        checkPermissionsStatus()

        // RecyclerViewのセットアップ
        alarmAdapter = AlarmAdapter(alarmList)
        alarmRecyclerView.layoutManager = LinearLayoutManager(this)
        alarmRecyclerView.adapter = alarmAdapter

        requestPermissionButton.setOnClickListener {
            requestMissingPermissions()
        }

        setAlarmButton.setOnClickListener {
            showTimePickerDialog()
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

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val alarmTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            scheduleAlarm(selectedHour, selectedMinute)
            alarmList.add(alarmTime)
            alarmAdapter.notifyDataSetChanged()
        }, hour, minute, true).show()
    }

    private fun scheduleAlarm(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmList.size, // Unique request code for each alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(this, "アラームが設定されました: ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_SHORT).show()
    }
}
