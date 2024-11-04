package com.example.wakuwakuschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmAdapter(
    private val alarmList: List<AlarmItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.alarm_time_text)
        val messageTextView: TextView = itemView.findViewById(R.id.alarm_message_text)
        val deleteButton: Button = itemView.findViewById(R.id.delete_alarm_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmItem = alarmList[position]
        holder.timeTextView.text = alarmItem.time
        holder.messageTextView.text = alarmItem.message
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }
}
