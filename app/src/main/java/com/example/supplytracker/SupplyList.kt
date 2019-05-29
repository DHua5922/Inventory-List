package com.example.supplytracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import java.lang.NumberFormatException

/**
 * This class displays a list of items after the splash screen is shown.
 */
class SupplyList : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
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
                    val item = Item("${editableName.text}", "${editableAmount.text}".trim().toInt())

                    if(database.addItem(item)) {
                        listManager.swapCursor(database.getAllItems())
                        editableName.text.clear()
                        editableAmount.text.clear()
                    }
                } catch(e : NumberFormatException) {
                    Toast.makeText(this, "Amount must only be whole numbers", LENGTH_SHORT).show()
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
        }
    }

    fun showMenu(view: View, popupXML : Int) {
        PopupMenu(this, view).apply {
            // SupplyList implements OnMenuItemClickListener
            setOnMenuItemClickListener(this@SupplyList)
            inflate(popupXML)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if(item.itemId == R.id.option_remove_all ||
                item.itemId == R.id.option_remove_empty ||
                item.itemId == R.id.option_remove_leftover ||
                item.itemId == R.id.option_remove_checked) {
            database.clear(item)
            listManager = ItemRecyclerAdapter(this, database.getAllItems())
        } else if(item.itemId == R.id.option_sort_names_atoz ||
            item.itemId == R.id.option_sort_names_ztoa ||
            item.itemId == R.id.option_sort_names_empty_atoz ||
            item.itemId == R.id.option_sort_names_empty_ztoa ||
            item.itemId == R.id.option_sort_names_leftover_atoz ||
            item.itemId == R.id.option_sort_names_leftover_ztoa ||
            item.itemId == R.id.option_sort_names_checked_atoz ||
            item.itemId == R.id.option_sort_names_checked_ztoa) {
            listManager = ItemRecyclerAdapter(this, database.sortNames(item))
        } else {
            listManager = ItemRecyclerAdapter(this, database.sortAmount(item))
        }

        listDisplay.adapter = listManager
        return true
    }
}
