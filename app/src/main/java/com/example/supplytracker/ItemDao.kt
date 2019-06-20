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

    @Query("SELECT DISTINCT COUNT(column_listName) FROM table_item WHERE column_listName = :listName")
    fun getListNameCount(listName : String?) : Int

    // operations for sorting items by name
    @Query("SELECT * FROM table_item WHERE column_listName = :listName ORDER BY column_name ASC")
    fun sortNameAToZ(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName ORDER BY column_name DESC")
    fun sortNameZToA(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount = 0.0 ORDER BY column_name ASC")
    fun sortNameEmptyAToZ(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount = 0.0 ORDER BY column_name DESC")
    fun sortNameEmptyZToA(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0 ORDER BY column_name ASC")
    fun sortNameLeftoverAToZ(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0 ORDER BY column_name DESC")
    fun sortNameLeftoverZToA(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_isFull = 1 ORDER BY column_name ASC")
    fun sortNameFullAToZ(listName : String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_isFull = 1 ORDER BY column_name DESC")
    fun sortNameFullZToA(listName : String?) : List<Item>

    // operations for sorting items by amount
    @Query("SELECT * FROM table_item WHERE column_listName = :listName ORDER BY column_amount ASC")
    fun sortAmountAscending(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName ORDER BY column_amount DESC")
    fun sortAmountDescending(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount <= 0.0 ORDER BY column_amount ASC")
    fun sortAmountEmpty(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0 ORDER BY column_amount ASC")
    fun sortAmountLeftoverAscending(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0 ORDER BY column_amount DESC")
    fun sortAmountLeftoverDescending(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_isFull = 1 ORDER BY column_amount ASC")
    fun sortAmountFullAscending(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_isFull = 1 ORDER BY column_amount DESC")
    fun sortAmountFullDescending(listName: String?) : List<Item>

    // operations for deleting items
    @Delete
    fun delete(item : Item?) : Int

    @Query("DELETE FROM table_item WHERE column_listName = :listName AND column_amount <= 0")
    fun deleteEmpty(listName: String?) : Int

    @Query("DELETE FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0")
    fun deleteLeftover(listName: String?) : Int

    @Query("DELETE FROM table_item WHERE column_listName = :listName AND column_isFull = 1")
    fun deleteFull(listName: String?) : Int

    @Query("DELETE FROM table_item WHERE column_listName = :listName")
    fun deleteAllItems(listName: String?) : Int

    // operations for getting items
    @Query("SELECT * FROM table_item WHERE column_id = :id")
    fun getItem(id : Long?) : Item

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_name = :name ORDER BY column_order ASC")
    fun getItemByName(name: String?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_name LIKE '%' || :keyword || '%' ORDER BY column_order ASC")
    fun getItemsWithKeyword(keyword: String?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount = :amount ORDER BY column_order ASC")
    fun getItemsEqualTo(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount != :amount ORDER BY column_order ASC")
    fun getItemsNotEqualTo(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount < :amount ORDER BY column_order ASC")
    fun getItemsLessThan(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount <= :amount ORDER BY column_order ASC")
    fun getItemsLessThanOrEqualTo(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > :amount ORDER BY column_order ASC")
    fun getItemsGreaterThan(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount >= :amount ORDER BY column_order ASC")
    fun getItemsGreaterThanOrEqualTo(amount: Double?, listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount <= 0.0 ORDER BY column_order ASC")
    fun getEmptyItems(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_amount > 0.0 AND column_isFull = 0 ORDER BY column_order ASC")
    fun getLeftoverItems(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName AND column_isFull = 1 ORDER BY column_order ASC")
    fun getFullItems(listName: String?) : List<Item>

    @Query("SELECT * FROM table_item WHERE column_listName = :listName ORDER BY column_order ASC")
    fun getAllItems(listName: String?) : List<Item>

    @Query("SELECT DISTINCT column_listName FROM table_item")
    fun getAllSavedListNames() : List<String>
}