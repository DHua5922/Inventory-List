package com.example.supplytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SupplyDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "supplydatabase.db"

        private val TABLE_NAME = "Items"
        private val COL_ID = "ID"
        private val COL_NAME = "Name"
        private val COL_QUANTITY = "Quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " VARCHAR(100) NOT NULL, " + COL_QUANTITY + " VARCHAR(100) NOT NULL);"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for items, so its upgrade policy is
        // to simply to discard the data and start over
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ");")
        onCreate(db!!)
    }

    fun addItem(item : Item) : Boolean {
        val db : SQLiteDatabase = this.writableDatabase

        var values : ContentValues = ContentValues().apply {
            put(COL_NAME, item.name)
            put(COL_QUANTITY, item.quantity)
        }
        var result : Long = db?.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1.toLong()
    }

    fun getItems() : Cursor {
        val db : SQLiteDatabase = this.writableDatabase
        val query : String = "SELECT * FROM " + TABLE_NAME
        val data : Cursor = db.rawQuery(query, null)
        return data
    }
}

