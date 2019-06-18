package com.example.supplytracker

import android.app.AlertDialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_edit_field.view.*

class Dialog {
    companion object {
        fun showConfirmationDialog(context : Context,
                            itemId : Int = -1,
                            title : String = "",
                            message : String = "",
                            itemViewModel: ItemViewModel ?= null,
                            listManager: ItemAdapter ?= null,
                            method: MenuItem ?= null,
                            itemDisplay : ItemAdapter.ItemHolder ?= null) {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)

                builder.apply {
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id ->
                        when(itemId) {
                            R.id.option_save_list -> {

                            }
                            R.id.option_delete_this_list -> {

                            }
                            R.id.btn_delete -> {
                                listManager!!.removeItem(itemDisplay!!.adapterPosition)
                            }
                            else -> {
                                itemViewModel!!.delete(method!!)
                                listManager!!.setItems(itemViewModel.getAllItems())
                            }
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.btn_dialog_cancel) { dialog, id ->
                        dialog.dismiss()
                    }
                    setTitle(title)
                    setMessage(message)
                    create()
                }
                builder.show()
            }
        }

        fun promptListDialog(context : Context,
                                itemId : Int,
                                title : String = "",
                                message : String,
                                hint : String = "") {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)

                when (itemId) {
                    R.id.option_save_list_as -> {
                        // Set up the input
                        val input = EditText(context)
                        // Specify the type of input expected
                        input.inputType = InputType.TYPE_CLASS_TEXT
                        input.hint = hint
                        builder.setView(input)
                    }
                    R.id.option_delete_list -> {

                    }
                    R.id.option_open_list -> {

                    }
                }

                builder.apply {
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id ->
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.btn_dialog_cancel) { dialog, id ->
                        dialog.dismiss()
                    }
                    setTitle(title)
                    setMessage(message)
                    create()
                }

                builder.show()
            }
        }

        fun showNameEditDialog(context : Context,
                               layout : Int,
                               itemViewModel : ItemViewModel,
                               listManager : ItemAdapter,
                               nameDisplay : TextView,
                               position : Int) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val currentName = nameDisplay.text

            // change display's background color to indicate it is being modified
            nameDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.pressed_info_display))

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
                val items = listManager.getItems()
                items[position].name = "${dialogView.field_new_info.text}"
                // try to update item with new name and exit dialog
                if(itemViewModel.update(items[position])) {
                    // update list with new name
                    listManager.setItems(items)
                    // exit dialog
                    alertDialog.dismiss()
                } else {
                    items[position].name = "$currentName"
                }
            }


            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        fun showAmountEditDialog(context : Context,
                                 layout : Int,
                                 itemViewModel : ItemViewModel,
                                 listManager : ItemAdapter,
                                 itemDisplay : ItemAdapter.ItemHolder,
                                 position : Int) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val amountDisplay = itemDisplay.amountDisplay
            val name = itemDisplay.nameDisplay.text
            val currentAmount = amountDisplay.text

            // change display's background color to indicate it is being modified
            amountDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.pressed_info_display))

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
                val items = listManager.getItems()
                // try to update item with new amount and exit dialog
                items[position].amount = "${dialogView.field_new_info.text}".toDouble()
                if(itemViewModel.update(items[position])) {
                    listManager.setItems(items)
                    // exit dialog
                    alertDialog.dismiss()

                    // when item is being updated with new amount, check if item is empty or not
                    val displayLayout = itemDisplay.linearLayout
                    if(items[position].amount <= 0) {
                        // notify user that item is empty
                        val styledText = TextStyle.bold("$name", "Now, $name is empty")
                        Toast.makeText(context, styledText, Toast.LENGTH_SHORT).show()
                        // item is empty so layout is colored red
                        displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                    } else {
                        // item is not empty so layout is colored white
                        displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    }
                } else {
                    items[position].amount = "$currentAmount".toDouble()
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}
