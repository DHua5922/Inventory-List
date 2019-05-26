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
    val COL_NAME = "Name"
    val COL_QUANTITY = "Quantity"
    val database : SQLiteDatabase = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($COL_NAME VARCHAR(100), " +
                "$COL_QUANTITY INTEGER);"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for items, so its upgrade policy is
        // to simply to discard the data and start over
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun addItem(item : Item) : Boolean {
        if(getItem(item.name).getCount() > 0) {
            throw Exception("Item already exists!")
        } else {
            val values : ContentValues = ContentValues().apply {
                put(COL_NAME, item.name)
                put(COL_QUANTITY, item.quantity)
            }
            return database.insert(TABLE_NAME, null, values) > 0
        }
    }

    fun deleteItem(name : String) : Boolean {
        return database.delete(TABLE_NAME, "$COL_NAME = '$name'", null) > 0
    }

    fun updateName(newText : String, oldText : String) : Boolean {

        if(newText.isEmpty())
            throw Exception("Name cannot be empty")
        else if (getItem(newText).count > 0)
            throw Exception("Item already exists")

        val newValues: ContentValues = ContentValues().apply {
            put(COL_NAME, newText)
        }

        return database.update(TABLE_NAME, newValues, "$COL_NAME = '$oldText'", null) > 0
    }

    fun updateQuantity(itemName : String, newNum : String, oldNum : String) : Boolean {
        try {
            val newValues = ContentValues()
            newValues.put(COL_QUANTITY, newNum.toInt())
            return database.update(
                TABLE_NAME,
                newValues,
                "$COL_NAME = '$itemName' AND $COL_QUANTITY = ${oldNum.toInt()}",
                null
            ) > 0
        } catch(e : Exception) {
            return false
        }
    }

    fun clear() {
        database.delete(TABLE_NAME, null, null)
    }

    fun getItem(name : String) : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = '$name'", null)
    }

    fun getAllItems() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun sortAlphabetically() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_NAME COLLATE NOCASE ASC",null)
    }

    fun sortNumerically() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_QUANTITY ASC",null)
    }
}

