package com.example.supplytracker

import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class SupplyList : AppCompatActivity() {

    var supplyList = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_list)

        var adapter: ItemRecyclerAdapter = ItemRecyclerAdapter(supplyList, this)
        var recyclerView: RecyclerView = findViewById(R.id.supply_list)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(LinearLayoutManager(this));

        var itemName : EditText = findViewById(R.id.itemName)
        var itemQuantity : EditText = findViewById(R.id.itemQuantity)

        var addBtn : Button = findViewById(R.id.btn_add)
        addBtn.setOnClickListener(View.OnClickListener {
            var name : String = itemName.text.toString()
            var quantity : String = itemQuantity.text.toString()

            if(name.length == 0) {
                Toast.makeText(this, "Item name cannot be empty", LENGTH_SHORT).show()
            } else if(quantity.length == 0) {
                Toast.makeText(this, "Item quantity cannot be empty", LENGTH_SHORT).show()
            } else {
                var item = Item(name, quantity)
                val db = SupplyDatabase(this)
                if(db.addItem(item)) {
                    Toast.makeText(this, "Item successfully added!", LENGTH_SHORT).show()
                    supplyList.add(item)
                    adapter.notifyItemInserted(supplyList.indexOf(item))
                } else {
                    Toast.makeText(this, "Item could not be added!", LENGTH_SHORT).show()
                }
            }
        });
    }
}
