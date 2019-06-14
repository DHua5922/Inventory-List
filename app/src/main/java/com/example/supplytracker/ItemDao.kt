package com.example.supplytracker

import android.arch.persistence.room.*
import android.arch.persistence.room.Update

/**
 * This class represents a DAO (data access object) that validates
 * SQL at compile-time and associate it with a method, so I don't
 * have to worry about the SQL again. Room uses the DAO to create
 * a clean API for code.
 */
@Dao
interface ItemDao {
    @Insert
    fun insert(item: Item?) : Long

    @Update
    fun update(item : Item?) : Int

    @Update
    fun update(items: List<Item>?) : Int

    @Query("UPDATE table_item SET column_name = :newName WHERE column_name = :oldName")
    fun updateName(oldName : String?, newName : String?) : Int

    // operations for sorting items by name
    @Query("SELECT * FROM table_item ORDER BY column_name ASC")
    fun sortNameAToZ() : List<Item>

    @Query("SELECT * FROM table_item ORDER BY column_name DESC")
    fun sortNameZToA() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount = 0.0 ORDER BY column_name ASC")
    fun sortNameEmptyAToZ() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount = 0.0 ORDER BY column_name DESC")
    fun sortNameEmptyZToA() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0 ORDER BY column_name ASC")
    fun sortNameLeftoverAToZ() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0 ORDER BY column_name DESC")
    fun sortNameLeftoverZToA() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_isFull = 1 ORDER BY column_name ASC")
    fun sortNameFullAToZ() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_isFull = 1 ORDER BY column_name DESC")
    fun sortNameFullZToA() : List<Item>

    // operations for sorting items by amount
    @Query("SELECT * FROM table_item ORDER BY column_amount ASC")
    fun sortAmountAscending() : List<Item>

    @Query("SELECT * FROM table_item ORDER BY column_amount DESC")
    fun sortAmountDescending() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount <= 0.0 ORDER BY column_amount ASC")
    fun sortAmountEmpty() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0 ORDER BY column_amount ASC")
    fun sortAmountLeftoverAscending() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0 ORDER BY column_amount DESC")
    fun sortAmountLeftoverDescending() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_isFull = 1 ORDER BY column_amount ASC")
    fun sortAmountFullAscending() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_isFull = 1 ORDER BY column_amount DESC")
    fun sortAmountFullDescending() : List<Item>

    // operations for deleting items
    @Delete
    fun delete(item : Item?) : Int

    @Query("DELETE FROM table_item WHERE column_amount <= 0")
    fun deleteEmpty() : Int

    @Query("DELETE FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0")
    fun deleteLeftover() : Int

    @Query("DELETE FROM table_item WHERE column_isFull = 1")
    fun deleteFull() : Int

    @Query("DELETE FROM table_item")
    fun deleteAllItems() : Int

    // operations for getting items
    @Query("SELECT * FROM table_item WHERE column_id = :id")
    fun getItem(id : Long?) : Item

    @Query("SELECT COUNT(column_name) FROM table_item WHERE column_name = :name")
    fun getItemCount(name : String?) : Int

    @Query("SELECT * FROM table_item WHERE column_name = :name ORDER BY column_order ASC")
    fun getItemByName(name: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_name LIKE '%' || :keyword || '%' ORDER BY column_order ASC")
    fun getItemsWithKeyword(keyword: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount = :amount ORDER BY column_order ASC")
    fun getItemsEqualTo(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount != :amount ORDER BY column_order ASC")
    fun getItemsNotEqualTo(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount < :amount ORDER BY column_order ASC")
    fun getItemsLessThan(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount <= :amount ORDER BY column_order ASC")
    fun getItemsLessThanOrEqualTo(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > :amount ORDER BY column_order ASC")
    fun getItemsGreaterThan(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount >= :amount ORDER BY column_order ASC")
    fun getItemsGreaterThanOrEqualTo(amount: Double?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount <= 0.0 ORDER BY column_order ASC")
    fun getEmptyItems() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_amount > 0.0 AND column_isFull = 0 ORDER BY column_order ASC")
    fun getLeftoverItems() : List<Item>

    @Query("SELECT * FROM table_item WHERE column_isFull = 1 ORDER BY column_order ASC")
    fun getFullItems() : List<Item>

    @Query("SELECT * FROM table_item ORDER BY column_order ASC")
    fun getAllItems() : List<Item>
}