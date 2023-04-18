package com.example.chatapp.sqliteconnetion

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConnectDBHelper(private val context: Context, private val TABLE_NAME:String):
    SQLiteOpenHelper(context,TABLE_NAME,null,1) {
    private val COL_ID = "id"
    private val COL_SENDERNAME = "sendername"
    private val COL_MESSAGECONTENT = "messagecontent"

    companion object{
        private val DATABASE_NAME = "ALLMESSAGES_DATABASE"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COL_SENDERNAME  VARCHAR(5),$COL_MESSAGECONTENT  VARCHAR(100))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertData(messageData: MessageData){
        val sqliteDB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_SENDERNAME , messageData.senderName)
        contentValues.put(COL_MESSAGECONTENT, messageData.messageContent)

        val result = sqliteDB.insert(TABLE_NAME,null,contentValues)
        if(result != -1L)
            println("Kayıt Başarılı")
        else
            println("Kayıt yapılamadı.")
    }

    @SuppressLint("Range")
    fun readData():MutableList<MessageData>{
        val messageDataList = mutableListOf<MessageData>()
        val sqliteDB = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = sqliteDB.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val messageData = MessageData()
                messageData.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                messageData.senderName = result.getString(result.getColumnIndex(COL_SENDERNAME))
                messageData.messageContent = result.getString(result.getColumnIndex(COL_MESSAGECONTENT))
                messageDataList.add(messageData)
            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return messageDataList
    }

    fun deleteAllData(){
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME,null,null)
        sqliteDB.close()
    }
}