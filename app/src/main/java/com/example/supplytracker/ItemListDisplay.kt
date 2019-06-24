package com.example.supplytracker

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.*
import android.view.*
import kotlinx.android.synthetic.main.list_display.*
import java.util.Collections.swap

class ItemListDisplay : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var itemViewModel : ItemViewModel
    private lateinit var listManager : ItemAdapter

    companion object {
        var listName: String = "Unsaved"
    }

    override fun onCreate(savedState : Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.list_display)

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel(application)::class.java)
        listManager = ItemAdapter(this, itemViewModel)

        val listNames = itemViewModel.getAllSavedListNames()
        if(listNames.isNotEmpty())
            listName = listNames[0]

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = listName

        listManager.setItems(itemViewModel.getAllItems(listName))
        list_display.adapter = listManager
        list_display.layoutManager = LinearLayoutManager(this)

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(recyclerView : RecyclerView, dragged : RecyclerView.ViewHolder, target : RecyclerView.ViewHolder): Boolean {
                val fromPosition : Int = dragged.adapterPosition
                val toPosition : Int = target.adapterPosition
                val list = listManager.getItems()

                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        swap(list, i, i + 1)

                        val order1 = list[i].order
                        val order2 = list[i + 1].order
                        list[i].order = order2
                        list[i + 1].order = order1
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        swap(list, i, i - 1)

                        val order1 = list[i].order
                        val order2 = list[i - 1].order
                        list[i].order = order2
                        list[i - 1].order = order1
                    }
                }

                listManager.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                itemViewModel.update(listManager.getItems())
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(list_display)

        // button displays options when clicked
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
     * @param   view    given view that was clicked
     */
    override fun onClick(view : View) {
        // identify button that was clicked and perform respective action
        when (view.id) {
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

                    val result = itemViewModel.add(item)
                    if(result > -1) {
                        editableName.text.clear()
                        editableAmount.text.clear()
                        item.id = result
                        itemViewModel.update(item)
                        listManager.addItem(item)
                    }
                } catch(e : NumberFormatException) {
                    //Toast.makeText(this, "Amount must only be a number", Toast.LENGTH_SHORT).show()
                    Utility.printStyledMessage(this, "Amount must only be a number")
                }
            }
            // button for removing items
            R.id.btn_clear -> {
                showMenu(view, R.menu.options_sort_remove)
            }
            // button for sorting items by names
            R.id.btn_sort_names -> {
                showMenu(view, R.menu.options_sort_names)
            }
            // button for sorting items by amount
            R.id.btn_sort_amount -> {
                showMenu(view, R.menu.options_sort_amount)
            }
            // button for searching items
            R.id.btn_search -> {
                showMenu(view, R.menu.options_search_items)
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
     * Identifies and implements the more specific method to manage this list.
     *
     * @param   method  more specific list management method that was chosen
     * @return          true if specific list management method was implemented, or false
     */
    override fun onMenuItemClick(method: MenuItem): Boolean {
        // if one of method for removing items is clicked
        if(method.itemId == R.id.option_remove_all) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = "Are you sure you want to delete all the items in this list?",
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method)
        }
        else if(method.itemId == R.id.option_remove_empty) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = "Are you sure you want to delete all the empty items in this list?",
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method)
        }
        else if(method.itemId == R.id.option_remove_leftover) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = "Are you sure you want to delete all the leftover items in this list?",
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method)
        }
        else if(method.itemId == R.id.option_remove_checked) {
            Dialog.showConfirmationDialog(
                context = this,
                listName = listName,
                message = "Are you sure you want to delete all the items that are full in this list?",
                itemId = method.itemId,
                itemViewModel = itemViewModel,
                listManager = listManager,
                method = method)
        }
        // if searching items by name or keyword
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
        // if searching items by amount
        else if(method.itemId == R.id.option_search_amount) {
            Dialog.showSearchAmountDialog(
                context = this,
                method = method,
                listName = listName,
                listManager = listManager,
                itemViewModel = itemViewModel,
                layoutDialog = R.layout.dialog_search_item_amount,
                arrayComparisons = R.array.array_comparisons,
                comparisonOptionsLayout = R.layout.comparison_options
            )
        }
        // if searching empty, leftover, full, or all items
        else if(method.itemId == R.id.option_search_empty ||
            method.itemId == R.id.option_search_leftover ||
            method.itemId == R.id.option_search_full ||
            method.itemId == R.id.option_search_all) {
            listManager.setItems(itemViewModel.search(method, listName))
        }

        // otherwise, a sorting method is clicked
        else {
            //itemViewModel.sort(method).observe(this,
            //Observer {item -> listManager.setItems(item!!)})
            listManager.setItems(itemViewModel.sort(method, listName))
        }

        list_display.adapter = listManager
        return true
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.options_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.option_save_list_as -> {
                Dialog.promptListDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Enter the name for this list to be saved as",
                    hint = "Enter new name for list",
                    itemViewModel = itemViewModel,
                    listManager = listManager,
                    activity = this
                )
                true
            }
            R.id.option_open_list -> {
                Dialog.promptListDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Choose which list to view",
                    hint = "Enter name of list to open",
                    itemViewModel = itemViewModel,
                    listManager = listManager,
                    activity = this
                )
                true
            }
            R.id.option_delete_list -> {
                Dialog.promptListDialog(
                    context = this,
                    itemId = item.itemId,
                    message = "Choose which list to delete",
                    hint = "Enter name of list to delete",
                    itemViewModel = itemViewModel,
                    listManager = listManager
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}