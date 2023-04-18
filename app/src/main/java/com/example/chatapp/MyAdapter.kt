package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val listener: OnItemClickListener, private val userList: ArrayList<Users>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val itemTextView: TextView = view.findViewById(R.id.textViewUser)
        fun bindData(userName:String){
            itemTextView.text = userName
        }
        init {
            itemView.setOnClickListener {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.satirlar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(userList[position].UserName)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}
open interface OnItemClickListener {
    fun onItemClick(position: Int)
}