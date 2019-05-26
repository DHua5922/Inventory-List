package com.example.supplytracker;

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.dialog_edit_field.view.*

class ItemRecyclerAdapter(context : Context, cursor : Cursor) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {

    val context = context
    var cursor = cursor

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(itemLayout: View) : RecyclerView.ViewHolder(itemLayout) {
        var linearLayout : LinearLayout = itemLayout.findViewById(R.id.layoutHolder)
        var checkBox : CheckBox = itemLayout.findViewById(R.id.checkBox)
        var itemName : TextView = itemLayout.findViewById(R.id.name)
        var itemQuantity : TextView = itemLayout.findViewById(R.id.quantity)
        var deleteBtn : Button = itemLayout.findViewById(R.id.btn_delete)
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

        if(cursor.moveToPosition(position)) {
            val name : String = cursor.getString(cursor.getColumnIndex("Name"))
            val quantity : Int = cursor.getInt(cursor.getColumnIndex("Quantity"))

            holder.itemName.setText(name)
            holder.itemName.setOnClickListener(View.OnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_field, null)
                val builder = AlertDialog.Builder(context).setView(dialogView)
                val alertDialog = builder.show()

                dialogView.title.text = "Replacing Item Name"
                dialogView.description.text = "The field above will replace the item name: ${holder.itemName.text}"

                dialogView.btn_dialog_ok.setOnClickListener {
                    val newName = dialogView.field.text.toString()

                    try {
                        SupplyDatabase(context).updateName(newName, holder.itemName.text.toString())
                        holder.itemName.text = newName
                        alertDialog.dismiss()
                    } catch(e : Exception) {
                        Toast.makeText(context, e.message, LENGTH_SHORT).show()
                    }
                }

                dialogView.btn_dialog_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            })

            holder.itemQuantity.setText(quantity.toString())
            holder.itemQuantity.setOnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_field, null)
                val builder = AlertDialog.Builder(context).setView(dialogView)
                val alertDialog = builder.show()

                dialogView.title.text = "Replacing Item Quantity"
                dialogView.description.text = "The field above will replace ${holder.itemQuantity.text} as the current quantity for ${holder.itemName.text}"

                dialogView.btn_dialog_ok.setOnClickListener {
                    if(SupplyDatabase(context).updateQuantity(
                            holder.itemName.text.toString(),
                            dialogView.field.text.toString(),
                            holder.itemQuantity.text.toString()
                        )) {
                        holder.itemQuantity.text = dialogView.field.text
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(context, "Quantity must only have numbers", LENGTH_SHORT).show()
                    }
                }

                dialogView.btn_dialog_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            holder.deleteBtn.setOnClickListener(View.OnClickListener {
                val database = SupplyDatabase(context)

                if(database.deleteItem(holder.itemName.text.toString())) {
                    this.swapCursor(database.getAllItems())
                    Toast.makeText(context, "Item successfully deleted!", LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Item could not be deleted!", LENGTH_SHORT).show()
                }
            })
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = cursor.count

    fun swapCursor(newCursor : Cursor) {
        cursor.close()
        cursor = newCursor
        notifyDataSetChanged()
    }
}
