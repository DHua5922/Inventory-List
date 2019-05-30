package com.example.supplytracker

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_edit_field.view.*
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import java.lang.IndexOutOfBoundsException

/**
 * This class sets and changes the list display by manipulating each item display,
 * using the context of the page that is displaying the list and the database iterator.
 */
class ItemRecyclerAdapter(private val context: Context, private var cursor: Cursor) : RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>() {

    /**
     * This class provides a reference to the views for each item display,
     * using the given layout that has views for displaying item information.
     */
    class ViewHolder(itemLayout: View) : RecyclerView.ViewHolder(itemLayout) {
        // layout for displaying item information
        val linearLayout : LinearLayout = itemLayout.findViewById(R.id.display_layout)
        // checkbox for indicating if item is full or not
        val checkBox : CheckBox = itemLayout.findViewById(R.id.checkbox)
        // display for item name
        val nameDisplay : TextView = itemLayout.findViewById(R.id.display_name)
        // display for item amount
        val amountDisplay : TextView = itemLayout.findViewById(R.id.display_amount)
        // button for deleting item display
        val deleteBtn : Button = itemLayout.findViewById(R.id.btn_delete)
    }

    /**
     * Creates a new view holder that has the views for item display,
     * using the parent view group and type of view.
     *
     * @param   parent      parent view group
     * @param   viewType    type of view
     * @return              new view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view holder
        val itemLayout = LayoutInflater.from(parent.context).inflate(R.layout.template_supply_item, parent, false) as View
        return ViewHolder(itemLayout)
    }

    /**
     * Binds events to the views in the given view holder.
     *
     * @param   holder      view holder
     * @param   position    position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(cursor.moveToPosition(position)) {
            val database = SupplyDatabase(context)

            // bind events to views in view holder
            onBindName(holder.nameDisplay, database)
            onBindAmount(holder, database)
            onBindDeleteBtn(holder, database)
            onBindCheckbox(holder, database)
        }
    }

    /**
     * Gets the number of items in the list
     *
     * @return              number of items in the list
     */
    override fun getItemCount() = cursor.count

    /**
     * Switches to the given database iterator to update the changes in the list.
     *
     * @param   newCursor   given database iterator
     */
    fun swapCursor(newCursor : Cursor) {
        cursor.close()
        cursor = newCursor
        notifyDataSetChanged()
    }

    /**
     * When the checkbox in the given item display is clicked, color the layout of the item display
     * as an indication if the item is full, empty, or being used. The name display is needed to
     * check if the specific item in the given database is empty or not.
     *
     * @param   itemDisplay     item display
     * @param   database        database
     */
    private fun onBindCheckbox(itemDisplay : ViewHolder, database : SupplyDatabase) {
        val checkbox = itemDisplay.checkBox
        val displayLayout = itemDisplay.linearLayout
        val itemName = "${itemDisplay.nameDisplay.text}".trim()

        // every time list is updated, check if item is still full
        try {
            // if item is still full, indicate it by checking box and coloring display green
            if (cursor.getInt(cursor.getColumnIndex(SupplyDatabase.COL_ISFULL)) == 1) {
                checkbox.isChecked = true
                displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isFull))
            }
        } catch(e : IndexOutOfBoundsException) {
            Log.d("Item check failed", "Column for checking if item is full has not been created yet")
        }

        // when checkbox is clicked
        checkbox.setOnClickListener {
            lateinit var styledText : SpannableStringBuilder
            when {
                // if item is full, layout of item display is colored green
                checkbox.isChecked -> {
                    database.updateCheckmark(itemName, true)
                    checkbox.isChecked = true
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isFull))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is full")
                }
                // if item is empty, layout of item display is colored red
                database.getAmount("${itemDisplay.nameDisplay.text}") <= 0.0 -> {
                    database.updateCheckmark(itemName, false)
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is empty")
                }
                // otherwise, item still has some amount so layout of item display is colored white
                else -> {
                    database.updateCheckmark(itemName, false)
                    checkbox.isChecked = false
                    displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    styledText = TextStyle.bold(itemName, "Now, $itemName is not full")
                }
            }
            Toast.makeText(context, styledText, LENGTH_SHORT).show()
        }
    }

    /**
     * When the given name display is clicked, change the display's background color to indicate that
     * the display is being modified and show a dialog for entering a new name for the given display
     * of the current item name. The item in the given database is updated with the new name.
     *
     * @param   nameDisplay     display of item name
     * @param   database        database
     */
    private fun onBindName(nameDisplay : TextView, database: SupplyDatabase) {
        // display item name
        nameDisplay.text = cursor.getString(cursor.getColumnIndex(SupplyDatabase.COL_NAME)).trim()
        // when display for item name is clicked
        nameDisplay.setOnClickListener {
            // change display's background color to indicate it is being modified
            nameDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.pressed_info_display))

            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_field, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val currentName = nameDisplay.text

            // if dialog is exited out, change display's background color back to default
            alertDialog.setOnDismissListener {
                nameDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.field))
            }

            // display dialog title
            dialogView.title.text = context.getString(R.string.title_name_change)
            // style and display current item name
            dialogView.info_to_be_changed.text = TextStyle.bold("$currentName", context.getString(R.string.name_to_be_changed, currentName))
            // describe dialog purpose
            dialogView.description.text = context.getString(R.string.name_change_description)

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                // try to update item with new name and exit dialog
                if(database.updateName("${dialogView.field_new_info.text}", "$currentName")) {
                    // update list with new name
                    swapCursor(database.getAllItems())
                    // exit dialog
                    alertDialog.dismiss()
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
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
     * @param   database        database
     */
    private fun onBindAmount(itemDisplay : ViewHolder, database : SupplyDatabase) {
        val amountDisplay = itemDisplay.amountDisplay

        // display item amount
        amountDisplay.text = "${cursor.getDouble(cursor.getColumnIndex(SupplyDatabase.COL_AMOUNT))}"

        val name = itemDisplay.nameDisplay.text
        val displayLayout = itemDisplay.linearLayout

        // when item is being displayed for first time, check if item is empty or not
        if(database.getAmount("$name") <= 0) {
            // item is empty so layout is colored red
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
        } else {
            // item is not empty so layout is colored white
            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        // when display for item amount is clicked
        amountDisplay.setOnClickListener {
            // change display's background color to indicate it is being modified
            amountDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.pressed_info_display))

            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_field, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val currentAmount = amountDisplay.text

            // if dialog is exited out, change display's background color back to default
            alertDialog.setOnDismissListener {
                amountDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.field))
            }

            // display dialog title
            dialogView.title.text = context.getString(R.string.title_amount_change)
            // style and display current item amount
            dialogView.info_to_be_changed.text = TextStyle.bold("$currentAmount", context.getString(R.string.amount_to_be_changed, name, currentAmount))
            // describe dialog purpose
            dialogView.description.text = TextStyle.bold("$name", context.getString(R.string.amount_change_description, name))
            // notify user that field is for entering decimal numbers
            dialogView.field_new_info.hint = context.getString(R.string.hint_dialog_new_amount)
            // set decimal as input type for editable amount
            dialogView.field_new_info.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                // try to update item with new amount and exit dialog
                if(database.updateAmount(
                        "$name",
                        "${dialogView.field_new_info.text}".trim(),
                        "$currentAmount")) {
                    // update list with new amount
                    swapCursor(database.getAllItems())
                    // exit dialog
                    alertDialog.dismiss()

                    // when item is being updated with new amount, check if item is empty or not
                    if(database.getAmount("$name") <= 0) {
                        // notify user that item is empty
                        val styledText = TextStyle.bold("$name", "Now, $name is empty")
                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                        // item is empty so layout is colored red
                        displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                    } else {
                        // item is not empty so layout is colored white
                        displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    /**
     * When the delete button on the given item display is clicked, delete that item display.
     * The specific item will be deleted from the database.
     *
     * @param   itemDisplay     item display
     * @param   database        database
     */
    private fun onBindDeleteBtn(itemDisplay : ViewHolder, database : SupplyDatabase) {
        // when delete button of item display is clicked
        itemDisplay.deleteBtn.setOnClickListener{
            // if item is in database, delete item and update list
            if(database.deleteItem("${itemDisplay.nameDisplay.text}")) {
                this.swapCursor(database.getAllItems())
            }
        }
    }


}
