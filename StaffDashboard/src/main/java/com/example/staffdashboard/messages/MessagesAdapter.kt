package com.example.staffdashboard.messages

import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.R

class MessagesAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.toString()
//        holder.timestampTextView.text = SimpleDateFormat("HH:mm").format(Date(message.timestamp))
    }


    override fun getItemCount(): Int {
        return messages.size
    }
}
