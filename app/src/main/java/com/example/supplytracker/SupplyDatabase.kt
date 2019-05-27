package com.example.supplytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import java.lang.NumberFormatException

/**
 * This supply manager accesses and manipulates the database that stores all the items.
 * This class gets the context of the page that displays the list of items.
 * Operations:
 *  - add item to database
 *  - delete item from database
 *  - update item with new name
 *  - update item with new amount
 *  - delete all items
 *  - get amount of specific item
 *  - get specific item
 *  - get all items
 *  - sort items in alphabetical order
 *  - sort items from lowest to highest amount
 */
class SupplyDatabase(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // initialize variables
    private val database : SQLiteDatabase = this.writableDatabase
    companion object {
        // If you change the database schema, you must increment the database version.
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "supplydatabase.db"
        private const val TABLE_NAME = "Items"
        const val COL_NAME = "Name"
        const val COL_QUANTITY = "Quantity"
    }

    /**
     * Creates a table for the given SQLite database.
     *
     * @param   db  SQLite database
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE $TABLE_NAME ($COL_NAME VARCHAR(100), $COL_QUANTITY INTEGER)")
    }

    /**
     * Deletes data from the SQLite database that stores all the items and starts over.
     *
     * @param   db          SQLite database
     * @param   oldVersion  old database version
     * @param   newVersion  new database database
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // database is only a cache for items, so it discards data and starts over
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Adds the given item to the database if the item is new;
     * otherwise, print the appropriate error that occurred during this operation.
     *
     * @param   item    given item
     * @return          true if item is added to database, or false
     */
    fun addItem(item : Item) : Boolean {
        val name = item.name.trim()
        val amount = item.amount

        if(name.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
        } else if(getItem(name).count > 0) {
            Toast.makeText(context, "$name already exists in this list!", LENGTH_SHORT).show()
        } else  {
            // prepare to add item to database
            val values: ContentValues = ContentValues().apply {
                put(COL_NAME, name)
                put(COL_QUANTITY, amount)
            }

            // try to add item to database
            if(database.insert(TABLE_NAME, null, values) > 0) {
                Toast.makeText(context, "$name(quantity: $amount) added to list", LENGTH_SHORT).show()
                return true
            } else {
                Toast.makeText(context, "$name(quantity: $amount) could not be added to list", LENGTH_SHORT).show()
            }
        }

        return false
    }

    /**
     * Deletes the given item from the database if the item is in the database;
     * otherwise, print the appropriate error that occurred during this operation.
     *
     * @param   itemName    name of item
     * @return              true if item is deleted from database, or false
     */
    fun deleteItem(itemName : String) : Boolean {
        val name = itemName.trim()

        // try to delete given item from database
        if (database.delete(TABLE_NAME, "$COL_NAME = '$name'", null) > 0)  {
            Toast.makeText(context, "Deleted $name from list", LENGTH_SHORT).show()
            return true
        } else  {
            Toast.makeText(context, "$name is not in this list", LENGTH_SHORT).show()
        }

        return false
    }

    /**
     * Updates the current name of the item with the given new name if the item is in the database;
     * otherwise, print the appropriate error that occurred during this operation.
     *
     * @param   newName     new name for item
     * @param   oldName     current name of item
     * @return              true if item has been updated with new name, or false
     */
    fun updateName(newName : String, oldName : String) : Boolean {
        val itemName = newName.trim()

        if(itemName.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
        } else if (getItem(itemName).count > 0) {
            Toast.makeText(context, "$itemName already exists in this list", LENGTH_SHORT).show()
        } else {
            // prepare to update item with new name
            val newValues: ContentValues = ContentValues().apply {
                put(COL_NAME, itemName)
            }

            // try to update item with new name
            if (database.update(TABLE_NAME, newValues, "$COL_NAME = '$oldName'", null) > 0) {
                Toast.makeText(context, "$oldName replaced with $itemName", LENGTH_SHORT).show()
                return true
            } else {
                Toast.makeText(context, "Could not replace $oldName with $itemName", LENGTH_SHORT).show()
            }
        }

        return false
    }

    /**
     * Updates the current amount of the item with the given new amount if the item is in the database;
     * otherwise, print the appropriate error that occurred during this operation. The name is needed
     * to search for the specific item in the database, since there can be duplicates of item amount.
     *
     * @param   itemName    name of item
     * @param   newAmount   new amount for item
     * @param   oldAmount   current amount of item
     * @return              true if item has been updated with new amount, or false
     */
    fun updateAmount(itemName : String, newAmount : String, oldAmount : String) : Boolean {
        // try to update item with new amount; otherwise, print error
        try {
            val name = itemName.trim()
            val newNum = newAmount.trim().toInt()
            val oldNum = oldAmount.trim().toInt()

            // prepare to update item with new amount
            val newValues = ContentValues().apply {
                put(COL_QUANTITY, newNum)
            }

            // try to update item with new amount
            if(database.update(TABLE_NAME, newValues,
                    "$COL_NAME = '$name' AND $COL_QUANTITY = $oldNum",
                    null) > 0) {
                Toast.makeText(context, "Quantity for $name changed from $oldNum to $newNum", LENGTH_SHORT).show()
                return true
            } else {
                Toast.makeText(context, "Could not change quantity for $name from $oldNum to $newNum", LENGTH_SHORT).show()
            }
        } catch(e : NumberFormatException) {
            Toast.makeText(context, "Quantity must only be whole numbers", LENGTH_SHORT).show()
        }

        return false
    }

    /**
     * Deletes all the items from the database.
     */
    fun clear() {
        database.delete(TABLE_NAME, null, null)
    }

    /**
     * Gets the amount of the given item.
     *
     * @param   name    name of item
     * @return          amount of given item
     */
    fun getAmount(name : String) : Int {
        val cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = '${name.trim()}'", null)
        var amount = -1

        // move database iterator to item and get item's amount
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            amount = cursor.getInt(1)
            cursor.close()
        }

        return amount
    }

    /**
     * Gets the given item in the database.
     *
     * @param   name    given name of item
     * @return          given item
     */
    private fun getItem(name : String) : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = '${name.trim()}'", null)
    }

    /**
     * Gets all the items in the database.
     *
     * @return          all items in database
     */
    fun getAllItems() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    /**
     * Sorts and gets all the items in alphabetical order in the database.
     *
     * @return          all items sorted in alphabetical order in database
     */
    fun sortAlphabetically() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_NAME COLLATE NOCASE ASC",null)
    }

    /**
     * Sorts and gets all the items from lowest to highest amount in the database.
     *
     * @return          all items sorted from lowest to highest amount in database
     */
    fun sortAmount() : Cursor {
        return database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_QUANTITY ASC",null)
    }
}

