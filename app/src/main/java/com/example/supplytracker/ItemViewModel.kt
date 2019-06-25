package com.example.supplytracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.view.MenuItem

/**
 * This class represents a ViewModel that holds app's UI
 * data in a lifecycle-conscious way that survives
 * configuration changes. All the data needed for the UI
 * are held and processed in this ViewModel.
 */
class ItemViewModel(application : Application) : AndroidViewModel(application) {
    private val app: Application = application
    private val repository: ItemRepository = ItemRepository(app)

    /**
     * Adds the given item to the current list.
     *
     * @param   item      given item
     * @return            rowId of inserted item
     */
    fun add(item : Item) : Long {
        val name = item.name

        when {
            name.isEmpty() -> Utility.printStyledMessage(app, "Name cannot be empty")
            else -> {
                val result = repository.insert(item)
                if(result > 0) {
                    val amount = item.amount
                    Utility.printStyledMessage(app, "$name (amount: $amount) added to list", arrayOf(name, "$amount"))
                    return result
                }
            }
        }

        return -1
    }

    /**
     * Adds the given items to the given list.
     *
     * @param   items       given list of items
     * @param   listName    given list
     */
    fun add(items : List<Item>, listName : String) {
        for((i, item) in items.withIndex()) {
            val newItem = Item(
                name = item.name,
                amount = item.amount,
                isFull = item.isFull,
                order = i,
                listName = listName
            )

            newItem.id = repository.insert(newItem)
            repository.update(newItem)
        }
    }

    /**
     * Updates the given item in the database.
     *
     * @param   item    given item to update
     * @return          true if update was successful, or false
     */
    fun update(item : Item) : Boolean {
        return repository.update(item) > 0
    }

    /**
     * Updates the given items in the database.
     *
     * @param   items   given list of items to update
     * @return          true if update was successful, or false
     */
    fun update(items : List<Item>) : Boolean {
        return repository.update(items) > 0
    }

    /**
     * Sorts all or certain items in the given list by their names or amount based on chosen sort method.
     * Throws an exception if the chosen method for sorting is invalid.
     *
     * @param       sortMethod  chosen sort method
     * @param       listName    given list
     * @exception   Exception   if sort method is invalid
     * @return                  list of items sorted based on chosen sort method
     */
    fun sort(sortMethod : MenuItem, listName: String) : List<Item> {
        lateinit var itemList : List<Item>

        // sort items based on chosen method
        when (sortMethod.itemId) {
            // sort by names
            R.id.option_sort_names_atoz -> {
                itemList = repository.sortNameAToZ(listName)
                Utility.printStyledMessage(app, "List sorted A - Z")
            }
            R.id.option_sort_names_ztoa -> {
                itemList = repository.sortNameZToA(listName)
                Utility.printStyledMessage(app, "List sorted Z - A")
            }
            R.id.option_sort_names_empty_atoz -> {
                itemList = repository.sortNameEmptyAToZ(listName)
                Utility.printStyledMessage(app, "Empty items sorted A - Z")
            }
            R.id.option_sort_names_empty_ztoa -> {
                itemList = repository.sortNameEmptyZToA(listName)
                Utility.printStyledMessage(app, "Empty items sorted Z - A")
            }
            R.id.option_sort_names_leftover_atoz -> {
                itemList = repository.sortNameLeftoverAToZ(listName)
                Utility.printStyledMessage(app, "Leftover items sorted A - Z")
            }
            R.id.option_sort_names_leftover_ztoa -> {
                itemList = repository.sortNameLeftoverZToA(listName)
                Utility.printStyledMessage(app, "Leftover items sorted Z - A")
            }
            R.id.option_sort_names_checked_atoz -> {
                itemList = repository.sortNameFullAToZ(listName)
                Utility.printStyledMessage(app, "Full (checked) items sorted A - Z")
            }
            R.id.option_sort_names_checked_ztoa -> {
                itemList = repository.sortNameFullZToA(listName)
                Utility.printStyledMessage(app, "Full (checked) items sorted Z - A")
            }
            // sort by amount
            R.id.option_sort_amount_increase -> {
                itemList = repository.sortAmountAscending(listName)
                Utility.printStyledMessage(app, "List sorted from lowest to highest amount")
            }
            R.id.option_sort_amount_decrease -> {
                itemList = repository.sortAmountDescending(listName)
                Utility.printStyledMessage(app, "List sorted from highest to lowest amount")
            }
            R.id.option_sort_amount_empty -> {
                itemList = repository.sortAmountEmpty(listName)
                Utility.printStyledMessage(app, "Empty items sorted")
            }
            R.id.option_amount_leftover_increase -> {
                itemList = repository.sortAmountLeftoverAscending(listName)
                Utility.printStyledMessage(app, "Leftover items sorted from lowest to highest amount")
            }
            R.id.option_sort_amount_leftover_decrease -> {
                itemList = repository.sortAmountLeftoverDescending(listName)
                Utility.printStyledMessage(app, "Leftover items sorted from highest to lowest amount")
            }
            R.id.option_sort_amount_full_increase -> {
                itemList = repository.sortAmountFullAscending(listName)
                Utility.printStyledMessage(app, "Full (checked) items sorted from lowest to highest amount")
            }
            R.id.option_sort_amount_full_decrease -> {
                itemList = repository.sortAmountFullDescending(listName)
                Utility.printStyledMessage(app, "Full (checked) items sorted from highest to lowest amount")
            }
            // invalid sort method
            else -> {
                val message = "Invalid method for sorting"
                Utility.printStyledMessage(app, message)
                throw Exception(message)
            }
        }

        return itemList
    }

    /**
     * Deletes all or certain items from the given list based on the chosen removal method.
     * Throws an exception if the chosen method for removing is invalid.
     *
     * @param       removalMethod   chosen removal method
     * @param       listName        given list
     * @exception   Exception       if remove method is invalid
     * @return                      number of rows deleted
     */
    fun delete(removalMethod : MenuItem, listName : String) : Int {
        val result: Int

        // remove items based on chosen method
        when (removalMethod.itemId) {
            R.id.option_delete_list_all -> {
                result = repository.deleteAllLists()
                Utility.printStyledMessage(app, "Removed all lists")
            }
            R.id.option_remove_all -> {
                result = delete(listName)
                Utility.printStyledMessage(app, "Removed all items")
            }
            R.id.option_remove_empty -> {
                result = repository.deleteEmpty(listName)
                Utility.printStyledMessage(app, "Removed all empty items")
            }
            R.id.option_remove_leftover -> {
                result = repository.deleteLeftover(listName)
                Utility.printStyledMessage(app, "Removed all leftover items")
            }
            R.id.option_remove_checked -> {
                result = repository.deleteFull(listName)
                Utility.printStyledMessage(app, "Removed all full (checked) items")
            }
            else -> {
                val message = "Invalid method for removing items"
                Utility.printStyledMessage(app, message)
                throw Exception(message)
            }
        }

        return result
    }

    /**
     * Deletes the given item from the database.
     *
     * @param   item    given item
     * @return          number of rows deleted
     */
    fun delete(item : Item) : Int {
        val name = item.name
        val result = repository.delete(item)

        val message = if(result > 0) {
            "$name has been deleted"
        } else {
            "$name could not be deleted"
        }
        Utility.printStyledMessage(app, message, arrayOf(name))
        return result
    }

    /**
     * Deletes the given list from the database.
     *
     * @param   listName    given list
     * @return              number of rows deleted
     */
    fun delete(listName : String) : Int {
        return repository.deleteAllItems(listName)
    }

    /**
     * Searches all or certain items in the given list based on the chosen search method.
     * Throws an exception if the chosen method for searching is invalid.
     *
     * @param       searchMethod    chosen search method
     * @param       listName        given list
     * @param       word            name or keyword to search item for
     * @param       amount          exact amount to search items for
     * @param       comparison      comparison for searching items with more than exact amount
     * @exception   Exception       if search method is invalid
     * @return                      chosen items to search for
     */
    fun search(searchMethod : MenuItem, listName: String, word : String = "", amount : Double = -1.0, comparison : Int = -1) : List<Item> {
        lateinit var itemList : List<Item>

        // search items based on chosen method
        when (searchMethod.itemId) {
            R.id.option_search_name -> {
                itemList = repository.getItemByName(word, listName)
            }
            R.id.option_search_amount -> {
                when (comparison) {
                    0 -> itemList = repository.getItemsEqualTo(amount, listName)
                    1 -> itemList = repository.getItemsNotEqualTo(amount, listName)
                    2 -> itemList = repository.getItemsLessThan(amount, listName)
                    3 -> itemList = repository.getItemsLessThanOrEqualTo(amount, listName)
                    4 -> itemList = repository.getItemsGreaterThan(amount, listName)
                    5 -> itemList = repository.getItemsGreaterThanOrEqualTo(amount, listName)
                }
            }
            R.id.option_search_keyword -> {
                itemList = repository.getItemsWithKeyword(word, listName)
            }
            R.id.option_search_empty -> {
                itemList = repository.getEmptyItems(listName)
            }
            R.id.option_search_leftover -> {
                itemList = repository.getLeftoverItems(listName)
            }
            R.id.option_search_full -> {
                itemList = repository.getFullItems(listName)
            }
            R.id.option_search_all -> {
                itemList = repository.getAllItems(listName)
            }
            else -> {
                val message = "Invalid method for searching items"
                Utility.printStyledMessage(app, message)
                throw Exception(message)
            }
        }

        return itemList
    }

    /**
     * Gets the total number of the given list.
     *
     * @param   listName    given list
     * @return              total number of the given list in the database
     */
    fun getListNameCount(listName: String) : Int {
        return repository.getListNameCount(listName)
    }

    /**
     * Gets all the items from the given list.
     *
     * @param   listName    given list
     * @return              list of items from given list
     */
    fun getAllItems(listName: String): List<Item> {
        return repository.getAllItems(listName)
    }

    /**
     * Gets all the different names of the saved lists.
     *
     * @return              list of different names of saved lists
     */
    fun getAllSavedListNames() : List<String> {
        return repository.getAllSavedListNames()
    }

    /**
     * Gets all the different names of the items from the given list.
     *
     * @param   listName    given list
     * @return              list of different item names from given list
     */
    fun getAllItemNames(listName : String) : List<String> {
        return repository.getAllItemNames(listName)
    }
}