package com.example.supplytracker

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * This class represents a table of items in the SQLite database.
 * Each item has a name, amount, an indication of whether the item
 * is full or not, order number in the list, and the list that
 * this item belongs to. Each item property represents a column in
 * the database.
 */
@Entity(tableName = "table_item")
data class Item(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "column_id") var id: Long = 0,
    @ColumnInfo(name = "column_name") var name: String,
    @ColumnInfo(name = "column_amount") var amount: Double,
    @ColumnInfo(name = "column_isFull") var isFull: Int = 0,
    @ColumnInfo(name = "column_order") var order: Int,
    @ColumnInfo(name = "column_listName") var listName: String
)