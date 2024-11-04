package com.example.wakuwakuschedule

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "WakuwakuSchedule"
        const val ALARM_LIST_KEY = "alarm_list_key"
    }

    private lateinit var permissionStatusTextView: TextView
    private lateinit var requestPermissionButton: Button
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarmButton: FloatingActionButton
    private val alarmList = mutableListOf<AlarmItem>()
    private lateinit var alarmAdapter: AlarmAdapter
    private val gson = Gson()

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
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view)
        addAlarmButton = findViewById(R.id.add_alarm_button)

        // 保存されたアラームリストを読み込む
        loadAlarmList()

        // RecyclerViewのセットアップ
        alarmAdapter = AlarmAdapter(alarmList)
        alarmRecyclerView.layoutManager = LinearLayoutManager(this)
        alarmRecyclerView.adapter = alarmAdapter

        requestPermissionButton.setOnClickListener {
            requestMissingPermissions()
        }

        addAlarmButton.setOnClickListener {
            showAddAlarmDialog()
        }
    }

    private fun checkPermissionsStatus() {
        if (!Settings.canDrawOverlays(this)) {
            permissionStatusTextView.text = "他のアプリの上に表示する権限が必要です"
        } else {
            permissionStatusTextView.text = "すべての必要な権限が付与されています"
        }
    }

    private fun requestMissingPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            requestPermissionsLauncher.launch(intent)
        } else {
            Toast.makeText(this, "すでに必要な権限が付与されています", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddAlarmDialog() {
        // ダイアログビューの設定
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_alarm, null)
        val messageEditText: EditText = dialogView.findViewById(R.id.message_edit_text)
        val timeButton: Button = dialogView.findViewById(R.id.time_button)

        var selectedHour = 0
        var selectedMinute = 0

        // 時刻選択ボタンの設定
        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, hourOfDay, minuteOfHour ->
                selectedHour = hourOfDay
                selectedMinute = minuteOfHour
                timeButton.text = String.format("%02d:%02d", hourOfDay, minuteOfHour)
            }, hour, minute, true).show()
        }

        // ダイアログの設定
        AlertDialog.Builder(this)
            .setTitle("新しいアラームを追加")
            .setView(dialogView)
            .setPositiveButton("追加") { _, _ ->
                val message = messageEditText.text.toString()
                if (message.isNotEmpty() && timeButton.text != "選択") {
                    val alarmTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    scheduleAlarm(selectedHour, selectedMinute)
                    alarmList.add(AlarmItem(alarmTime, message))
                    alarmAdapter.notifyDataSetChanged()
                    saveAlarmList() // アラームリストを保存
                } else {
                    Toast.makeText(this, "メッセージと時刻を入力してください", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    @SuppressLint("ScheduleExactAlarm")
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
            alarmList.size,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "アラームが設定されました: ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_SHORT).show()
    }

    private fun saveAlarmList() {
        val sharedPreferences = getSharedPreferences("wakuwaku_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = gson.toJson(alarmList)
        editor.putString(ALARM_LIST_KEY, json)
        editor.apply()
    }

    private fun loadAlarmList() {
        val sharedPreferences = getSharedPreferences("wakuwaku_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(ALARM_LIST_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<AlarmItem>>() {}.type
            val loadedList: MutableList<AlarmItem> = gson.fromJson(json, type)
            alarmList.clear()
            alarmList.addAll(loadedList)
        }
    }
}
