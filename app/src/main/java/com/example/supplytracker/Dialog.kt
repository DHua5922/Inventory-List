package com.example.supplytracker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.*
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.dialog_edit_field.view.*
import kotlinx.android.synthetic.main.dialog_edit_field.view.btn_dialog_cancel
import kotlinx.android.synthetic.main.dialog_edit_field.view.btn_dialog_ok
import kotlinx.android.synthetic.main.dialog_edit_field.view.description
import kotlinx.android.synthetic.main.dialog_edit_field.view.title
import kotlinx.android.synthetic.main.dialog_search_item_amount.*
import kotlinx.android.synthetic.main.dialog_search_item_amount.view.*
import kotlinx.android.synthetic.main.dialog_search_item_word.view.*
import android.widget.ArrayAdapter
import android.widget.AdapterView

class Dialog {
    companion object {
        fun showConfirmationDialog(context : Context,
                                   itemId : Int = -1,
                                   listName : String = "Unsaved",
                                   title : String = "",
                                   message : String,
                                   listManager: ItemAdapter,
                                   itemViewModel: ItemViewModel ?= null,
                                   method: MenuItem ?= null,
                                   itemDisplay : ItemAdapter.ItemHolder ?= null) {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id -> }
                    setNegativeButton(R.string.btn_dialog_cancel) { dialog, id -> }
                    setTitle(title)
                    setMessage(message)
                    create()
                }
                builder.show()
            }

            alertDialog!!.setOnDismissListener {
                if(itemId == R.id.btn_delete)
                    itemDisplay!!.deleteBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            }

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                when(itemId) {
                    R.id.btn_delete -> {
                        listManager.removeItem(itemDisplay!!.adapterPosition)
                    }
                    else -> {
                        itemViewModel!!.delete(method!!, listName)
                        listManager.setItems(itemViewModel.getAllItems(listName))
                    }
                }
                alertDialog.dismiss()
            }
        }

        fun promptListDialog(context : Context,
                             itemId : Int,
                             title : String = "",
                             message : String,
                             hint : String,
                             itemViewModel: ItemViewModel,
                             listManager: ItemAdapter,
                             activity: Activity ?= null) {
            val input = Utility.buildAutoCompleteTextView(
                context,
                InputType.TYPE_CLASS_TEXT,
                hint,
                R.layout.comparison_options,
                itemViewModel.getAllSavedListNames(),
                itemId
            )

            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setView(input)
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id -> }
                    setNegativeButton(R.string.btn_dialog_cancel) { dialog, id -> }
                    setTitle(title)
                    setMessage(message)
                    create()
                }
                builder.show()
            }

            alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                val listName = "${input.text}".trim()
                when (itemId) {
                    R.id.option_save_list_as -> {
                        when {
                            listName.isEmpty() -> Utility.printStyledMessage(context, "Name cannot be empty")
                            itemViewModel.getListNameCount(listName) > 0 -> {
                                Utility.printStyledMessage(context, "$listName already exists", arrayOf(listName))
                            }
                            else -> {
                                itemViewModel.add(listManager.getItems(), listName)
                                itemViewModel.delete("Unsaved")
                                ItemListDisplay.listName = listName
                                (activity as ItemListDisplay).supportActionBar!!.title = listName
                                listManager.setItems(itemViewModel.getAllItems(listName))
                                Utility.printStyledMessage(context, "This list has been saved as $listName", arrayOf(listName))
                                alertDialog.dismiss()
                            }
                        }
                    }
                    else -> {
                        val list = itemViewModel.getAllItems(listName)
                        if(itemId == R.id.option_open_list) {
                            if(list.isNotEmpty()) {
                                ItemListDisplay.listName = listName
                                (activity as ItemListDisplay).supportActionBar!!.title = listName
                                listManager.setItems(list)
                                alertDialog.dismiss()
                            } else {
                                Utility.printStyledMessage(context, "List does not exist")
                            }
                        } else {
                            if(list.isNotEmpty()) {
                                itemViewModel.delete(listName)
                                if (listName == ItemListDisplay.listName)
                                    listManager.setItems(itemViewModel.getAllItems(listName))
                                Utility.printStyledMessage(context, "$listName has been deleted", arrayOf(listName))
                                alertDialog.dismiss()
                            } else {
                                Utility.printStyledMessage(context, "List does not exist")
                            }
                        }
                    }
                }
            }
        }

        fun showNameEditDialog(context : Context,
                               layout : Int,
                               itemViewModel : ItemViewModel,
                               listManager : ItemAdapter,
                               itemDisplay : ItemAdapter.ItemHolder) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val nameDisplay = itemDisplay.nameDisplay
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
            dialogView.info_to_be_changed.text = Utility.bold(arrayOf("$currentName"), context.getString(R.string.name_to_be_changed, currentName))
            // describe dialog purpose
            dialogView.description.text = context.getString(R.string.name_change_description)

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val name = "${dialogView.field_new_info.text}".trim()
                if(name.isNotEmpty()) {
                    // try to update item with new name and exit dialog
                    val position = itemDisplay.adapterPosition
                    val item = listManager.getItemAt(position)
                    item.name = name
                    if (itemViewModel.update(item)) {
                        // update list with new name
                        listManager.notifyItemChanged(position)
                        Utility.printStyledMessage(context, "Name for $currentName is now $name", arrayOf("$currentName", name))
                        // exit dialog
                        alertDialog.dismiss()
                    } else {
                        item.name = "$currentName"
                        Utility.printStyledMessage(context, "Could not update name for $currentName", arrayOf("$currentName"))
                    }
                } else {
                    Utility.printStyledMessage(context, "Name cannot be empty")
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
                                 itemDisplay : ItemAdapter.ItemHolder) {
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
            dialogView.info_to_be_changed.text = Utility.bold(arrayOf("$currentAmount"), context.getString(R.string.amount_to_be_changed, name, currentAmount))
            // describe dialog purpose
            dialogView.description.text = Utility.bold(arrayOf("$name"), context.getString(R.string.amount_change_description, name))
            // notify user that field is for entering decimal numbers
            dialogView.field_new_info.hint = context.getString(R.string.hint_dialog_new_amount)
            // set decimal as input type for editable amount
            dialogView.field_new_info.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                // try to update item with new amount and exit dialog
                try {
                    val newAmount = "${dialogView.field_new_info.text}".toDouble()
                    val position = itemDisplay.adapterPosition
                    val item = listManager.getItemAt(position)
                    item.amount = newAmount
                    if (itemViewModel.update(item)) {
                        listManager.notifyItemChanged(position)
                        Utility.printStyledMessage(context, "Amount for $name has been changed from $currentAmount to $newAmount", arrayOf("$name", "$currentAmount", "$newAmount"))
                        // exit dialog
                        alertDialog.dismiss()

                        // when item is being updated with new amount, check if item is empty or not
                        val displayLayout = itemDisplay.linearLayout
                        if (item.amount <= 0) {
                            // item is empty so layout is colored red
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                        } else {
                            // item is not empty so layout is colored white
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                        }
                    } else {
                        item.amount = "$currentAmount".toDouble()
                        Utility.printStyledMessage(context, "Could not update amount for $name", arrayOf("$name"))
                    }
                } catch(e : NumberFormatException) {
                    Toast.makeText(context, "Amount must only be a number", LENGTH_SHORT).show()
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        fun showSearchWordDialog(context : Context,
                                 listName: String,
                                 searchOption : MenuItem,
                                 layout : Int,
                                 itemViewModel: ItemViewModel,
                                 listManager : ItemAdapter,
                                 title : Int,
                                 description : Int,
                                 hint : Int) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            // show dialog
            val alertDialog : AlertDialog = AlertDialog.Builder(context).setView(dialogView).show()

            alertDialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            val searchField = dialogView.field_search_word
            lateinit var arrayAdapter: ArrayAdapter<String>
            //var selectedPosition = -1
            if(searchOption.itemId == R.id.option_search_name) {
                arrayAdapter = ArrayAdapter(
                    context,
                    R.layout.comparison_options,
                    itemViewModel.getAllItemNames(ItemListDisplay.listName)
                )

                searchField.setAdapter(arrayAdapter)
                searchField.setOnClickListener {
                    searchField.showDropDown()
                    Utility.hideKeyboard(context, searchField)
                }
            }

            // display dialog title
            dialogView.title.text = context.getString(title)
            // describe dialog purpose
            dialogView.description.text = context.getString(description)
            // notify user of what to enter in field
            searchField.hint = context.getString(hint)

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                // try to search items by name or keyword
                try {
                    val word = "${searchField.text}".trim()
                    if(word.isNotEmpty()) {
                        // search items
                        listManager.setItems(
                            itemViewModel.search(
                                searchMethod = searchOption,
                                listName = listName,
                                word = word
                            )
                        )
                        // exit dialog
                        alertDialog.dismiss()
                    } else {
                        Utility.printStyledMessage(context, "Field cannot be empty")
                    }
                // otherwise, search method is invalid
                } catch (e : Exception) {
                    Utility.printStyledMessage(context, e.message!!)
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        fun showSearchAmountDialog(context : Context,
                                   method : MenuItem,
                                   listName: String,
                                   listManager : ItemAdapter,
                                   itemViewModel: ItemViewModel,
                                   layoutDialog : Int,
                                   arrayComparisons : Int,
                                   comparisonOptionsLayout : Int) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layoutDialog, null)
            val alertDialog : AlertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val spinner = alertDialog.array_comparisons

            // bind event to clicked list of comparison option
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter.createFromResource(
                context, arrayComparisons, comparisonOptionsLayout
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(comparisonOptionsLayout)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val amount = "${dialogView.field_search_amount.text}".trim()
                if(amount.isEmpty()) {
                    Utility.printStyledMessage(context, "Amount must only be a number")
                } else {
                    // search items by amount
                    listManager.setItems(
                        itemViewModel.search(
                            method,
                            listName = listName,
                            amount = amount.toDouble(),
                            comparison = spinner.selectedItemPosition
                        )
                    )
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
}