package com.example.chatapp

object UserList {
    lateinit var userList : ArrayList<Users>
    var position : Int = -1
    fun getUser():Users{
        return userList[position]
    }
}