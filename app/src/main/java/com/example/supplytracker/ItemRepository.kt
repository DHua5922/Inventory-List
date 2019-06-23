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

    fun getListNameCount(listName : String) : Int {
        return AsyncTaskGetListNameCount(itemDao).execute(listName).get()
    }

    /**
     * Updates the given list of items from the database.
     *
     * @param   item    item
     */
    fun update(items : List<Item>) : Int {
        return AsyncTaskUpdateList(itemDao).execute(items).get()
    }

    // function for sorting items by names
    /**
     * Sorts the items from the database A - Z.
     */
    fun sortNameAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database Z - A.
     */
    fun sortNameZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database A - Z with amount = 0.0.
     */
    fun sortNameEmptyAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameEmptyAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database Z - A with amount = 0.0.
     */
    fun sortNameEmptyZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameEmptyZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database A - Z that are not checked and with amount > 0.0.
     */
    fun sortNameLeftoverAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameLeftOverAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database Z - A that are not checked and with amount > 0.0.
     */
    fun sortNameLeftoverZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameLeftoverZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database A - Z that are checked.
     */
    fun sortNameFullAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameFullAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database Z - A that are checked.
     */
    fun sortNameFullZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameFullZToA(itemDao).execute(listName).get()
    }

    // function for sorting items by amount
    /**
     * Sorts the items from the database by lowest to highest amount.
     */
    fun sortAmountAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database by highest to lowest amount.
     */
    fun sortAmountDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountDescending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database with amount = 0.0 by lowest to highest amount.
     *
     * The order will not matter, since all the empty items have 0.0 as the
     * amount value to be considered empty.
     */
    fun sortAmountEmpty(listName: String) : List<Item> {
        return AsyncTaskSortAmountEmpty(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database that are not checked and with amount > 0.0 by
     * lowest to highest amount.
     */
    fun sortAmountLeftoverAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountLeftoverAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database that are not checked and with amount > 0.0 by
     * highest to lowest amount.
     */
    fun sortAmountLeftoverDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountLeftoverDescending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database that are checked by lowest to highest amount.
     */
    fun sortAmountFullAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountFullAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items from the database that are checked by highest to lowest amount.
     */
    fun sortAmountFullDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountFullDescending(itemDao).execute(listName).get()
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
    fun deleteEmpty(listName: String) : Int {
        return AsyncTaskDeleteEmpty(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the items from the database that are not checked and with amount > 0.0.
     */
    fun deleteLeftover(listName: String) : Int {
        return AsyncTaskDeleteLeftover(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the items from the database that are checked.
     */
    fun deleteFull(listName: String) : Int {
        return AsyncTaskDeleteFull(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the items from the database.
     */
    fun deleteAllItems(listName: String) : Int {
        return AsyncTaskDeleteAllItems(itemDao).execute(listName).get()
    }

    // function for getting items
    fun getItem(id: Long?) : Item {
        return AsyncTaskGetItem(itemDao).execute(id).get()
    }

    /**
     * Gets the item from the database that has the given name.
     *
     * @param   name        name
     * @return              item with the given name
     */
    fun getItemByName(name: String?, listName: String) : List<Item> {
        return AsyncTaskGetItemByName(itemDao).execute(name, listName).get()
    }

    /**
     * Gets all the items from the database that have the given keyword.
     *
     * @param   keyword     keyword
     * @return              all the items with the given keyword
     */
    fun getItemsWithKeyword(keyword: String?, listName: String) : List<Item> {
        return AsyncTaskGetItemsWithKeyword(itemDao).execute(keyword, listName).get()
    }

    /**
     * Gets all the items from the database that have the given amount.
     *
     * @param   amount      amount
     * @return              all the items with the given amount
     */
    fun getItemsEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsEqualTo(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database that do not have the given amount.
     *
     * @param   amount      amount
     * @return              all the items that do not have the given amount
     */
    fun getItemsNotEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsNotEqualTo(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database with amount < the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount < given amount
     */
    fun getItemsLessThan(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsLessThan(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database with amount <= the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount <= given amount
     */
    fun getItemsLessThanOrEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsLessThanOrEqualTo(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database with amount > the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount > given amount
     */
    fun getItemsGreaterThan(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsGreaterThan(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database with amount >= the given amount.
     *
     * @param   amount      amount
     * @return              all the items with amount >= given amount
     */
    fun getItemsGreaterThanOrEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsGreaterThanOrEqualTo(itemDao).execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items from the database with amount = 0.0.
     *
     * @return              all the items with amount = 0.0
     */
    fun getEmptyItems(listName: String) : List<Item> {
        return AsyncTaskGetEmptyItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the items from the database that are not checked and with amount > 0.0.
     *
     * @return              all the items that are not checked and with amount > 0.0
     */
    fun getLeftoverItems(listName: String) : List<Item> {
        return AsyncTaskGetLeftoverItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the items from the database that are checked.
     *
     * @return              all the items that are checked
     */
    fun getFullItems(listName: String) : List<Item> {
        return AsyncTaskGetFullItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the items from the database.
     *
     * @return              all the items
     */
    fun getAllItems(listName: String) : List<Item>  {
        return AsyncTaskGetAllItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the names of the saved lists from the database.
     *
     * @return              all the names of the saved lists from the database
     */
    fun getAllSavedListNames() : List<String> {
        return AsyncTaskGetAllSavedListNames(itemDao).execute().get()
    }

    fun getAllItemNames(listName : String?) : List<String> {
        return AsyncTaskGetAllItemNames(itemDao).execute(listName).get()
    }

    /**
     * The AsyncTask classes must be static so that they do not have a reference
     * to the repository itself; otherwise, a memory leak will occur.
     */
    companion object {

        private class SearchParams internal constructor(
            internal var amount: Double?,
            internal var listName: String
        )

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

        private class AsyncTaskGetListNameCount(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.getListNameCount(listName[0])
            }
        }

        // async tasks for sorting names
        /**
         * Sorts the items from the database A - Z in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameAToZ(listName[0])
            }
        }

        /**
         * Sorts the items from the database Z - A in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameZToA(listName[0])
            }
        }

        /**
         * Sorts the items from the database A - Z with amount = 0.0 in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameEmptyAToZ(listName[0])
            }
        }

        /**
         * Sorts the items from the database Z - A with amount = 0.0 in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameEmptyZToA(listName[0])
            }
        }

        /**
         * Sorts the items from the database A - Z that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftOverAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameLeftoverAToZ(listName[0])
            }
        }

        /**
         * Sorts the items from the database Z - A that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftoverZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameLeftoverZToA(listName[0])
            }
        }

        /**
         * Sorts the items from the database A - Z that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameFullAToZ(listName[0])
            }
        }

        /**
         * Sorts the items from the database Z - A that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameFullZToA(listName[0])
            }
        }

        // async task for sorting amount
        /**
         * Sorts the items from the database by lowest to highest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountAscending(listName[0])
            }
        }

        /**
         * Sorts the items from the database by highest to lowest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountDescending(listName[0])
            }
        }

        /**
         * Sorts the items from the database with amount = 0.0 by lowest to highest amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskSortAmountEmpty(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountEmpty(listName[0])
            }
        }

        /**
         * Sorts the items from the database that are not checked and with amount > 0.0 by lowest to highest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountLeftoverAscending(listName[0])
            }
        }

        /**
         * Sorts the items from the database that are not checked and with amount > 0.0 by highest to lowest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountLeftoverDescending(listName[0])
            }
        }

        /**
         * Sorts the items from the database that are checked by lowest to highest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountFullAscending(listName[0])
            }
        }

        /**
         * Sorts the items from the database that are checked by highest to lowest amount
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountFullDescending(listName[0])
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
        private class AsyncTaskDeleteEmpty(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteEmpty(listName[0])
            }
        }

        /**
         * Deletes all the items from the database that are not checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteLeftover(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteLeftover(listName[0])
            }
        }

        /**
         * Deletes all the items from the database that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteFull(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteFull(listName[0])
            }
        }

        /**
         * Deletes all the items from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskDeleteAllItems(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteAllItems(listName[0])
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
        private class AsyncTaskGetItemByName(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemByName(name[0], name[1])
            }
        }

        /**
         * Gets all the items from the database that has the given keyword in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsWithKeyword(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemsWithKeyword(name[0], name[1])
            }
        }

        /**
         * Gets all the items from the database that have the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database that do not have the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsNotEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsNotEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database with amount < the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThan(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsLessThan(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database with amount <= the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsLessThanOrEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database with amount > the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThan(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsGreaterThan(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database with amount >= the given amount in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsGreaterThanOrEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items from the database with amount = 0.0 in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetEmptyItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getEmptyItems(listName[0])
            }
        }

        /**
         * Gets all the items from the database that are checked and with amount > 0.0
         * in the background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetLeftoverItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getLeftoverItems(listName[0])
            }
        }

        /**
         * Gets all the items from the database that are checked in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetFullItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getFullItems(listName[0])
            }
        }

        /**
         * Gets all the items from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetAllItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getAllItems(listName[0])
            }
        }

        /**
         * Gets all the names of the saved lists from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetAllSavedListNames(private var itemDao: ItemDao) : AsyncTask<Void, Void, List<String>>() {
            override fun doInBackground(vararg params : Void?): List<String> {
                return itemDao.getAllSavedListNames()
            }
        }

        /**
         * Gets all the names of the items in the given list from the database in the background thread,
         * using the given Data Access Object.
         */
        private class AsyncTaskGetAllItemNames(private var itemDao: ItemDao) : AsyncTask<String, Void, List<String>>() {
            override fun doInBackground(vararg listName : String?): List<String> {
                return itemDao.getAllItemNames(listName[0])
            }
        }
    }
}