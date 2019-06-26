package com.dylanhua.inventorylist

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
    private val database: ItemDatabase? =
        ItemDatabase.getDatabase(application)
    private var itemDao: ItemDao = database!!.itemDao()

    /**
     * Adds the given item to the database.
     *
     * @param   item    item
     * @return          rowId of inserted item
     */
    fun insert(item : Item) : Long {
        return AsyncTaskInsertItem(itemDao).execute(item).get()
    }

    /**
     * Updates the given item in the database.
     *
     * @param   item    item
     * @return          number of rows updated
     */
    fun update(item : Item) : Int {
        return AsyncTaskUpdateItem(itemDao).execute(item).get()
    }

    /**
     * Updates the given list of items in the database.
     *
     * @param   itemList    item
     * @return              number of rows updated
     */
    fun update(itemList : List<Item>) : Int {
        return AsyncTaskUpdateList(itemDao).execute(itemList).get()
    }

    // function for sorting items by names
    /**
     * Sorts the items in the given list A - Z.
     *
     * @param   listName    given list
     * @return              list of items sorted A - Z
     */
    fun sortNameAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items in the given list Z - A.
     *
     * @param   listName    given list
     * @return              list of items sorted Z - A
     */
    fun sortNameZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the empty items in the given list A - Z.
     *
     * @param   listName    given list
     * @return              list of empty items sorted A - Z
     */
    fun sortNameEmptyAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameEmptyAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the empty items in the given list Z - A.
     *
     * @param   listName    given list
     * @return              list of empty items sorted Z - A
     */
    fun sortNameEmptyZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameEmptyZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the leftover items in the given list A - Z.
     *
     * @param   listName    given list
     * @return              list of leftover items sorted A - Z
     */
    fun sortNameLeftoverAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameLeftOverAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the leftover items in the given list Z - A.
     *
     * @param   listName    given list
     * @return              list of empty items sorted Z - A
     */
    fun sortNameLeftoverZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameLeftoverZToA(itemDao).execute(listName).get()
    }

    /**
     * Sorts the checked items in the given list A - Z.
     *
     * @param   listName    given list
     * @return              list of checked items sorted A - Z
     */
    fun sortNameFullAToZ(listName: String) : List<Item> {
        return AsyncTaskSortNameFullAToZ(itemDao).execute(listName).get()
    }

    /**
     * Sorts the checked items in the given list Z - A.
     *
     * @param   listName    given list
     * @return              list of checked items sorted Z - A
     */
    fun sortNameFullZToA(listName: String) : List<Item> {
        return AsyncTaskSortNameFullZToA(itemDao).execute(listName).get()
    }

    // function for sorting items by amount
    /**
     * Sorts the items in the given list by increasing amount.
     *
     * @param   listName    given list
     * @return              list of items sorted by increasing amount
     */
    fun sortAmountAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the items in the given list by decreasing amount.
     *
     * @param   listName    given list
     * @return              list of items sorted by decreasing amount
     */
    fun sortAmountDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountDescending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the empty items in the given list by increasing amount.
     *
     * @param   listName    given list
     * @return              list of empty items sorted by increasing amount
     */
    fun sortAmountEmpty(listName: String) : List<Item> {
        return AsyncTaskSortAmountEmpty(itemDao).execute(listName).get()
    }

    /**
     * Sorts the leftover items in the given list by increasing amount.
     *
     * @param   listName    given list
     * @return              list of leftover items sorted by increasing amount
     */
    fun sortAmountLeftoverAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountLeftoverAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the leftover items in the given list by decreasing amount.
     *
     * @param   listName    given list
     * @return              list of leftover items sorted by decreasing amount
     */
    fun sortAmountLeftoverDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountLeftoverDescending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the checked items in the given list by increasing amount.
     *
     * @param   listName    given list
     * @return              list of checked items sorted by increasing amount
     */
    fun sortAmountFullAscending(listName: String) : List<Item> {
        return AsyncTaskSortAmountFullAscending(itemDao).execute(listName).get()
    }

    /**
     * Sorts the checked items in the given list by decreasing amount.
     *
     * @param   listName    given list
     * @return              list of checked items sorted by decreasing amount
     */
    fun sortAmountFullDescending(listName: String) : List<Item> {
        return AsyncTaskSortAmountFullDescending(itemDao).execute(listName).get()
    }

    // function for deleting items
    /**
     * Deletes the given item from the database.
     *
     * @param   item    given item
     * @return          number of rows deleted
     */
    fun delete(item : Item) : Int {
        return AsyncTaskDeleteItem(itemDao).execute(item).get()
    }

    /**
     * Deletes all the empty items in the given list from the database.
     *
     * @param   listName    given list
     * @return              number of rows deleted
     */
    fun deleteEmpty(listName: String) : Int {
        return AsyncTaskDeleteEmpty(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the leftover items in the given list from the database.
     *
     * @param   listName    given list
     * @return              number of rows deleted
     */
    fun deleteLeftover(listName: String) : Int {
        return AsyncTaskDeleteLeftover(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the checked items in the given list from the database.
     *
     * @param   listName    given list
     * @return              number of rows deleted
     */
    fun deleteFull(listName: String) : Int {
        return AsyncTaskDeleteFull(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the items in the given list from the database.
     *
     * @param   listName    given list
     * @return              number of rows deleted
     */
    fun deleteAllItems(listName: String) : Int {
        return AsyncTaskDeleteAllItems(itemDao).execute(listName).get()
    }

    /**
     * Deletes all the items in the database.
     *
     * @return              number of rows deleted
     */
    fun deleteAllLists() : Int {
        return AsyncTaskDeleteAllLists(itemDao).execute().get()
    }

    // function for getting items
    /**
     * Gets all the items in the given list from the database with the given name.
     *
     * @param   name        given name
     * @param   listName    given list
     * @return              item in the given list with the given name
     */
    fun getItemByName(name: String?, listName: String) : List<Item> {
        return AsyncTaskGetItemByName(itemDao)
            .execute(name, listName).get()
    }

    /**
     * Gets all the items in the given list from the database with the given keyword.
     *
     * @param   keyword     given keyword
     * @param   listName    given list
     * @return              all the items in the given list with the given keyword
     */
    fun getItemsWithKeyword(keyword: String?, listName: String) : List<Item> {
        return AsyncTaskGetItemsWithKeyword(itemDao)
            .execute(keyword, listName).get()
    }

    /**
     * Gets all the items in the given list from the database with amount equal to the given amount.
     *
     * @param   amount      given amount
     * @param   listName    given list
     * @return              all the items in the given list with the given amount
     */
    fun getItemsEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsEqualTo(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items in the given list from the database that do not have the given amount.
     *
     * @param   amount      given amount
     * @param   listName    given list
     * @return              all the items in the given list that do not have the given amount
     */
    fun getItemsNotEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsNotEqualTo(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items in the given list from the database with amount < the given amount.
     *
     * @param   amount      amount
     * @param   listName    given list
     * @return              all the items in the given list with amount < given amount
     */
    fun getItemsLessThan(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsLessThan(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items in the given list from the database with amount <= the given amount.
     *
     * @param   amount      amount
     * @param   listName    given list
     * @return              all the items in the given list with amount <= given amount
     */
    fun getItemsLessThanOrEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsLessThanOrEqualTo(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items in the given list from the database with amount > the given amount.
     *
     * @param   amount      amount
     * @param   listName    given list
     * @return              all the items in the given list with amount > given amount
     */
    fun getItemsGreaterThan(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsGreaterThan(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the items in the given list from the database with amount >= the given amount.
     *
     * @param   amount      amount
     * @param   listName    given list
     * @return              all the items in the given list with amount >= given amount
     */
    fun getItemsGreaterThanOrEqualTo(amount: Double?, listName: String) : List<Item> {
        return AsyncTaskGetItemsGreaterThanOrEqualTo(itemDao)
            .execute(SearchParams(amount, listName)).get()
    }

    /**
     * Gets all the empty items in the given list from the database.
     *
     * @param   listName    given list
     * @return              all the empty items in the given list
     */
    fun getEmptyItems(listName: String) : List<Item> {
        return AsyncTaskGetEmptyItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the leftover items in the given list from the database.
     *
     * @param   listName    given list
     * @return              all the leftover items in the given list
     */
    fun getLeftoverItems(listName: String) : List<Item> {
        return AsyncTaskGetLeftoverItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the checked items in the given list from the database.
     *
     * @param   listName    given list
     * @return              all the checked items in the given list
     */
    fun getFullItems(listName: String) : List<Item> {
        return AsyncTaskGetFullItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the items in the given list from the database.
     *
     * @param   listName    given list
     * @return              all the items in the given list
     */
    fun getAllItems(listName: String) : List<Item>  {
        return AsyncTaskGetAllItems(itemDao).execute(listName).get()
    }

    /**
     * Gets all the names of the saved lists from the database.
     *
     * @return              all the names of the saved lists
     */
    fun getAllSavedListNames() : List<String> {
        return AsyncTaskGetAllSavedListNames(itemDao).execute().get()
    }

    /**
     * Gets all the item names in the given list from the database.
     *
     * @param   listName    given list
     * @return              all the item names in the given list
     */
    fun getAllItemNames(listName : String?) : List<String> {
        return AsyncTaskGetAllItemNames(itemDao).execute(listName).get()
    }

    /**
     * Gets the total number of the given list from the database.
     *
     * @param   listName    given list
     * @return              total number of the given list
     */
    fun getListNameCount(listName : String) : Int {
        return AsyncTaskGetListNameCount(itemDao).execute(listName).get()
    }

    /**
     * The AsyncTask classes must be static so that they do not have a reference
     * to the repository itself; otherwise, a memory leak will occur.
     */
    companion object {

        /**
         * This class groups item amount and list name into a single parameter type
         * for AsyncTask's doInBackground().
         */
        private class SearchParams internal constructor(
            internal var amount: Double?,
            internal var listName: String
        )

        /**
         * Adds a new item to the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskInsertItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Long>() {
            override fun doInBackground(vararg items : Item?): Long? {
                return itemDao.insert(items[0])
            }
        }

        /**
         * Updates the item from the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskUpdateItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg items : Item?): Int {
                return itemDao.update(items[0])
            }
        }

        /**
         * Updates the list of items from the database in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskUpdateList(private var itemDao: ItemDao) : AsyncTask<List<Item>, Void, Int>() {
            override fun doInBackground(vararg items : List<Item>?): Int {
                return itemDao.update(items[0])
            }
        }

        // async tasks for sorting items by names
        /**
         * Sorts the items in the given list from the database A - Z in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameAToZ(listName[0])
            }
        }

        /**
         * Sorts the items in the given list from the database Z - A in the background thread, using
         * the given Data Access Object.
         */
        private class AsyncTaskSortNameZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameZToA(listName[0])
            }
        }

        /**
         * Sorts the empty items in the given list from the database A - Z in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameEmptyAToZ(listName[0])
            }
        }

        /**
         * Sorts the empty items in the given list from the database Z - A in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameEmptyZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameEmptyZToA(listName[0])
            }
        }

        /**
         * Sorts the leftover items in the given list from the database A - Z in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftOverAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameLeftoverAToZ(listName[0])
            }
        }

        /**
         * Sorts the leftover items in the given list from the database Z - A in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameLeftoverZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameLeftoverZToA(listName[0])
            }
        }

        /**
         * Sorts the checked items in the given list from the database A - Z in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullAToZ(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameFullAToZ(listName[0])
            }
        }

        /**
         * Sorts the checked items in the given list from the database Z - A in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortNameFullZToA(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortNameFullZToA(listName[0])
            }
        }

        // async task for sorting amount
        /**
         * Sorts the items in the given list from the database by increasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountAscending(listName[0])
            }
        }

        /**
         * Sorts the items in the given list from the database by decreasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountDescending(listName[0])
            }
        }

        /**
         * Sorts the empty items in the given list from the database by increasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountEmpty(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountEmpty(listName[0])
            }
        }

        /**
         * Sorts the leftover items in the given list from the database by increasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountLeftoverAscending(listName[0])
            }
        }

        /**
         * Sorts the leftover items in the given list from the database by decreasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountLeftoverDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountLeftoverDescending(listName[0])
            }
        }

        /**
         * Sorts the checked items in the given list from the database by increasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullAscending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountFullAscending(listName[0])
            }
        }

        /**
         * Sorts the checked items in the given list from the database by decreasing amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskSortAmountFullDescending(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item> {
                return itemDao.sortAmountFullDescending(listName[0])
            }
        }

        // async task for deleting items
        /**
         * Deletes the given item from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteItem(private var itemDao: ItemDao) : AsyncTask<Item, Void, Int>() {
            override fun doInBackground(vararg item : Item?): Int {
                return itemDao.delete(item[0])
            }
        }

        /**
         * Deletes all the empty items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteEmpty(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteEmpty(listName[0])
            }
        }

        /**
         * Deletes all the leftover items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteLeftover(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteLeftover(listName[0])
            }
        }

        /**
         * Deletes all the checked items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteFull(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteFull(listName[0])
            }
        }

        /**
         * Deletes all the items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskDeleteAllItems(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.deleteAllItems(listName[0])
            }
        }

        /**
         * Deletes all the items in the database in the
         * background thread, using the given Data Access Object.
         *
         * @return              number of rows deleted
         */
        private class AsyncTaskDeleteAllLists(private var itemDao: ItemDao) : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params : Void?): Int {
                return itemDao.deleteAllLists()
            }
        }

        /**
         * Gets all the items in the given list from the database that has the given name in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemByName(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemByName(name[0], name[1])
            }
        }

        /**
         * Gets all the items in the given list from the database that has the given keyword in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsWithKeyword(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg name : String?): List<Item>? {
                return itemDao.getItemsWithKeyword(name[0], name[1])
            }
        }

        /**
         * Gets all the items in the given list from the database that have the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items in the given list from the database that do not have the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsNotEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsNotEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items in the given list from the database with amount < the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThan(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsLessThan(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items in the given list from the database with amount <= the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsLessThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsLessThanOrEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items in the given list from the database with amount > the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThan(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsGreaterThan(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the items in the given list from the database with amount >= the given amount in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetItemsGreaterThanOrEqualTo(private var itemDao: ItemDao) : AsyncTask<SearchParams, Void, List<Item>>() {
            override fun doInBackground(vararg searchParams : SearchParams?): List<Item>? {
                return itemDao.getItemsGreaterThanOrEqualTo(searchParams[0]?.amount, searchParams[0]?.listName)
            }
        }

        /**
         * Gets all the empty items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetEmptyItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getEmptyItems(listName[0])
            }
        }

        /**
         * Gets all the leftover items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetLeftoverItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getLeftoverItems(listName[0])
            }
        }

        /**
         * Gets all the checked items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetFullItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getFullItems(listName[0])
            }
        }

        /**
         * Gets all the items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetAllItems(private var itemDao: ItemDao) : AsyncTask<String, Void, List<Item>>() {
            override fun doInBackground(vararg listName : String?): List<Item>? {
                return itemDao.getAllItems(listName[0])
            }
        }

        /**
         * Gets all the names of the saved lists from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetAllSavedListNames(private var itemDao: ItemDao) : AsyncTask<Void, Void, List<String>>() {
            override fun doInBackground(vararg params : Void?): List<String> {
                return itemDao.getAllSavedListNames()
            }
        }

        /**
         * Gets all the names of the items in the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetAllItemNames(private var itemDao: ItemDao) : AsyncTask<String, Void, List<String>>() {
            override fun doInBackground(vararg listName : String?): List<String> {
                return itemDao.getAllItemNames(listName[0])
            }
        }

        /**
         * Gets the total number of the given list from the database in the
         * background thread, using the given Data Access Object.
         */
        private class AsyncTaskGetListNameCount(private var itemDao: ItemDao) : AsyncTask<String, Void, Int>() {
            override fun doInBackground(vararg listName : String?): Int {
                return itemDao.getListNameCount(listName[0])
            }
        }
    }
}