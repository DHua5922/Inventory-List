package com.example.supplytracker

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.dialog_edit_field.view.btn_dialog_cancel
import kotlinx.android.synthetic.main.dialog_edit_field.view.btn_dialog_ok
import kotlinx.android.synthetic.main.dialog_edit_field.view.description
import kotlinx.android.synthetic.main.dialog_edit_field.view.title
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.*
import kotlinx.android.synthetic.main.dialog_search_item_word.view.*

/**
 * This class displays a list of items after the splash screen is shown.
 */
class SupplyList : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    private lateinit var database : SupplyDatabase
    private lateinit var listManager : ItemRecyclerAdapter
    private lateinit var listDisplay: RecyclerView
    private lateinit var editableName : EditText
    private lateinit var editableAmount : EditText

    /**
     * Creates and displays a list of items.
     * This method is called when this list is first created.
     *
     * @param   savedInstanceState  Bundle containing list's previous data, if there was one
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_list)

        database = SupplyDatabase(this)
        listManager = ItemRecyclerAdapter(this, database.getAllItems())
        listDisplay = findViewById(R.id.list_display)
        editableName = findViewById(R.id.editableName)
        editableAmount = findViewById(R.id.editableAmount)

        // add a horizontal line below each item displayed
        listDisplay.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        // load all items in list
        listDisplay.adapter = listManager
        // set layout manager for the view displaying items
        listDisplay.layoutManager = LinearLayoutManager(this)

        // button displays options when clicked
        findViewById<Button>(R.id.btn_add).setOnClickListener(this)
        findViewById<Button>(R.id.btn_clear).setOnClickListener(this)
        findViewById<Button>(R.id.btn_sort_names).setOnClickListener(this)
        findViewById<Button>(R.id.btn_sort_amount).setOnClickListener(this)
        findViewById<Button>(R.id.btn_search).setOnClickListener(this)
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
        when {
            // button for adding item
            view.id == R.id.btn_add -> {
                // when button is clicked, try to add item to list
                try {
                    val item = Item("${editableName.text}", "${editableAmount.text}".trim().toDouble())

                    if(database.addItem(item)) {
                        listManager.swapCursor(database.getAllItems())
                        editableName.text.clear()
                        editableAmount.text.clear()
                    }
                } catch(e : NumberFormatException) {
                    Toast.makeText(this, "Amount must only be a number", LENGTH_SHORT).show()
                }
            }
            // button for removing items
            view.id == R.id.btn_clear -> {
                showMenu(view, R.menu.options_sort_remove)
            }
            // button for sorting items by names
            view.id == R.id.btn_sort_names -> {
                showMenu(view, R.menu.options_sort_names)
            }
            // button for sorting items by amount
            view.id == R.id.btn_sort_amount -> {
                showMenu(view, R.menu.options_sort_amount)
            }
            // button for searching items
            view.id == R.id.btn_search -> {
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
            setOnMenuItemClickListener(this@SupplyList)
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
            database.clear(method)
            listManager = ItemRecyclerAdapter(this, database.getAllItems())

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

            // show comparisons to amount
            val comparisonArray : Spinner = dialogView.findViewById(R.id.array_comparisons)
            // bind event to clicked list of comparison option
            comparisonArray.onItemSelectedListener = this
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter.createFromResource(
                this, R.array.array_comparisons, R.layout.comparison_options
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.comparison_options)
                // Apply the adapter to the spinner
                comparisonArray.adapter = adapter
            }

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val amount = "${dialogView.field_search_amount.text}".trim()
                if(amount.isEmpty()) {
                    Toast.makeText(this, "Amount must only be a number", LENGTH_SHORT).show()
                } else {
                    // search items by amount
                    listManager.swapCursor(
                        database.search(method,
                        amount = amount.toDouble(),
                        comparison = comparisonArray.selectedItemPosition)
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
            listManager.swapCursor(database.search(method))
        }
        // otherwise, a sorting method is clicked
        else {
            listManager = ItemRecyclerAdapter(this, database.sort(method))
        }

        // update list
        listDisplay.adapter = listManager
        return true
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
                listManager.swapCursor(database.search(searchOption, "${dialogView.field_search_word.text}"))
                // exit dialog
                alertDialog.dismiss()
            // otherwise, search method is invalid
            } catch (e : Exception) {
                Toast.makeText(this, e.message, LENGTH_SHORT).show()
            }
        }

        // when cancel button in dialog is clicked, exit out of dialog
        dialogView.btn_dialog_cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}
