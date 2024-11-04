package com.example.wakuwakuschedule

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Intentからメッセージを取得
        val message = intent.getStringExtra("alarm_message") ?: "No message"

        // TextViewにメッセージを表示
        val messageTextView: TextView = findViewById(R.id.message_text_view)
        messageTextView.text = message
    }
}
