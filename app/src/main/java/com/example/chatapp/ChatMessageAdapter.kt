package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.sqliteconnetion.MessageData
import java.util.*

class ChatMessageAdapter(val chatMessages: MutableList<MessageData>) : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
        view.layoutParams.height = 200
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.setItem(chatMessage)
    }

    override fun getItemCount():Int{
        return chatMessages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val you: TextView = itemView.findViewById(R.id.yourMessage)
        private val me: TextView = itemView.findViewById(R.id.myMessage)

        fun setItem(myItem: MessageData) {
            if(myItem.senderName == "you"){
                me.isVisible = false
                you.text = myItem.messageContent
            }
            else{
                me.text = myItem.messageContent
                you.isVisible = false
            }
        }
    }

}
