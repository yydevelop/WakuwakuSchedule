package com.example.wakuwakuschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmAdapter(private val alarmList: List<AlarmItem>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
        val messageTextView: TextView = itemView.findViewById(R.id.message_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmItem = alarmList[position]
        holder.timeTextView.text = alarmItem.time
        holder.messageTextView.text = alarmItem.message
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }
}
