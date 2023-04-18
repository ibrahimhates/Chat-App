package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.sqliteconnetion.ConnectDBHelper
import com.example.chatapp.sqliteconnetion.MessageData
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var cond:Boolean = false
    companion object{
        lateinit var admin: Admin
        lateinit var sharedPrefs: SharedPreferences
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //loading sembol ilk acilista calismasin diye
        binding.progressBar.visibility = View.INVISIBLE
        //kullaniciyi kaydetme islemi tekrar girise gerek kalmasin diye
        sharedPrefs = getSharedPreferences("UserSaved", Context.MODE_PRIVATE)

        //Sifreyi gorutuleyip gizlemek icin yapildi
        val transMethod = binding.userPassword.transformationMethod
        passwordShowOrHide(transMethod,binding)

        var userName = sharedPrefs.getString("username",null).toString()
        var userPassword =  sharedPrefs.getString("password",null).toString()
        var userId =  sharedPrefs.getString("userid",null).toString()

        if(!userName.isNullOrEmpty() &&
            !userPassword.isNullOrEmpty() && !userName.equals("null")){
            admin = Admin(userName,userId,userPassword)
            binding.progressBar.visibility = View.VISIBLE
            checkUserExits(userName,userPassword,true)
        }else{
            binding.loginButton.setOnClickListener{
                binding.progressBar.visibility = View.VISIBLE//loading simgesi icin
                var userName = binding.userName.text.toString().trim()
                var userPassword = binding.userPassword.text.toString().trim()
                if(userName.isNullOrEmpty()){
                    binding.userName.error = "Kullanici adi bos gecilemez!"
                    binding.progressBar.visibility = View.INVISIBLE
                }
                if(userPassword.isNullOrEmpty()){
                    binding.userPassword.error = "Sifre bos gecilemez"
                    binding.progressBar.visibility = View.INVISIBLE
                }
                else{
                    checkUserExits(userName,userPassword,false)
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    fun checkUserExits(userName:String,userPassword:String,isSavedUser:Boolean){
        if(cond){
            return;
        }

        var database = FirebaseDatabase.getInstance()
        var ref = database.reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                val userCount = snapshot.childrenCount.toInt()
                var isUserError = true
                for (userSnapShot in snapshot.children){
                    count++
                    println("Test $count $userCount")
                    var name = userSnapShot.child("UserName").getValue(String::class.java).toString()
                    var password = userSnapShot.child("Password").getValue(String::class.java).toString()
                    var id = userSnapShot.child("id").getValue(String::class.java).toString()
                    if(name.equals(userName)){
                        isUserError = false
                        println("Kullanici dogru")
                    }
                    if(name.equals(userName) && password.equals(userPassword)){
                        admin = Admin(name,id,password)
                        SavedUserAndNextPage(isSavedUser)
                        return
                    }else if(count == userCount && !isSavedUser){
                        println("Hatali giris Yapildi")
                        count = 0
                        HataliGiris(isUserError)
                    }
                }
                binding.progressBar.visibility = View.INVISIBLE
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error or Data over")
            }
        })
        cond = true
    }

    fun HataliGiris(isUserError: Boolean){
        cond = false
        if(isUserError){
            binding.userName.error = "Kullanici adi hatali"
        }else
            binding.userPassword.error = "Sifre Hatali!"
    }

    fun SavedUserAndNextPage(isSavedUser: Boolean){
        if(!isSavedUser){
            cond = false
            val edit = sharedPrefs.edit()
            edit.putString("username",admin.UserName)
            edit.putString("password",admin.UserPassword)
            edit.putString("userid", admin.UserId)
            edit.apply()
            Toast.makeText(applicationContext,"Giris Basarili", Toast.LENGTH_LONG).show()
        }
        getNextPage()
    }

    fun getNextPage(){
        var view = Intent(applicationContext,AllUsers::class.java)
        startActivity(view)
    }

    fun passwordShowOrHide(transMethod: TransformationMethod, binding: ActivityMainBinding){
        binding.passwordShow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.userPassword.transformationMethod = null
            } else {
                binding.userPassword.transformationMethod = transMethod
            }
        }
    }
}

/*
var database = FirebaseDatabase.getInstance()
var databaseReference = database.reference.child("Users")
var id = databaseReference.push()

id.child("id").setValue(id.key.toString())
id.child("UserName").setValue(userName)
id.child("Password").setValue(userPassword)

 */