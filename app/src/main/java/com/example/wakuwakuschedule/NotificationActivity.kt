// NotificationActivity.kt
package com.example.wakuwakuschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val message = intent.getStringExtra("ALARM_MESSAGE") ?: "アラーム通知"
        val messageTextView = findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = message
    }
}
