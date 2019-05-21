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
    }

    val TABLE_NAME = "Items"
    val COL_ID = "ID"
    val COL_NAME = "Name"
    val COL_QUANTITY = "Quantity"
    val database : SQLiteDatabase = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($COL_NAME VARCHAR(100), " +
                "$COL_QUANTITY INT);"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for items, so its upgrade policy is
        // to simply to discard the data and start over
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ");")
        onCreate(db!!)
    }

    fun addItem(item : Item) : Boolean {
        var values : ContentValues = ContentValues().apply {
            put(COL_NAME, item.name)
            put(COL_QUANTITY, item.quantity)
        }
        var result : Long = database.insert(TABLE_NAME, null, values)
        return result > 0
    }

    fun deleteItem(item : Item) : Boolean {
        return database.delete(TABLE_NAME, "$COL_NAME = \'" + item.name + "\' AND $COL_QUANTITY = " + item.quantity, null) > 0;
    }

    fun clear() {
        database.delete(TABLE_NAME, null, null)
    }

    fun getAllItems() : Cursor {
        return database.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun getItem(position : Int) : Cursor {
        return database.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun sortAlphabetically() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY " + COL_NAME + " COLLATE NOCASE ASC",null);
    }

    fun sortNumerically() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY " + COL_QUANTITY,null);
    }
}

