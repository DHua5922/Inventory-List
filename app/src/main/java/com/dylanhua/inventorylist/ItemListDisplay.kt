package com.dylanhua.inventorylist

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import android.widget.*
import android.view.*
import kotlinx.android.synthetic.main.activity_item_list_display.*
import java.util.Collections.swap

/**
 * This class handles the display of items in the list
 */
class ItemListDisplay : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var itemViewModel : ItemViewModel
    private lateinit var listManager : ItemAdapter

    companion object {
        var listName: String = "Unsaved"
    }

    /**
     * Shows a page that displays a list of items.
     *
     * @param   savedState  saved data
     */
    override fun onCreate(savedState : Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_item_list_display)

        Dialog.showBannerAd(this)

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel(application)::class.java)
        listManager = ItemAdapter(this, itemViewModel)

        // show items in first list if it exists
        val listNames = itemViewModel.getAllSavedListNames()
        if(listNames.isNotEmpty())
            listName = listNames[0]

        // set action bar title as name of current list
        setSupportActionBar(toolbar as Toolbar?)
        Utility.setTitle(
            supportActionBar,
            listName
        )

        // display items
        listManager.setItems(itemViewModel.getAllItems(listName))
        list_display.adapter = listManager
        list_display.layoutManager = LinearLayoutManager(this)

        // drag and drop animation
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            /**
             * Update position of items as item display is being dragged to a new position.
             *
             * @param   recyclerView    list display
             * @param   dragged         item display being dragged
             * @param   target          item display after drag and drop
             */
            override fun onMove(recyclerView : RecyclerView, dragged : RecyclerView.ViewHolder, target : RecyclerView.ViewHolder): Boolean {
                val fromPosition : Int = dragged.adapterPosition
                val toPosition : Int = target.adapterPosition
                val list = listManager.getItems()

                // drag down
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        // swap item order
                        swap(list, i, i + 1)

                        val order1 = list[i].order
                        val order2 = list[i + 1].order
                        list[i].order = order2
                        list[i + 1].order = order1
                    }
                }
                // drag up
                else {
                    for (i in fromPosition downTo toPosition + 1) {
                        // swap item order
                        swap(list, i, i - 1)

                        val order1 = list[i].order
                        val order2 = list[i - 1].order
                        list[i].order = order2
                        list[i - 1].order = order1
                    }
                }
                // notify adapter that item was dragged to new position
                listManager.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            /**
             * After item was dragged to a new position, update items with new position in the database.
             *
             * @param   recyclerView    list display
             * @param   viewHolder      item display
             */
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                itemViewModel.update(listManager.getItems())
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(list_display)

        // buttons that display popup of specific actions when clicked
        btn_add.setOnClickListener(this)
        btn_clear.setOnClickListener(this)
        btn_sort_names.setOnClickListener(this)
        btn_sort_amount.setOnClickListener(this)
        btn_search.setOnClickListener(this)
    }

    /**
     * Performs respective action for the given button that was clicked.
     * The given button is identified by its id that was set in the layout
     * design for this activity.
     *
     * @param   button    given view that was clicked
     */
    override fun onClick(button : View) {
        // identify button that was clicked and perform respective action
        when (button.id) {
            // button for adding item
            R.id.btn_add -> {
                // when button is clicked, try to add item to list
                try {
                    val item = Item(
                        order = listManager.getMaxOrder() + 1,
                        name = "${editableName.text}".trim(),
                        amount = "${editableAmount.text}".trim().toDouble(),
                        listName = listName
                    )

                    // try to add item
                    val result = itemViewModel.add(item, this)
                    if(result > -1) {
                        // successful item insertion
                        // clear edit fields
                        editableName.text.clear()
                        editableAmount.text.clear()
                        // update inserted item with new id
                        item.id = result
                        itemViewModel.update(item)
                        // add item to list in adapter
                        listManager.addItem(item)
                    }
                } catch(e : NumberFormatException) {
                    Utility.printStyledMessage(
                        this,
                        "Amount must only be a number"
                    )
                }
            }
            // button for removing items
            R.id.btn_clear -> {
                showMenu(button, R.menu.options_sort_remove)
            }
            // button for sorting items by names
            R.id.btn_sort_names -> {
                showMenu(button, R.menu.options_sort_names)
            }
            // button for sorting items by amount
            R.id.btn_sort_amount -> {
                showMenu(button, R.menu.options_sort_amount)
            }
            // button for searching items
            R.id.btn_search -> {
                showMenu(button, R.menu.options_search_items)
            }
        }
    }

    /**
     * Shows the given dropdown of more specific methods to manage this list
     * based on the general method chosen.
     *
     * @param   view        general list management method that was chosen
     * @param   popupXML    chosen dropdown of more specific list management methods
     */
    private fun showMenu(view: View, popupXML : Int) {
        PopupMenu(this, view).apply {
            // SupplyList implements OnMenuItemClickListener
            setOnMenuItemClickListener(this@ItemListDisplay)
            inflate(popupXML)
            show()
        }
    }

    /**
     * Identifies and shows the appropriate dialog or implements the specific method to manage this list.
     *
     * @param   method  more specific list management method that was chosen
     * @return          true if specific list management method was implemented, or false
     */
    override fun onMenuItemClick(method: MenuItem): Boolean {
        // show confirmation dialog for removing all items
        if(method.itemId == R.id.option_remove_all) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = Utility.bold("Are you sure you want to delete all the items in this inventory?"),
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method
            )
        }
        // show confirmation dialog for removing all empty items
        else if(method.itemId == R.id.option_remove_empty) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = Utility.bold("Are you sure you want to delete all the empty items in this inventory?"),
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method
            )
        }
        // show confirmation dialog for removing all leftover items
        else if(method.itemId == R.id.option_remove_leftover) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = Utility.bold("Are you sure you want to delete all the leftover items in this inventory?"),
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method
            )
        }
        // show confirmation dialog for removing all full items
        else if(method.itemId == R.id.option_remove_checked) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = Utility.bold("Are you sure you want to delete all the items that are full in this inventory?"),
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method
            )
        }
        // show custom dialog for searching items by name
        else if(method.itemId == R.id.option_search_name) {
            Dialog.showSearchWordDialog(
                context = this,
                listName = listName,
                searchOption = method,
                layout = R.layout.dialog_search_item_word,
                itemViewModel = itemViewModel,
                listManager = listManager,
                title = R.string.title_search_name,
                description = R.string.search_name_description,
                hint = R.string.hint_search_name
            )
        }
        // show custom dialog for searching items by keyword
        else if (method.itemId == R.id.option_search_keyword) {
            Dialog.showSearchWordDialog(
                context = this,
                listName = listName,
                searchOption = method,
                layout = R.layout.dialog_search_item_word,
                itemViewModel = itemViewModel,
                listManager = listManager,
                title = R.string.title_search_keyword,
                description = R.string.search_keyword_description,
                hint = R.string.hint_search_keyword
            )
        }
        // show custom dialog for searching items by amount
        else if(method.itemId == R.id.option_search_amount) {
            Dialog.showSearchAmountDialog(
                context = this,
                method = method,
                listName = listName,
                listManager = listManager,
                itemViewModel = itemViewModel,
                layoutDialog = R.layout.dialog_search_item_amount,
                arrayComparisons = R.array.array_comparisons,
                dropdownItemLayout = R.layout.dropdown_item_layout
            )
        }
        // update list display if searching empty, leftover, full, or all items
        else if(method.itemId == R.id.option_search_empty ||
            method.itemId == R.id.option_search_leftover ||
            method.itemId == R.id.option_search_full ||
            method.itemId == R.id.option_search_all
        ) {
            listManager.setItems(itemViewModel.search(method, listName))
        }

        // otherwise, update list display with sorted items
        else {
            listManager.setItems(itemViewModel.sort(method, listName))
        }

        list_display.adapter = listManager
        return true
    }

    /**
     * Creates the given dropdown menu in the action bar.
     *
     * @param   menu    given menu layout
     * @return          true
     */
    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.options_inventories, menu)
        return true
    }

    /**
     * Shows appropriate dialog that prompts user to save, open, or delete list.
     *
     * @param   item    clicked dropdown operation
     * @return          true if chosen operation has been implemented, or false
     */
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        return when (item.itemId) {
            // show dialog that prompts user to save list
            R.id.option_save_list_as -> {
                Dialog.showListPromptDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Enter the name for this inventory to be saved as",
                    hint = "Enter new name",
                    itemViewModel = itemViewModel,
                    listManager = listManager,
                    activity = this
                )
                true
            }
            // show dialog that prompts user to open list
            R.id.option_open_list -> {
                Dialog.showListPromptDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Choose which inventory to see",
                    hint = "Enter name",
                    itemViewModel = itemViewModel,
                    listManager = listManager,
                    activity = this
                )
                true
            }
            // show dialog that prompts user to delete list
            R.id.option_delete_list -> {
                Dialog.showListPromptDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Choose which inventory to delete",
                    hint = "Enter name",
                    itemViewModel = itemViewModel,
                    listManager = listManager
                )
                true
            }
            // show confirmation dialog for deleting all lists
            R.id.option_delete_list_all -> {
                Dialog.showConfirmationDialog(
                    context = this,
                    itemId = item.itemId,
                    message = Utility.bold("Are you want to delete all your inventories?"),
                    itemViewModel = itemViewModel,
                    listManager = listManager,
                    method = item
                )
                true
            }
            // false
            else -> super.onOptionsItemSelected(item)
        }
    }
}