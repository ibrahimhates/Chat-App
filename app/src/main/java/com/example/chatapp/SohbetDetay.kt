package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivitySohbetDetayBinding
import com.example.chatapp.sqliteconnetion.ConnectDBHelper
import com.example.chatapp.sqliteconnetion.MessageData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Semaphore

class SohbetDetay : AppCompatActivity() {

    private lateinit var binding: ActivitySohbetDetayBinding
    lateinit var count: CountDownTimer
    val db by lazy { ConnectDBHelper(this,UserList.getUser().UserName) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySohbetDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.text = UserList.getUser().UserName
        val chatMessages = readDataSqlite()
        println(chatMessages)
        //For FireBase Connection
        val database = Firebase.database
        val reference = database.getReference("Messages")
        var refMe = reference.child(MainActivity.admin.UserName+UserList.getUser().UserName)//me
        var refYou = reference.child(UserList.getUser().UserName+MainActivity.admin.UserName)//You

        //----------------------------------------


        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView2.layoutManager = layoutManager

        val adapter = ChatMessageAdapter(chatMessages)
        binding.recyclerView2.adapter = adapter



        count = object : CountDownTimer(60000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                getYouMessage(chatMessages,adapter,refYou)
            }
            override fun onFinish() {
            }
        }
        count.start()

        binding.deleteAllButton.setOnClickListener {
            count.cancel()
            deleteAllMessage()
            getBackPage()
        }
        binding.sendButton.setOnClickListener{
            val messageContent = binding.editTextTextPersonName4.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val messageData = MessageData(senderName = "me", messageContent = messageContent)
                chatMessages.add(messageData)
                println("gonderildi ve kayit edildi")
                insertDataSqlite(messageData)
                refMe.child("isRead").setValue("false")
                refMe.child("message").setValue(messageContent)
                adapter.notifyItemInserted(chatMessages.size - 1)
                binding.editTextTextPersonName4.text.clear()
                binding.recyclerView2.scrollToPosition(chatMessages.size - 1)
            }
        }

    }

    override fun onBackPressed() {
        count.cancel()
        getBackPage()
    }

    fun insertDataSqlite(messageData: MessageData){
        db.insertData(messageData)
    }

    fun readDataSqlite():MutableList<MessageData>{
        return db.readData()
    }

    fun deleteAllMessage(){
        db.deleteAllData()
    }

    fun getYouMessage(chatMessages:MutableList<MessageData>,adapter:ChatMessageAdapter,refYou: DatabaseReference){
        var database = FirebaseDatabase.getInstance()
        var ref = database.getReference("Messages/"+UserList.getUser().UserName+MainActivity.admin.UserName)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var you = snapshot.child("message").getValue(String::class.java)
                var isRead:Boolean = snapshot.child("isRead").getValue(String::class.java).toBoolean()
                if(!you.isNullOrEmpty() &&
                    MainActivity.sharedPrefs.getString("last","") != you){
                    val chatMessage = MessageData(senderName = "you", messageContent = you)
                    chatMessages.add(chatMessage)
                    adapter.notifyItemInserted(chatMessages.size-1)
                    binding.editTextTextPersonName4.text.clear()
                    binding.recyclerView2.scrollToPosition(chatMessages.size - 1)
                    MainActivity.sharedPrefs.edit().putString("last",you).apply()
                    println("Geldi ve kayit edildi => $you")
                    insertDataSqlite(chatMessage)
                    refYou.child("isRead").setValue("true")
                }

            }
            override fun onCancelled(error: DatabaseError) {
                println("Error or Data over")
            }
        })
    }

    fun getBackPage(){
        val view = Intent(applicationContext,AllUsers::class.java)
        startActivity(view)
    }
}