package com.example.supplytracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import java.lang.NumberFormatException

/**
 * This class displays a list of items after the splash screen is shown.
 */
class SupplyList : AppCompatActivity(), View.OnClickListener {
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

        // initialize variables
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

        // button performs action when clicked
        findViewById<Button>(R.id.btn_add).setOnClickListener(this)
        findViewById<Button>(R.id.btn_clear).setOnClickListener(this)
        findViewById<Button>(R.id.btn_sort_alpha).setOnClickListener(this)
        findViewById<Button>(R.id.btn_sort_number).setOnClickListener(this)
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
            // button for deleting all items
            view.id == R.id.btn_clear -> {
                // when button is clicked, delete all items from list
                database.clear()
                listManager = ItemRecyclerAdapter(this, database.getAllItems())
                listDisplay.adapter = listManager
                Toast.makeText(this, "All items removed", LENGTH_SHORT).show()
            }
            // button for sorting items in alphabetical order
            view.id == R.id.btn_sort_alpha -> {
                // when button is clicked, sort items in alphabetical order
                listManager = ItemRecyclerAdapter(this, database.sortAlphabetically())
                listDisplay.adapter = listManager
                Toast.makeText(this, "List sorted in alphabetical order", LENGTH_SHORT).show()
            }
            // button for sorting items from lowest to highest amount
            view.id == R.id.btn_sort_number -> {
                // when button is clicked, sort items from lowest to highest amount
                listManager = ItemRecyclerAdapter(this, database.sortAmount())
                listDisplay.adapter = listManager
                Toast.makeText(this, "List sorted from lowest to highest amount", LENGTH_SHORT).show()
            }
        }
    }
}
