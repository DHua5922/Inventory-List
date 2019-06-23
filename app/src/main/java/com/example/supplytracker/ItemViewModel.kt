package com.example.supplytracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.text.SpannableStringBuilder
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT


/**
 * This class represents a ViewModel that holds app's UI
 * data in a lifecycle-conscious way that survives
 * configuration changes. All the data needed for the UI
 * are held and processed in this ViewModel.
 */
class ItemViewModel(application : Application) : AndroidViewModel(application) {
    private val app: Application = application
    private val repository: ItemRepository = ItemRepository(app)

    fun add(item : Item) : Long {
        val name = item.name.trim()
        val context = app
        lateinit var styledText : SpannableStringBuilder

        when {
            name.isEmpty() -> Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
            else -> {
                val result = repository.insert(item)
                if(result > 0) {
                    val amount = item.amount
                    styledText = TextStyle.bold(arrayOf(name, "$amount"), "$name (amount: $amount) added to list")
                    Toast.makeText(context, styledText, LENGTH_SHORT).show()
                    return result
                }
            }
        }

        return -1
    }

    fun add(items : List<Item>, listName : String) {
        for((i, item) in items.withIndex()) {
            val newItem = Item(
                name = item.name,
                amount = item.amount,
                isFull = item.isFull,
                order = i,
                listName = listName
            )

            val id = repository.insert(newItem)
            newItem.id = id
            repository.update(newItem)
        }
    }

    fun update(item : Item) : Boolean {
        return repository.update(item) > 0
    }

    fun update(items : List<Item>) : Boolean {
        return repository.update(items) > 0
    }

    /**
     * Sorts all or certain items by their names or amount based on chosen sort method.
     * Throws an exception if the chosen method for sorting is invalid.
     *
     * @param       sortMethod  chosen sort method
     * @exception   Exception   if sort method is invalid
     * @return                  items sorted based on chosen sort method
     */
    fun sort(sortMethod : MenuItem, listName: String) : List<Item> {
        lateinit var itemList : List<Item>
        var message = "Invalid method for sorting"

        // different methods to sort items
        when (sortMethod.itemId) {
            // sort by names
            R.id.option_sort_names_atoz -> {
                itemList = repository.sortNameAToZ(listName)
                message = "List sorted A - Z"
            }
            R.id.option_sort_names_ztoa -> {
                itemList = repository.sortNameZToA(listName)
                message = "List sorted Z - A"
            }
            R.id.option_sort_names_empty_atoz -> {
                itemList = repository.sortNameEmptyAToZ(listName)
                message = "Empty items sorted A - Z"
            }
            R.id.option_sort_names_empty_ztoa -> {
                itemList = repository.sortNameEmptyZToA(listName)
                message = "Empty items sorted Z - A"
            }
            R.id.option_sort_names_leftover_atoz -> {
                itemList = repository.sortNameLeftoverAToZ(listName)
                message = "Leftover items sorted A - Z"
            }
            R.id.option_sort_names_leftover_ztoa -> {
                itemList = repository.sortNameLeftoverZToA(listName)
                message = "Leftover items sorted Z - A"
            }
            R.id.option_sort_names_checked_atoz -> {
                itemList = repository.sortNameFullAToZ(listName)
                message = "Full (checked) items sorted A - Z"
            }
            R.id.option_sort_names_checked_ztoa -> {
                itemList = repository.sortNameFullZToA(listName)
                message = "Full (checked) items sorted Z - A"
            }
            // sort by amount
            R.id.option_sort_amount_increase -> {
                itemList = repository.sortAmountAscending(listName)
                message = "List sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_decrease -> {
                itemList = repository.sortAmountDescending(listName)
                message = "List sorted from highest to lowest amount"
            }
            R.id.option_sort_amount_empty -> {
                itemList = repository.sortAmountEmpty(listName)
                message = "Empty items sorted"
            }
            R.id.option_amount_leftover_increase -> {
                itemList = repository.sortAmountLeftoverAscending(listName)
                message = "Leftover items sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_leftover_decrease -> {
                itemList = repository.sortAmountLeftoverDescending(listName)
                message = "Leftover items sorted from highest to lowest amount"
            }
            R.id.option_sort_amount_full_increase -> {
                itemList = repository.sortAmountFullAscending(listName)
                message = "Full (checked) items sorted from lowest to highest amount"
            }
            R.id.option_sort_amount_full_decrease -> {
                itemList = repository.sortAmountFullDescending(listName)
                message = "Full (checked) items sorted from highest to lowest amount"
            }
            // invalid sort method
            else -> {
                Toast.makeText(app, message, LENGTH_SHORT).show()
                throw Exception(message)
            }
        }


        Toast.makeText(app, message, LENGTH_SHORT).show()
        return itemList
    }

    /**
     * Deletes all or certain items based on the chosen removal method.
     * Throws an exception if the chosen method for removing is invalid.
     *
     * @param       removalMethod   chosen removal method
     * @exception   Exception       if remove method is invalid
     * @return                      integer > 0 if removal method is successful, or 0
     */
    fun delete(removalMethod : MenuItem, listName : String) : Int {
        val result: Int
        var message = "Invalid method for removing items"

        // different methods to remove items
        when (removalMethod.itemId) {
            R.id.option_remove_all -> {
                result = delete(listName)
                message = "Removed all items"
            }
            R.id.option_remove_empty -> {
                result = repository.deleteEmpty(listName)
                message = "Removed all empty items"
            }
            R.id.option_remove_leftover -> {
                result = repository.deleteLeftover(listName)
                message = "Removed all leftover items"
            }
            R.id.option_remove_checked -> {
                result = repository.deleteFull(listName)
                message = "Removed all full (checked) items"
            } else -> {
            Toast.makeText(app, message, LENGTH_SHORT).show()
            throw Exception(message)
        }
        }

        Toast.makeText(app, message, LENGTH_SHORT).show()
        return result
    }

    fun delete(item : Item) : Int {
        lateinit var styledText : SpannableStringBuilder
        val name = item.name.trim()
        val result = repository.delete(item)
        if(result > 0) {
            styledText = TextStyle.bold(name, "$name has been deleted")
            Toast.makeText(app, styledText, LENGTH_SHORT).show()
        } else {
            styledText = TextStyle.bold(name, "$name could not be deleted")
            Toast.makeText(app, styledText, LENGTH_SHORT).show()
        }
        return result
    }

    fun delete(listName : String) : Int {
        return repository.deleteAllItems(listName)
    }

    /**
     * Searches all or certain items based on the chosen search method.
     * Throws an exception if the chosen method for searching is invalid.
     *
     * @param       searchMethod    chosen search method
     * @param       word            name or keyword to search item for
     * @param       amount          exact amount to search items for
     * @param       comparison      comparison for searching items with more than exact amount
     * @exception   Exception       if search method is invalid
     * @return                      chosen items to search for
     */
    fun search(searchMethod : MenuItem, listName: String, word : String = "", amount : Double = -1.0, comparison : Int = -1) : List<Item> {
        lateinit var itemList : List<Item>

        // different methods to search items
        when (searchMethod.itemId) {
            R.id.option_search_name -> {
                itemList = repository.getItemByName(word.trim(), listName)
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
                Toast.makeText(app, message, LENGTH_SHORT).show()
                throw Exception(message)
            }
        }

        return itemList
    }

    fun getListNameCount(listName: String) : Int {
        return repository.getListNameCount(listName)
    }

    fun getItem(id : Long) : Item {
        return repository.getItem(id)
    }

    fun getAllItems(listName: String): List<Item> {
        return repository.getAllItems(listName)
    }

    fun getAllSavedListNames() : List<String> {
        return repository.getAllSavedListNames()
    }

    fun getAllItemNames(listName : String) : List<String> {
        return repository.getAllItemNames(listName)
    }
}