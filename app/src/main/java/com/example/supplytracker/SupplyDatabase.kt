package com.example.supplytracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.SpannableStringBuilder
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import java.lang.NumberFormatException

/**
 * This supply manager accesses and manipulates the database that stores all the items.
 * Error messages are briefly shown in the page that displays the list of items, using
 * the context of that page.
 *
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

    private val database : SQLiteDatabase = this.writableDatabase
    companion object {
        // if database schema is changed, increment database version
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "supplydatabase.db"
        private const val TABLE_NAME = "Items"
        const val COL_NAME = "Name"
        const val COL_AMOUNT = "Amount"
        const val COL_ISFULL = "IsFull"
    }

    /**
     * Creates a table for the given SQLite database.
     *
     * @param   db  SQLite database
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE $TABLE_NAME ($COL_NAME VARCHAR(100), $COL_AMOUNT INTEGER, $COL_ISFULL INTEGER)")
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
        lateinit var styledText : SpannableStringBuilder

        if(name.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
        } else if(getItem(name).count > 0) {
            styledText = TextStyle.bold(name, "$name already exists in this list!")
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
        } else  {
            // prepare to add item to database
            val values: ContentValues = ContentValues().apply {
                put(COL_NAME, name)
                put(COL_AMOUNT, amount)
                put(COL_ISFULL, 0)
            }

            // try to add item to database
            if(database.insert(TABLE_NAME, null, values) > 0) {
                styledText = TextStyle.bold(arrayOf(name, "$amount"), "$name (amount: $amount) added to list")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
                return true
            // otherwise, show error message
            } else {
                styledText = TextStyle.bold(arrayOf(name, "$amount"), "$name (amount: $amount) could not be added to list")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
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
        lateinit var styledText : SpannableStringBuilder

        // if item is in database, delete item from database
        if (database.delete(TABLE_NAME, "$COL_NAME = '$name'", null) > 0)  {
            styledText = TextStyle.bold(name, "Deleted $name from list")
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
            return true
        // otherwise, show error message
        } else  {
            styledText = TextStyle.bold(name, "$name is not in this list")
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
        }

        return false
    }

    /**
     * Updates the given item with the given new indication if the item is full or not.
     *
     * @param   itemName    name of item
     * @param   isFull      new indication if item is full or not
     * @return              true if item has been updated with new indication, or false
     */
    fun updateCheckmark(itemName : String, isFull : Boolean) : Boolean {
        val name = itemName.trim()
        lateinit var styledText : SpannableStringBuilder

        if(name.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
        } else if (getItem(name).count < 1) {
            styledText = TextStyle.bold(name, "$name is not in this list")
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
        } else {
            // prepare to update item with indication
            val newValues: ContentValues = ContentValues().apply {
                if(isFull)
                    put(COL_ISFULL, 1)
                else
                    put(COL_ISFULL, 0)
            }

            // try to update item with indication
            if (database.update(TABLE_NAME, newValues, "$COL_NAME = '$name'", null) > 0) {
                styledText = TextStyle.bold(name, "Updated $name with new indication if it is full or not")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
                return true
            // otherwise, show error message
            } else {
                styledText = TextStyle.bold(name, "Could not update $name with new indication if it is full or not")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
            }
        }

        return false
    }

    /**
     * Updates the item with the given new name if the item is in the database;
     * otherwise, print the appropriate error that occurred during this operation.
     *
     * @param   newName     new name for item
     * @param   oldName     current name of item
     * @return              true if item has been updated with new name, or false
     */
    fun updateName(newName : String, oldName : String) : Boolean {
        val itemName = newName.trim()
        lateinit var styledText : SpannableStringBuilder

        if(itemName.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
        } else if (getItem(itemName).count > 0) {
            styledText = TextStyle.bold(itemName, "$itemName already exists in this list")
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
        } else {
            // prepare to update item with new name
            val newValues: ContentValues = ContentValues().apply {
                put(COL_NAME, itemName)
            }

            // try to update item with new name
            if (database.update(TABLE_NAME, newValues, "$COL_NAME = '$oldName'", null) > 0) {
                styledText = TextStyle.bold(arrayOf(oldName, itemName), "$oldName changed to $itemName")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
                return true
            // otherwise, show error message
            } else {
                styledText = TextStyle.bold(arrayOf(oldName, itemName), "Could not change $oldName to $itemName")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
            }
        }

        return false
    }

    /**
     * Updates the given item with the given new amount if the item is in the database; otherwise,
     * print the appropriate error that occurred during this operation. The name is needed to search
     * for the specific item in the database, since there can be duplicates of item amount. Throws an
     * exception if the given new amount is not an integer.
     *
     * @param       itemName    name of item
     * @param       newAmount   new amount for item
     * @param       oldAmount   current amount of item
     * @exception   Exception   if new amount is not integer
     * @return                  true if item has been updated with new amount, or false
     */
    fun updateAmount(itemName : String, newAmount : String, oldAmount : String) : Boolean {
        // try to update item with new amount; otherwise, show error message
        try {
            val name = itemName.trim()
            val newNum = newAmount.trim().toInt()
            val oldNum = oldAmount.trim().toInt()

            lateinit var styledText : SpannableStringBuilder
            if (getItem(name).count < 1) {
                styledText = TextStyle.bold(name, "$name is not in this list")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
            } else if (newNum == oldNum) {
                styledText = TextStyle.bold(name, "$name already has that amount")
                Toast.makeText(context, styledText, LENGTH_SHORT).show()
            } else {
                // prepare to update item with new amount
                val newValues = ContentValues().apply {
                    put(COL_AMOUNT, newNum)
                }

                // try to update item with new amount
                if(database.update(TABLE_NAME, newValues,
                        "$COL_NAME = '$name' AND $COL_AMOUNT = $oldNum",
                        null) > 0) {
                    styledText = TextStyle.bold(
                        arrayOf(name, "$oldNum", "$newNum"),
                        "Amount for $name changed from $oldNum to $newNum")
                    Toast.makeText(context, styledText, LENGTH_SHORT).show()
                    return true
                // otherwise, show error message
                } else {
                    styledText = TextStyle.bold(arrayOf(name, "$oldNum", "$newNum"), "Could not change amount for $name from $oldNum to $newNum")
                    Toast.makeText(context, styledText, LENGTH_SHORT).show()
                }
            }
        } catch(e : NumberFormatException) {
            Toast.makeText(context, "Amount must only be whole numbers", LENGTH_SHORT).show()
        }

        return false
    }

    /**
     * Gets the amount of the given item.
     *
     * @param   name    name of item
     * @return          amount of given item
     */
    fun getAmount(name : String) : Int {
        // initialize initial amount as error
        var amount = -1
        // get item in database
        val cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = '${name.trim()}'", null)

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
     * Deletes all or certain items from the database based on the chosen removal method.
     *
     * @param   removalMethod   chosen removal method
     * @return                  integer > 0 if removal method is successful, or 0
     */
    fun clear(removalMethod : MenuItem) : Int {
        var result = 0
        var message = "Invalid method for removing items"

        // different methods to remove items
        when (removalMethod.itemId) {
            R.id.option_remove_all -> {
                result = database.delete(TABLE_NAME, null, null)
                message = "Removed all items"
            }
            R.id.option_remove_empty -> {
                result = database.delete(TABLE_NAME, "$COL_AMOUNT <= 0", null)
                message = "Removed all empty items"
            }
            R.id.option_remove_leftover -> {
                result = database.delete(TABLE_NAME, "$COL_AMOUNT > 0 AND $COL_ISFULL = 0", null)
                message = "Removed all leftover items"
            }
            R.id.option_remove_checked -> {
                result = database.delete(TABLE_NAME, "$COL_ISFULL = 1", null)
                message = "Removed all full (checked) items"
            }
        }

        Toast.makeText(context, message, LENGTH_SHORT).show()
        return result
    }

    /**
     * Sorts all or certain items by their names based on chosen sort method.
     * Throws an exception if the chosen method for sorting names is invalid.
     *
     * @param       sortMethod  chosen sort method
     * @exception   Exception   if chosen method for sorting names is invalid
     * @return                  items in database sorted by names based on chosen sort method
     */
    fun sortNames(sortMethod : MenuItem) : Cursor {
        lateinit var cursor : Cursor
        var message = "Invalid method for sorting names"

        // different methods to sort items by names
        when (sortMethod.itemId) {
            R.id.option_sort_names_atoz -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_NAME COLLATE NOCASE ASC", null)
                message = "List sorted A - Z"
            }
            R.id.option_sort_names_ztoa -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_NAME COLLATE NOCASE DESC", null)
                message = "List sorted Z - A"
            }
            R.id.option_sort_names_empty_atoz -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT <= 0 ORDER BY $COL_NAME COLLATE NOCASE ASC",
                    null
                )
                message = "Empty items sorted A - Z"
            }
            R.id.option_sort_names_empty_ztoa -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT <= 0 ORDER BY $COL_NAME COLLATE NOCASE DESC",
                    null
                )
                message = "Empty items sorted Z - A"
            }
            R.id.option_sort_names_leftover_atoz -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT > 0 AND $COL_ISFULL = 0 ORDER BY $COL_NAME COLLATE NOCASE ASC",
                    null
                )
                message = "Leftover items sorted A - Z"
            }
            R.id.option_sort_names_leftover_ztoa -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT > 0 AND $COL_ISFULL = 0 ORDER BY $COL_NAME COLLATE NOCASE DESC",
                    null
                )
                message = "Leftover items sorted Z - A"
            }
            R.id.option_sort_names_checked_atoz -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_ISFULL = 1 ORDER BY $COL_NAME COLLATE NOCASE ASC",
                    null
                )
                message = "Full (checked) items sorted A - Z"
            }
            R.id.option_sort_names_checked_ztoa -> {
                cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE $COL_ISFULL = 1 ORDER BY $COL_NAME COLLATE NOCASE DESC",
                    null
                )
                message = "Full (checked) items sorted Z - A"
            }
            else -> {
                Toast.makeText(context, message, LENGTH_SHORT).show()
                throw Exception("Invalid method for sorting names")
            }
        }

        Toast.makeText(context, message, LENGTH_SHORT).show()
        return cursor
    }

    /**
     * Sorts all or certain items by their amount based on chosen sort method.
     * Throws an exception if the chosen method for sorting amount is invalid.
     *
     * @exception   Exception   if chosen method for sorting amount is invalid
     * @return                  items in database sorted by amount based on chosen sort method
     */
    fun sortAmount(sortMethod : MenuItem) : Cursor {
        lateinit var cursor : Cursor
        var message = "Invalid method for sorting amount"

        // different methods to sort items by amount
        when (sortMethod.itemId) {
            R.id.option_sort_amount_increase -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_AMOUNT ASC",null)
                message = "List sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_decrease -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_AMOUNT DESC",null)
                message = "List sorted from highest to lowest amount"
            }
            R.id.option_sort_amount_empty_increase -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT <= 0 ORDER BY $COL_AMOUNT ASC",null)
                message = "Empty items sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_empty_decrease -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT <= 0 ORDER BY $COL_AMOUNT DESC",null)
                message = "Empty items sorted from highest to lowest amount"
            }
            R.id.option_amount_leftover_increase -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT > 0 AND $COL_ISFULL = 0 ORDER BY $COL_AMOUNT ASC",null)
                message = "Leftover items sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_leftover_decrease -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_AMOUNT > 0 AND $COL_ISFULL = 0 ORDER BY $COL_AMOUNT DESC",null)
                message = "Leftover items sorted from highest to lowest amount"
            }
            R.id.option_sort_amount_full_increase -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ISFULL = 1 ORDER BY $COL_AMOUNT ASC",null)
                message = "Full (checked) items sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_full_decrease -> {
                cursor = database.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ISFULL = 1 ORDER BY $COL_AMOUNT DESC",null)
                message = "Full (checked) items sorted from highest to lowest amount"
            }
            else -> {
                Toast.makeText(context, message, LENGTH_SHORT).show()
                throw Exception("Invalid method for sorting amount")
            }
        }

        Toast.makeText(context, message, LENGTH_SHORT).show()
        return cursor
    }
}

