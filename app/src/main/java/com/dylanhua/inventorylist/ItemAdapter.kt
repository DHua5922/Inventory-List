package com.dylanhua.inventorylist

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.template_item_display.view.*

/**
 * This class is a list adapter for managing the item displays and this list of items.
 */
class ItemAdapter(private val context : Context, private val itemViewModel: ItemViewModel) : RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private var itemList = mutableListOf<Item>()

    /**
     * This class provides a reference to the views for each item display,
     * using the given layout.
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
     * @param   position    position in this adapter
     */
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        onBindName(holder)
        onBindAmount(holder)
        onBindDeleteBtn(holder)
        onBindCheckbox(holder)
    }

    /**
     * When the checkbox in the given item display was clicked, color the layout of the item display
     * as an indication if the item is full, empty, or leftover.
     *
     * @param   itemDisplay     given item display
     */
    private fun onBindCheckbox(itemDisplay : ItemHolder) {
        val checkbox = itemDisplay.checkBox
        val displayLayout = itemDisplay.linearLayout
        val position = itemDisplay.adapterPosition

        // every time list is updated, check if item is still full
        if (itemList[position].isFull == 1) {
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
            when {
                // if item is full, layout of item display is colored green
                checkbox.isChecked -> {
                    itemList[position].isFull = 1
                    checkbox.isChecked = true
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.isFull
                    ))
                    Utility.printStyledMessage(
                        context,
                        "Now, $itemName is full",
                        arrayOf(itemName)
                    )
                }
                // if item is empty, layout of item display is colored red
                itemList[position].amount <= 0.0 -> {
                    itemList[position].isFull = 0
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.isEmpty
                    ))
                    Utility.printStyledMessage(
                        context,
                        "Now, $itemName is empty",
                        arrayOf(itemName)
                    )
                }
                // otherwise, item has leftover amount so layout of item display is colored white
                else -> {
                    itemList[position].isFull = 0
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context,
                        R.color.white
                    ))
                    Utility.printStyledMessage(
                        context,
                        "Now, $itemName is not full",
                        arrayOf(itemName)
                    )
                }
            }
            itemViewModel.update(itemList[position])
        }
    }

    /**
     * When the name display in the given item display was clicked, change the display's background color
     * to indicate that the name display is being modified. A dialog will be shown for entering a new name
     * for the item. The item in the database will be updated with the new name.
     *
     * @param   itemDisplay     item display
     */
    private fun onBindName(itemDisplay : ItemHolder) {
        // item display position in the adapter
        val position = itemDisplay.adapterPosition
        // name display
        val nameDisplay = itemDisplay.nameDisplay

        // display item name
        nameDisplay.text = itemList[position].name
        // when display for item name was clicked
        nameDisplay.setOnClickListener {
            // show dialog for editing item name
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
     * When the amount display in the given item display was clicked, change the amount display's background color to
     * indicate that the display is being modified. A dialog will be shown for entering a new amount for the item.
     * That item in the database will be updated with the new amount. Based on the item's new amount, color the layout
     * as an indication if the item is empty, full, or a leftover.
     *
     * @param   itemDisplay     item display
     */
    private fun onBindAmount(itemDisplay : ItemHolder) {
        val amountDisplay = itemDisplay.amountDisplay
        val displayLayout = itemDisplay.linearLayout
        val position = itemDisplay.adapterPosition

        // display item amount
        amountDisplay.text = "${itemList[position].amount}"

        // when item is being displayed, check if item is empty or not
        if(itemList[position].amount <= 0) {
            // item is empty so layout background is red
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
        } else {
            // item is not empty so layout background is white
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        // when display for item amount was clicked
        amountDisplay.setOnClickListener {
            // show dialog for editing item amount
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
     * When the delete button on the given item display was clicked, show a dialog for
     * confirmation to delete item. The item will be deleted from the database.
     *
     * @param   itemDisplay     item display
     */
    private fun onBindDeleteBtn(itemDisplay : ItemHolder) {
        // delete button
        val deleteBtn = itemDisplay.deleteBtn
        // when delete button was clicked
        deleteBtn.setOnClickListener{
            // color delete button black to show which item is being deleted
            deleteBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            // show confirmation dialog for deleting item
            Dialog.showConfirmationDialog(
                context = context,
                itemId = deleteBtn.id,
                message = "Are you sure you want to delete ${itemDisplay.nameDisplay.text}?",
                listManager = this,
                itemDisplay = itemDisplay
            )
        }
    }

    /**
     * Gets the number of items in this list.
     *
     * @return              number of items in this list
     */
    override fun getItemCount() = this.itemList.size

    /**
     * Sets the given list of items for this adapter.
     *
     * @param   items       given list of items
     */
    internal fun setItems(items: List<Item>) {
        this.itemList = items as MutableList<Item>
        notifyDataSetChanged()
    }

    /**
     * Adds the given item to this adapter.
     *
     * @param   item        given item
     */
    internal fun addItem(item: Item) {
        this.itemList.add(item)
        notifyItemInserted(this.itemList.size - 1)
    }

    /**
     * Removes the item at the given position from this adapter.
     *
     * @param   position    given position
     */
    internal fun removeItem(position : Int) {
        // item has been deleted from the database
        if(itemViewModel.delete(this.itemList[position]) > 0) {
            // remove item from this adapter
            this.itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Gets the list of items in this adapter.
     *
     * @return              list of items in this adapter
     */
    internal fun getItems() : List<Item> {
        return this.itemList
    }

    /**
     * Gets the maximum order in this adapter.
     *
     * @return              maximum order in this adapter
     */
    internal fun getMaxOrder() : Int {
        return (
            if (itemCount == 0) 0
            else this.itemList[itemCount - 1].order
        )
    }

    /**
     * Gets the item at the given position in this adapter.
     *
     * @return              item at the given position in this adapter
     */
    internal fun getItemAt(position: Int) : Item {
        return this.itemList[position]
    }
}
