package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivityAllUsersBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AllUsers : AppCompatActivity() {
    private lateinit var binding: ActivityAllUsersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.text = MainActivity.admin.UserName
        UserList.userList = ArrayList()
        getAllUsers()
        quitStart()
    }
    override fun onBackPressed() { finishAffinity() }

    //Kullanici cikis yapmak isterse diye cikis islemleri
    fun quitStart(){
        binding.quitButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ÇIKIŞ")
            builder.setMessage("Cikis yapmak istediginden emin misin?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("EVET") { dialog, which ->
                quited()
            }
            builder.setNegativeButton("HAYIR") { dialog, which -> }

            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun quited(){
        MainActivity.sharedPrefs.edit()
            .remove("username")
            .remove("userid")
            .remove("password").apply()

        var view = Intent(applicationContext,MainActivity::class.java)
        startActivity(view)
    }
    //tiklanan kullanicinin posisyonun belirlenmesi icin olusturulmus method
    fun method(){
        var adapter = MyAdapter(object :OnItemClickListener{
            override fun onItemClick(position: Int) {
                UserList.position = position
                getDetailChatPage()
            }
        },UserList.userList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    //Tum kullanicilari veritabanindan ceken kod
    fun getAllUsers(){
        var database = FirebaseDatabase.getInstance()
        var ref = database.reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapShot in snapshot.children){
                    var name = userSnapShot.child("UserName").getValue(String::class.java).toString()
                    var id = userSnapShot.child("id").getValue(String::class.java).toString()
                    val user = Users(name,id)
                    if(user.UserId.equals(MainActivity.admin.UserId))
                        continue
                    UserList.userList.add(user)
                }
                method()
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error or Data over")
            }
        })
    }

    fun getDetailChatPage(){
        val database = Firebase.database
        val reference = database.getReference("Messages")
        reference.push().child(UserList.getUser().UserId+MainActivity.admin.UserId)

        var view = Intent(applicationContext,SohbetDetay::class.java)
        startActivity(view)
    }
}