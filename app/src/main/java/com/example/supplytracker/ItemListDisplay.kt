package com.example.supplytracker

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.*
import android.view.*
import kotlinx.android.synthetic.main.dialog_search_item_amount.*
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.*
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.btn_dialog_cancel
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.btn_dialog_ok
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.description
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.title
import kotlinx.android.synthetic.main.dialog_search_item_word.view.*
import kotlinx.android.synthetic.main.list_display.*
import java.util.Collections.swap
import android.text.InputType
import android.widget.EditText



class ItemListDisplay : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var itemViewModel : ItemViewModel
    private lateinit var listManager : ItemAdapter
    private var listName : String = "Unsaved"

    override fun onCreate(savedState : Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.list_display)

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel(application)::class.java)
        listManager = ItemAdapter(this, itemViewModel)
        listManager.setItems(itemViewModel.getAllItems())
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
                        name = "${editableName.text}",
                        amount = "${editableAmount.text}".trim().toDouble()
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
                    Toast.makeText(this, "Amount must only be a number", Toast.LENGTH_SHORT).show()
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
        if(method.itemId == R.id.option_remove_all ||
                method.itemId == R.id.option_remove_empty ||
                method.itemId == R.id.option_remove_leftover ||
                method.itemId == R.id.option_remove_checked) {
            itemViewModel.delete(method)
            listManager.setItems(itemViewModel.getAllItems())
        }
        // if searching items by name or keyword
        else if(method.itemId == R.id.option_search_name ||
                    method.itemId == R.id.option_search_keyword) {
            openDialog(method)
        }
        // if searching items by amount
        else if(method.itemId == R.id.option_search_amount) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_item_amount, null)
            val alertDialog : AlertDialog = AlertDialog.Builder(this).setView(dialogView).show()
            val spinner = alertDialog.array_comparisons

            // bind event to clicked list of comparison option
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter.createFromResource(
                this, R.array.array_comparisons, R.layout.comparison_options
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.comparison_options)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val amount = "${dialogView.field_search_amount.text}".trim()
                if(amount.isEmpty()) {
                    Toast.makeText(this, "Amount must only be a number", Toast.LENGTH_SHORT).show()
                } else {
                    // search items by amount
                    listManager.setItems(
                        itemViewModel.search(
                            method,
                            amount = amount.toDouble(),
                            comparison = spinner.selectedItemPosition
                        )
                    )
                    // exit dialog
                    alertDialog.dismiss()
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        // if searching empty, leftover, full, or all items
        else if(method.itemId == R.id.option_search_empty ||
                    method.itemId == R.id.option_search_leftover ||
                    method.itemId == R.id.option_search_full ||
                    method.itemId == R.id.option_search_all) {
            listManager.setItems(itemViewModel.search(method))
        }

        // otherwise, a sorting method is clicked
        else {
            //itemViewModel.sort(method).observe(this,
                //Observer {item -> listManager.setItems(item!!)})
            listManager.setItems(itemViewModel.sort(method))
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
            R.id.option_save_list -> {
                confirmDialog(item.itemId, message = "Are you sure you want to save this list?")
                true
            }
            R.id.option_save_list_as -> {
                confirmDialog(item.itemId, message = "Enter the name for this list to be saved as")
                true
            }
            R.id.option_open_list -> {
                confirmDialog(item.itemId, message = "Choose which list to view")
                true
            }
            R.id.option_delete_this_list -> {
                confirmDialog(item.itemId, message = "Are you sure you want to delete this list? This action cannot be undone.")
                true
            }
            R.id.option_delete_list -> {
                confirmDialog(item.itemId, message = "Choose which list to delete")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDialog(itemId : Int, title : String = "", message : String) {
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)

            if(itemId == R.id.option_save_list_as ||
                    itemId == R.id.option_open_list ||
                    itemId == R.id.option_delete_list) {
                // Set up the input
                val input = EditText(this)
                // Specify the type of input expected
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
            }

            builder.apply {
                setPositiveButton(R.string.btn_dialog_ok) { dialog, id ->
                    dialog.dismiss()
                }
                setNegativeButton(R.string.btn_dialog_cancel) { dialog, id ->
                    dialog.dismiss()
                }
                setTitle(title)
                setMessage(message)
                create()
            }

            builder.show()
        }
    }

    /**
     * Shows dialog for allowing items to be searched by name or keyword.
     *
     * @param   searchOption    chosen method (by name or keyword) for searching items
     */
    private fun openDialog(searchOption : MenuItem) {
        // initialize variables for showing dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_item_word, null)
        lateinit var alertDialog : AlertDialog

        // if searching items by name
        if (searchOption.itemId == R.id.option_search_name) {
            // show dialog
            alertDialog = AlertDialog.Builder(this).setView(dialogView).show()
            // display dialog title
            dialogView.title.text = this.getString(R.string.title_search_name)
            // describe dialog purpose
            dialogView.description.text = this.getString(R.string.search_name_description)
            // notify user that field is for searching specific item by name
            dialogView.field_search_word.hint = this.getString(R.string.hint_item_name)
            // if searching items by keyword
        } else if (searchOption.itemId == R.id.option_search_keyword) {
            // show dialog
            alertDialog = AlertDialog.Builder(this).setView(dialogView).show()
            // display dialog title
            dialogView.title.text = this.getString(R.string.title_search_keyword)
            // describe dialog purpose
            dialogView.description.text = this.getString(R.string.search_keyword_description)
            // notify user that field is for searching items by keyword
            dialogView.field_search_word.hint = this.getString(R.string.hint_dialog_keyword)
        }

        // when ok button in dialog is clicked
        dialogView.btn_dialog_ok.setOnClickListener {
            // try to search items by name or keyword
            try {
                // search items
                listManager.setItems(itemViewModel.search(searchOption, "${dialogView.field_search_word.text}"))
                // exit dialog
                alertDialog.dismiss()
                // otherwise, search method is invalid
            } catch (e : Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // when cancel button in dialog is clicked, exit out of dialog
        dialogView.btn_dialog_cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}