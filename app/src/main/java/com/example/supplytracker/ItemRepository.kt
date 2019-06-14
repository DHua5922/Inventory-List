package com.example.supplytracker

import android.app.Application
import android.os.AsyncTask

/**
 * This class represents a repository that abstracts access
 * to multiple data sources. This repository class gives a
 * clean API for data access to the rest of the application.
 * A Repository manages queries and allows multiple backends
 * to be used.
 */
class ItemRepository(application: Application) {
    private val database: ItemDatabase? = ItemDatabase.getDatabase(application)
    private var itemDao: ItemDao = database!!.itemDao()

    /**
     * Adds the given item to the database.
     *
     * @param   item    item
     */
    fun insert(item : Item) : Long {
        return AsyncTaskInsertItem(itemDao).execute(item).get()
    }

    /**
     * Updates the given item from the database.
     *
     * @param   item    item
     */
    fun update(item : Item) : Int {
        return AsyncTaskUpdateItem(itemDao).execute(item).get()
    }

    /**
     * Updates the given list of items from the database.
     *
     * @param   item    item
     */
    fun update(items : List<Item>) : Int {
        return AsyncTaskUpdateList(itemDao).execute(items).get()
    }


    /**
     * Updates the name of the given item from the database.
     *
     * @param   oldName    current item name
     * @param   newName    new item name
     */
    fun updateName(oldName : String, newName : String) : Int {
        return AsyncTaskUpdateName(itemDao).execute(oldName, newName).get()
    }

    // function for sorting items by names
    /**
     * Sorts the items from the database A - Z.
     */
    fun sortNameAToZ() : List<Item> {
        return AsyncTaskSortNameAToZ(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database Z - A.
     */
    fun sortNameZToA() : List<Item> {
        return AsyncTaskSortNameZToA(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database A - Z with amount = 0.0.
     */
    fun sortNameEmptyAToZ() : List<Item> {
        return AsyncTaskSortNameEmptyAToZ(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database Z - A with amount = 0.0.
     */
    fun sortNameEmptyZToA() : List<Item> {
        return AsyncTaskSortNameEmptyZToA(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database A - Z that are not checked and with amount > 0.0.
     */
    fun sortNameLeftoverAToZ() : List<Item> {
        return AsyncTaskSortNameLeftOverAToZ(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database Z - A that are not checked and with amount > 0.0.
     */
    fun sortNameLeftoverZToA() : List<Item> {
        return AsyncTaskSortNameLeftoverZToA(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database A - Z that are checked.
     */
    fun sortNameFullAToZ() : List<Item> {
        return AsyncTaskSortNameFullAToZ(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database Z - A that are checked.
     */
    fun sortNameFullZToA() : List<Item> {
        return AsyncTaskSortNameFullZToA(itemDao).execute().get()
    }

    // function for sorting items by amount
    /**
     * Sorts the items from the database by lowest to highest amount.
     */
    fun sortAmountAscending() : List<Item> {
       return AsyncTaskSortAmountAscending(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database by highest to lowest amount.
     */
    fun sortAmountDescending() : List<Item> {
        return AsyncTaskSortAmountDescending(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database with amount = 0.0 by lowest to highest amount.
     *
     * The order will not matter, since all the empty items have 0.0 as the
     * amount value to be considered empty.
     */
    fun sortAmountEmpty() : List<Item> {
        return AsyncTaskSortAmountEmpty(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database that are not checked and with amount > 0.0 by
     * lowest to highest amount.
     */
    fun sortAmountLeftoverAscending() : List<Item> {
        return AsyncTaskSortAmountLeftoverAscending(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database that are not checked and with amount > 0.0 by
     * highest to lowest amount.
     */
    fun sortAmountLeftoverDescending() : List<Item> {
        return AsyncTaskSortAmountLeftoverDescending(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database that are checked by lowest to highest amount.
     */
    fun sortAmountFullAscending() : List<Item> {
        return AsyncTaskSortAmountFullAscending(itemDao).execute().get()
    }

    /**
     * Sorts the items from the database that are checked by highest to lowest amount.
     */
    fun sortAmountFullDescending() : List<Item> {
        return AsyncTaskSortAmountFullDescending(itemDao).execute().get()
    }

    // function for deleting items
    /**
     * Deletes the given item from the database.
     *
     * @param   item    item
     */
    fun delete(item : Item) : Int {
        return AsyncTaskDeleteItem(itemDao).execute(item).get()
    }

    /**
     * Deletes all the items from the database with amount = 0.0.
     */
    fun deleteEmpty() : Int {
        return AsyncTaskDeleteEmpty(itemDao).execute().get()
    }

    /**
     * Deletes all the items from the database that are not checked and with amount > 0.0.
     */
    fun deleteLeftover() : Int {
        return AsyncTaskDeleteLeftover(itemDao).execute().get()
    }

    /**
     * Deletes all the items from the database that are checked.
     */
    fun deleteFull() : Int {
        return AsyncTaskDeleteFull(itemDao).execute().get()
    }

    /**
     * Deletes all the items from the database.
     */
    fun deleteAllItems() : Int {
        return AsyncTaskDeleteAllItems(itemDao).execute().get()
    }

    // function for getting items
    fun getItem(id: Long?) : Item {
        return AsyncTaskGetItem(itemDao).execute(id).get()
    }

    fun getItemCount(name: String?) : Int {
        return AsyncTaskGetItemCount(itemDao).execute(name).get()
    }

    /**
     * Gets the item from the database that has the given name.
     *
     * @param   name        name
     * @return              item with the given name
     */
    fun getItemByName(name: String?) : List<Item> {
        return AsyncTaskGetItemByName(itemDao).execute(name).get()
    }

    /**
     * Gets all the items from the database that have the given keyword.
     *
     * @param   keyword     keyword
     * @return              all the items with the given keyword
     */
    fun getItemsWithKeyword(keyword: String?) : List<Item> {
        return AsyncTaskGetItemsWithKeyword(itemDao).execute(keyword).get()
    }

    /**
     * Gets all the items from the database that have the given amount.
     *
     * @param   amount      amount
     * @return              all the items with the given amount
     */
    fun getItemsEqualTo(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsEqualTo(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database that do not have the given amount.
     *
     * @param   amount      amount
     * @return              all the items that do not have the given amount
     */
    fun getItemsNotEqualTo(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsNotEqualTo(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database with amount < the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount < given amount
     */
    fun getItemsLessThan(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsLessThan(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database with amount <= the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount <= given amount
     */
    fun getItemsLessThanOrEqualTo(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsLessThanOrEqualTo(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database with amount > the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount > given amount
     */
    fun getItemsGreaterThan(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsGreaterThan(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database with amount >= the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount >= given amount
     */
    fun getItemsGreaterThanOrEqualTo(amount: Double?) : List<Item> {
        return AsyncTaskGetItemsGreaterThanOrEqualTo(itemDao).execute(amount).get()
    }

    /**
     * Gets all the items from the database with amount = 0.0.
     *
     * @return              all the items with amount = 0.0
     */
    fun getEmptyItems() : List<Item> {
        return AsyncTaskGetEmptyItems(itemDao).execute().get()
    }

    /**
     * Gets all the items from the database that are not checked and with amount > 0.0.
     *
     * @return              all the items that are not checked and with amount > 0.0
     */
    fun getLeftoverItems() : List<Item> {
        return AsyncTaskGetLeftoverItems(itemDao).execute().get()
    }

    /**
     * Gets all the items from the database that are checked.
     *
     * @return              all the items that are checked
     */
    fun getFullItems() : List<Item> {
        return AsyncTaskGetFullItems(itemDao).execute().get()
    }

    /**
     * Gets all the items from the database.
     *
     * @return              all the items
     */
    fun getAllItems() : List<Item>  {
        return AsyncTaskGetAllItems(itemDao).execute().get()
    }

    /**
     * The AsyncTask classes must be static so that they do not have a reference
     * to the repository itself; otherwise, a memory leak will occur.
     */
    companion object {
        /**
         * Adds an item to the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskInsertItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Long>() {
            override fun doInBackground(vararg items : Item?): Long? {
                return itemDao.insert(items[0])
            }
        }

        /**
         * Updates an item from the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskUpdateItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.update(items[0])
            }
        }

        /**
         * Updates an item from the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskUpdateList(private var itemDao: ItemDao) : AsyncTask<List<Item>, Void, Int>() {
            override fun doInBackground(vararg items : List<Item>?): Int {
                return itemDao.update(items[0])
            }
        }


        /**
         * Updates the name of the given item from the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskUpdateName(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg name : String?): Int {
                return itemDao.updateName(name[0], name[1])
            }
        }

        // async tasks for sorting names
        /**
         * Sorts the items from the database A - Z in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameAToZ(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameAToZ()
            }
        }

        /**
         * Sorts the items from the database Z - A in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameZToA(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameZToA()
            }
        }

        /**
         * Sorts the items from the database A - Z with amount = 0.0 in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyAToZ(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameEmptyAToZ()
            }
        }

        /**
         * Sorts the items from the database Z - A with amount = 0.0 in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyZToA(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameEmptyZToA()
            }
        }

        /**
         * Sorts the items from the database A - Z that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftOverAToZ(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameLeftoverAToZ()
            }
        }

        /**
         * Sorts the items from the database Z - A that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftoverZToA(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameLeftoverZToA()
            }
        }

        /**
         * Sorts the items from the database A - Z that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullAToZ(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameFullAToZ()
            }
        }

        /**
         * Sorts the items from the database Z - A that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullZToA(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortNameFullZToA()
            }
        }

        // async task for sorting amount
        /**
         * Sorts the items from the database by lowest to highest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountAscending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountAscending()
            }
        }

        /**
         * Sorts the items from the database by highest to lowest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountDescending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountDescending()
            }
        }

        /**
         * Sorts the items from the database with amount = 0.0 by lowest to highest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountEmpty(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountEmpty()
            }
        }

        /**
         * Sorts the items from the database that are not checked and with amount > 0.0 by lowest to highest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverAscending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountLeftoverAscending()
            }
        }

        /**
         * Sorts the items from the database that are not checked and with amount > 0.0 by highest to lowest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverDescending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountLeftoverDescending()
            }
        }

        /**
         * Sorts the items from the database that are checked by lowest to highest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullAscending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountFullAscending()
            }
        }

        /**
         * Sorts the items from the database that are checked by highest to lowest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullDescending(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item> {
                return itemDao.sortAmountFullDescending()
            }
        }

        // async task for deleting items
        /**
         * Deletes the given item from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg item : Item?): Int {
                return itemDao.delete(item[0])
            }
        }

        /**
         * Deletes all the items from the database with amount = 0.0 in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteEmpty(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.deleteEmpty()
            }
        }

        /**
         * Deletes all the items from the database that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteLeftover(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.deleteLeftover()
            }
        }

        /**
         * Deletes all the items from the database that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteFull(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.deleteFull()
            }
        }

        /**
         * Deletes all the items from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteAllItems(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.deleteAllItems()
            }
        }

        // async tasks for getting items
        private class AsyncTaskGetItem(private var itemDao: ItemDao) : AsyncTask<Long, Void, Item>() {
            override fun doInBackground(vararg id : Long?): Item {
                return itemDao.getItem(id[0])
            }
        }

        /**
         * Gets the item from the database that has the given name in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemCount(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg name : String?): Int {
                return itemDao.getItemCount(name[0])
            }
        }

        /**
         * Gets the item from the database that has the given name in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemByName(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemByName(name[0])
            }
        }

        /**
         * Gets all the items from the database that has the given keyword in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsWithKeyword(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemsWithKeyword(name[0])
            }
        }

        /**
         * Gets all the items from the database that have the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsEqualTo(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsEqualTo(amount[0])
            }
        }

        /**
         * Gets all the items from the database that do not have the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsNotEqualTo(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsNotEqualTo(amount[0])
            }
        }

        /**
         * Gets all the items from the database with amount < the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThan(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsLessThan(amount[0])
            }
        }

        /**
         * Gets all the items from the database with amount <= the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsLessThanOrEqualTo(amount[0])
            }
        }

        /**
         * Gets all the items from the database with amount > the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThan(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsGreaterThan(amount[0])
            }
        }

        /**
         * Gets all the items from the database with amount >= the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<Double, Void, List<Item>>() {
            override fun doInBackground(vararg amount : Double?): List<Item>? {
                return itemDao.getItemsGreaterThanOrEqualTo(amount[0])
            }
        }

        /**
         * Gets all the items from the database with amount = 0.0 in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetEmptyItems(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item>? {
                return itemDao.getEmptyItems()
            }
        }

        /**
         * Gets all the items from the database that are checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetLeftoverItems(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item>? {
                return itemDao.getLeftoverItems()
            }
        }

        /**
         * Gets all the items from the database that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetFullItems(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item>? {
                return itemDao.getFullItems()
            }
        }

        /**
         * Gets all the items from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetAllItems(private var itemDao: ItemDao) : AsyncTask<Item, Void, List<Item>>() {
            override fun doInBackground(vararg items : Item?): List<Item>? {
                return itemDao.getAllItems()
            }
        }

    }
}