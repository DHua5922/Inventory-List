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

class SupplyList : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_list)

        val database = SupplyDatabase(this)

        var adapter: ItemRecyclerAdapter = ItemRecyclerAdapter(this, database.getAllItems())
        val recyclerView: RecyclerView = findViewById(R.id.supply_list)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(LinearLayoutManager(this));


        val itemName : EditText = findViewById(R.id.itemName)
        val itemQuantity : EditText = findViewById(R.id.itemQuantity)

        val addBtn : Button = findViewById(R.id.btn_add)
        addBtn.setOnClickListener(View.OnClickListener {
            try {
                val name : String = itemName.text.toString().trim()
                val quantity : String = itemQuantity.text.toString().trim()

                if(name.length == 0)
                    throw IllegalArgumentException("Name cannot be empty")
                val item = Item(name, quantity.toInt())

                if(database.addItem(item)) {
                    adapter.swapCursor(database.getAllItems())
                    itemName.getText().clear()
                    itemQuantity.getText().clear()
                    Toast.makeText(this, "Item successfully added!", LENGTH_SHORT).show()
                } else {
                    throw Exception("Item could not be added!")
                }
            } catch(e : NumberFormatException) {
                Toast.makeText(this, "Quantity must only be whole numbers", LENGTH_SHORT).show()
            } catch (e : Exception) {
                Toast.makeText(this, e.message, LENGTH_SHORT).show()
            }
        })

        val btnClear : Button = findViewById(R.id.btn_clear)
        btnClear.setOnClickListener(View.OnClickListener {
            database.clear()
            adapter = ItemRecyclerAdapter(this, database.getAllItems())
            recyclerView.setAdapter(adapter)
            Toast.makeText(this, "List cleared!", LENGTH_SHORT).show()
        })

        val btnSortByAlphabet : Button = findViewById(R.id.btn_sort_alpha)
        btnSortByAlphabet.setOnClickListener(View.OnClickListener {
            adapter = ItemRecyclerAdapter(this, database.sortAlphabetically())
            recyclerView.setAdapter(adapter)
        })

        val btnSortByNumber : Button = findViewById(R.id.btn_sort_number)
        btnSortByNumber.setOnClickListener(View.OnClickListener {
            adapter = ItemRecyclerAdapter(this, database.sortNumerically())
            recyclerView.setAdapter(adapter)
        })
    }


}
