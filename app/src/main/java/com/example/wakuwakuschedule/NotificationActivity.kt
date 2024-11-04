package com.example.wakuwakuschedule

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.widget.Button

class NotificationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var alarmMessageTextView: TextView
    private var alarmMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // インテントからメッセージを取得
        alarmMessage = intent.getStringExtra("alarm_message")

        alarmMessageTextView = findViewById(R.id.message_text_view)
        alarmMessageTextView.text = alarmMessage

        // TextToSpeechの初期化
        tts = TextToSpeech(this, this)

        // 閉じるボタンの設定
        val closeButton: Button = findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            finish() // Activityを閉じる
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 言語の設定
            tts.language = Locale.JAPANESE
            speakOut()
        }
    }

    private fun speakOut() {
        alarmMessage?.let {
            tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroy() {
        // TextToSpeechのリソース解放
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
