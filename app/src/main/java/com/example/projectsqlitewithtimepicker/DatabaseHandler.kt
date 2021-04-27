package com.example.projectsqlitewithtimepicker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ActivityDatabase"

        private val TABLE_ACTIVITY = "ActivityTable"
//
        private val KEY_ID = "_id"
        private val KEY_TIME = "time"
        private val KEY_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
//        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
//                + KEY_EMAIL + " TEXT" + ")")
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE ${TABLE_ACTIVITY}('${KEY_ID}' INTEGER PRIMARY KEY, ${KEY_TIME} TEXT, ${KEY_DESCRIPTION} TEXT)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITY")
        onCreate(db)
    }

    /**
     * Function to insert data
     */
    fun addActivity(act: MyActivityModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TIME, act.time)
        contentValues.put(KEY_DESCRIPTION, act.description)

        //inserting activity details using insert query.
        val success = db.insert(TABLE_ACTIVITY, null, contentValues)

        db.close()
        return success
    }
    /**
     * Function to read the records
     */
    fun viewActivity(): ArrayList<MyActivityModel> {
        val actList: ArrayList<MyActivityModel> = ArrayList<MyActivityModel>()

        val selectQuery = "SELECT * FROM ${TABLE_ACTIVITY}"

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var time: String
        var description: String

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))

                val act = MyActivityModel(id = id, time = time, description = description)
                actList.add(act)
            }while (cursor.moveToNext())
        }
        return actList
    }
    /**
     * Function to delete record
     */
    fun deleteActivity(act: MyActivityModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, act.id)

        val success = db.delete(TABLE_ACTIVITY, KEY_ID + "=" + act.id, null)

        db.close()
        return success
    }
    /**
     * Function to update record
     */
    fun updateActivity(act: MyActivityModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TIME, act.time)
        contentValues.put(KEY_DESCRIPTION, act.description)

        val success = db.update(TABLE_ACTIVITY, contentValues, KEY_ID + "=" + act.id, null)

        db.close()
        return success
    }
}