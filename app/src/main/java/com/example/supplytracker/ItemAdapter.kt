package com.example.supplytracker

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.template_item_display.view.*

class ItemAdapter(private val context : Context, private val itemViewModel: ItemViewModel) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private var items = mutableListOf<Item>()

    /**
     * This class provides a reference to the views for each item display,
     * using the given layout that has views for displaying item information.
     */
    inner class ItemHolder(itemLayout: View) : RecyclerView.ViewHolder(itemLayout) {
        // layout for displaying item information
        val linearLayout : LinearLayout = itemLayout.display_layout
        // checkbox for indicating if item is full or not
        val checkBox : CheckBox = itemLayout.checkbox
        // display for item name
        val nameDisplay : TextView = itemLayout.display_name
        // display for item amount
        val amountDisplay : TextView = itemLayout.display_amount
        // button for deleting item display
        val deleteBtn : Button = itemLayout.btn_delete
    }

    /**
     * Creates a new view holder that has the views for item display,
     * using the parent view group and type of view.
     *
     * @param   parent      parent view group
     * @param   viewType    type of view
     * @return              new view holder
     */
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ItemHolder {
        // create a new view holder
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.template_item_display, parent, false) as View
        return ItemHolder(itemLayout)
    }

    /**
     * Binds events to the views in the given view holder.
     *
     * @param   holder      view holder
     * @param   position    position
     */
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        onBindName(holder)
        onBindAmount(holder)
        onBindDeleteBtn(holder)
        onBindCheckbox(holder)
    }

    /**
     * When the checkbox in the given item display is clicked, color the layout of the item display
     * as an indication if the item is full, empty, or being used. The name display is needed to
     * check if the specific item in the given database is empty or not.
     *
     * @param   itemDisplay     item display
     * @param   position        index of item display in list
     */
    private fun onBindCheckbox(itemDisplay : ItemHolder) {
        val checkbox = itemDisplay.checkBox
        val displayLayout = itemDisplay.linearLayout
        val position = itemDisplay.adapterPosition

        // every time list is updated, check if item is still full
        if (items[position].isFull == 1) {
            // item is still full so indicate it by checking box and coloring display green
            checkbox.isChecked = true
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isFull))
        } else {
            // item is not full anymore so indicate it by unchecking box
            checkbox.isChecked = false
        }

        // when checkbox is clicked
        checkbox.setOnClickListener {
            val itemName = "${itemDisplay.nameDisplay.text}".trim()
            lateinit var styledText : SpannableStringBuilder
            when {
                // if item is full, layout of item display is colored green
                checkbox.isChecked -> {
                    items[position].isFull = 1
                    checkbox.isChecked = true
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isFull))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is full")
                }
                // if item is empty, layout of item display is colored red
                items[position].amount <= 0.0 -> {
                    items[position].isFull = 0
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is empty")
                }
                // otherwise, item has leftover amount so layout of item display is colored white
                else -> {
                    items[position].isFull = 0
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is not full")
                }
            }
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
            itemViewModel.update(items[position])
        }
    }

    /**
     * When the given name display is clicked, change the display's background color to indicate that
     * the display is being modified and show a dialog for entering a new name for the given display
     * of the current item name. The item in the given database is updated with the new name.
     *
     * @param   nameDisplay     display of item name
     * @param   position        index of name display in list
     */
    private fun onBindName(itemDisplay : ItemHolder) {
        // display item name
        val position = itemDisplay.adapterPosition
        val nameDisplay = itemDisplay.nameDisplay
        nameDisplay.text = items[position].name
        // when display for item name is clicked
        nameDisplay.setOnClickListener {
            Dialog.showNameEditDialog(
                context = context,
                layout = R.layout.dialog_edit_field,
                itemViewModel = itemViewModel,
                listManager = this,
                itemDisplay = itemDisplay
            )
        }
    }

    /**
     * When the amount display in the given item display is clicked, change the amount display's background color to
     * indicate that the display is being modified and show a dialog for entering a new amount for that amount display.
     * The name display is needed to find the specific item in the given database. That item in the database is updated
     * with the new amount. Based on the item's new amount, color the layout as an indication if the item is empty,
     * full, or being used.
     *
     * @param   itemDisplay     item display
     * @param   position        index of item display in list
     */
    private fun onBindAmount(itemDisplay : ItemHolder) {
        val amountDisplay = itemDisplay.amountDisplay
        val displayLayout = itemDisplay.linearLayout
        val position = itemDisplay.adapterPosition

        // display item amount
        amountDisplay.text = "${items[position].amount}"

        // when item is being displayed for first time, check if item is empty or not
        if(items[position].amount <= 0) {
            // item is empty so layout is colored red
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
        } else {
            // item is not empty so layout is colored white
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        // when display for item amount is clicked
        amountDisplay.setOnClickListener {
            Dialog.showAmountEditDialog(
                context = context,
                layout = R.layout.dialog_edit_field,
                itemViewModel = itemViewModel,
                listManager = this,
                itemDisplay = itemDisplay
            )
        }
    }

    /**
     * When the delete button on the given item display is clicked, delete that item display.
     * The specific item will be deleted from the database.
     *
     * @param   itemDisplay     item display
     */
    private fun onBindDeleteBtn(itemDisplay : ItemHolder) {
        // when delete button of item display is clicked
        itemDisplay.deleteBtn.setOnClickListener{
            // delete item and update list
            Dialog.showConfirmationDialog(
                context = context,
                itemId = itemDisplay.deleteBtn.id,
                message = "Are you sure you want to delete ${itemDisplay.nameDisplay.text}",
                listManager = this,
                itemDisplay = itemDisplay
            )
        }
    }

    /**
     * Gets the number of items in the list
     *
     * @return              number of items in the list
     */
    override fun getItemCount() = items.size

    internal fun setItems(items: List<Item>) {
        this.items = items as MutableList<Item>
        notifyDataSetChanged()
    }

    internal fun addItem(item: Item) {
        this.items.add(item)
        notifyItemInserted(this.items.size - 1)
    }

    internal fun removeItem(position : Int) {
        if(itemViewModel.delete(this.items[position]) > 0) {
            this.items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    internal fun getItems() : List<Item> {
        return this.items
    }

    internal fun getMaxOrder() : Int {
        return (
            if (itemCount == 0) 0
            else this.items[itemCount - 1].order
        )
    }
}
