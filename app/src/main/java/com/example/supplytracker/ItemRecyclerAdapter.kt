package com.example.supplytracker;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout

class ItemRecyclerAdapter(private val itemList: ArrayList<Item>) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(itemLayout: View) : RecyclerView.ViewHolder(itemLayout) {
        var linearLayout : LinearLayout = itemLayout.findViewById(R.id.layoutHolder)
        var checkBox : CheckBox = itemLayout.findViewById(R.id.checkBox)
        var itemName : EditText = itemLayout.findViewById(R.id.name)
        var itemQuantity : EditText = itemLayout.findViewById(R.id.quantity)
        var button : Button = itemLayout.findViewById(R.id.btn_delete)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRecyclerAdapter.ViewHolder {
        // create a new view
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.template_supply_item, parent, false) as View
        return ViewHolder(itemLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // Set item views based on your views and data model
        holder.itemName.setText(itemList.get(position).name)
        holder.itemQuantity.setText(itemList.get(position).quantity)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemList.size
}
