package com.devdhua512.inventorylist

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.*
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.dialog_ad.*
import kotlinx.android.synthetic.main.dialog_ad.view.*
import kotlinx.android.synthetic.main.dialog_confirmation.view.message
import kotlinx.android.synthetic.main.dialog_list_prompt.view.*


/**
 * This class holds functions for showing dialogs to confirm user action or prompt user.
 * These static functions can be accessed directly by referencing the class name and then the function name.
 * Dialogs:
 *  - ask confirmation for deleting and searching items
 *  - prompt user to save, open, and delete specific list.
 */
class Dialog {
    companion object {

        /**
         * Shows banner ads, using the given activity context.
         *
         * @param    context     given activity context
         */
        fun showBannerAd(context: Context) {
            // use custom dialog layout
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ad, null)
            // build dialog
            val alertDialog = AlertDialog.Builder(context).apply {
                // load ad
                MobileAds.initialize(context, context.getString(R.string.app_id))
                dialogView.adView.loadAd(AdRequest.Builder().build())
                setView(dialogView)
            }.show()

            alertDialog.btn_dialog_exit_ad.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        /**
         * Asks user for confirmation to delete items.
         *
         * @param   context         activity context
         * @param   itemId          view id
         * @param   listName        given list
         * @param   message         message to ask user
         * @param   listManager     adapter for managing list
         * @param   itemViewModel   View Model
         * @param   method          popup menu option
         * @param   itemDisplay     item view in list display
         */
        fun showConfirmationDialog(
                context : Context,
                itemId : Int = -1,
                listName : String = "Unsaved",
                message : SpannableStringBuilder,
                listManager: ItemAdapter,
                itemViewModel: ItemViewModel?= null,
                method: MenuItem ?= null,
                itemDisplay : ItemAdapter.ItemHolder?= null
        ) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null).apply {
                this.message.text = message
            }
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()

            // if dialog was exited out, set the background for delete button back to red
            alertDialog!!.setOnDismissListener {
                if(itemId == R.id.btn_delete)
                    itemDisplay!!.deleteBtn.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.red)
                    )
            }

            // if ok button in dialog was clicked
            alertDialog.btn_dialog_ok.setOnClickListener {
                when(itemId) {
                    // delete item view
                    R.id.btn_delete -> {
                        listManager.removeItem(itemDisplay!!.adapterPosition)
                    }
                    // delete items based on chosen removal method
                    else -> {
                        itemViewModel!!.delete(method!!, listName, context)
                        listManager.setItems(itemViewModel.getAllItems(listName))
                    }
                }
                // exit dialog
                alertDialog.dismiss()
            }

            // if cancel button in dialog was clicked
            alertDialog.btn_dialog_cancel.setOnClickListener {
                // exit dialog
                alertDialog.dismiss()
            }
        }

        /**
         * Prompts user to open, delete, or save list.
         *
         * @param   context         activity context
         * @param   itemId          view id
         * @param   message         message to prompt user
         * @param   hint            message to tell user what to enter in field
         * @param   itemViewModel   View Model
         * @param   listManager     adapter for managing list
         * @param   activity        activity
         */
        fun showListPromptDialog(
                context : Context,
                itemId : Int,
                message : String,
                hint : String,
                itemViewModel: ItemViewModel,
                listManager: ItemAdapter,
                activity: Activity ?= null
        ) {
            lateinit var input : AutoCompleteTextView
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_list_prompt, null).apply {
                this.message.text = message
                input = this.input
                Utility.buildAutoCompleteTextView(
                    input,
                    context,
                    InputType.TYPE_CLASS_TEXT,
                    hint,
                    R.layout.dropdown_item_layout,
                    itemViewModel.getAllSavedListNames()
                )
            }
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()

            alertDialog!!.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }

            // if ok button in dialog was clicked
            alertDialog.btn_dialog_ok.setOnClickListener{
                val listName = "${input.text}".trim()
                when (itemId) {
                    // save list
                    R.id.option_save_list_as -> {
                        when {
                            listName.isEmpty() -> Utility.printStyledMessage(
                                context,
                                "Name cannot be empty"
                            )
                            itemViewModel.getListNameCount(listName) > 0 -> {
                                Utility.printStyledMessage(
                                    context,
                                    "$listName already exists",
                                    arrayOf(listName)
                                )
                            }
                            else -> {
                                // copy items and add them to database
                                itemViewModel.add(listManager.getItems(), listName)
                                // delete unsaved items
                                itemViewModel.delete("Unsaved")
                                // after saving items, change to saved list
                                ItemListDisplay.listName = listName
                                Utility.setTitle(
                                    (activity as ItemListDisplay).supportActionBar,
                                    listName
                                )
                                listManager.setItems(itemViewModel.getAllItems(listName))
                                Utility.printStyledMessage(
                                    context,
                                    "This list has been saved as $listName",
                                    arrayOf(listName)
                                )
                                // exit dialog
                                alertDialog.dismiss()
                            }
                        }
                    }
                    // open or delete list
                    else -> {
                        val list = itemViewModel.getAllItems(listName)
                        // open list
                        if(itemId == R.id.option_open_list) {
                            if(list.isNotEmpty()) {
                                // display list that user wants to open
                                ItemListDisplay.listName = listName
                                Utility.setTitle(
                                    (activity as ItemListDisplay).supportActionBar,
                                    listName
                                )
                                listManager.setItems(list)
                                // exit dialog
                                alertDialog.dismiss()
                            } else {
                                Utility.printStyledMessage(
                                    context,
                                    "List does not exist"
                                )
                            }
                        }
                        // delete chosen list
                        else {
                            if(list.isNotEmpty()) {
                                // delete chosen list
                                itemViewModel.delete(listName)
                                if (listName == ItemListDisplay.listName)
                                    // if deleting current list, update list display
                                    listManager.setItems(itemViewModel.getAllItems(listName))
                                Utility.printStyledMessage(
                                    context,
                                    "$listName has been deleted",
                                    arrayOf(listName)
                                )
                                // exit dialog
                                alertDialog.dismiss()
                            } else {
                                Utility.printStyledMessage(
                                    context,
                                    "List does not exist"
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * Prompts user to change item name.
         *
         * @param   context         activity context
         * @param   layout          dialog layout
         * @param   listManager     adapter for managing list
         * @param   itemViewModel   View Model
         * @param   itemDisplay     item view in list display
         */
        fun showNameEditDialog(
                context : Context,
                layout : Int,
                itemViewModel : ItemViewModel,
                listManager : ItemAdapter,
                itemDisplay : ItemAdapter.ItemHolder
        ) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val nameDisplay = itemDisplay.nameDisplay
            val currentName = nameDisplay.text

            // change display's background color to indicate it is being modified
            nameDisplay.setBackgroundColor(ContextCompat.getColor(context,
                R.color.pressed_info_display
            ))

            // if dialog was exited out, change display's background color back to default
            alertDialog.setOnDismissListener {
                nameDisplay.setBackgroundColor(ContextCompat.getColor(context, R.color.field))
            }

            // display dialog title
            dialogView.title.text = context.getString(R.string.title_name_change)
            // style and display current item name
            dialogView.info_to_be_changed.text = Utility.bold(
                context.getString(R.string.name_to_be_changed, currentName),
                arrayOf("$currentName")
            )
            // describe what will happen to item name
            dialogView.description.text = context.getString(R.string.name_change_description)

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val newName = "${dialogView.field_new_info.text}".trim()
                if(newName.isNotEmpty()) {
                    // try to update item with new name and exit dialog
                    val position = itemDisplay.adapterPosition
                    val item = listManager.getItemAt(position)
                    item.name = newName
                    if (itemViewModel.update(item)) {
                        // update item display with new name
                        listManager.notifyItemChanged(position)
                        Utility.printStyledMessage(
                            context,
                            "Name for $currentName has been changed to $newName",
                            arrayOf("$currentName", newName)
                        )
                        // exit dialog
                        alertDialog.dismiss()
                    } else {
                        // change back to current item name if item could not be updated with new name
                        item.name = "$currentName"
                        Utility.printStyledMessage(
                            context,
                            "Could not update name for $currentName",
                            arrayOf("$currentName")
                        )
                    }
                } else {
                    Utility.printStyledMessage(context, "Name cannot be empty")
                }
            }

            // when cancel button in dialog is clicked, exit dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        /**
         * Prompts user to change item amount.
         *
         * @param   context         activity context
         * @param   layout          dialog layout
         * @param   listManager     adapter for managing list
         * @param   itemViewModel   View Model
         * @param   itemDisplay     item view in list display
         */
        fun showAmountEditDialog(
                context : Context,
                layout : Int,
                itemViewModel : ItemViewModel,
                listManager : ItemAdapter,
                itemDisplay : ItemAdapter.ItemHolder
        ) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            val alertDialog = AlertDialog.Builder(context).setView(dialogView).show()
            val amountDisplay = itemDisplay.amountDisplay
            val name = itemDisplay.nameDisplay.text
            val currentAmount = amountDisplay.text

            // change display's background color to indicate it is being modified
            amountDisplay.setBackgroundColor(ContextCompat.getColor(context,
                R.color.pressed_info_display
            ))

            // if dialog was exited out, change display's background color back to default
            alertDialog.setOnDismissListener {
                amountDisplay.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.field
                ))
            }

            // display dialog title
            dialogView.title.text = context.getString(R.string.title_amount_change)
            // style and display current item amount
            dialogView.info_to_be_changed.text = Utility.bold(
                context.getString(R.string.amount_to_be_changed, name, currentAmount),
                arrayOf("$currentAmount")
            )
            // describe what will happen to item amount
            dialogView.description.text = Utility.bold(
                context.getString(R.string.amount_change_description, name),
                arrayOf("$name")
            )
            // notify user that field is for entering decimal numbers
            dialogView.field_new_info.hint = context.getString(R.string.hint_dialog_new_amount)
            // set decimal as input type for editable amount
            dialogView.field_new_info.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // when ok button in dialog was clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                try {
                    // try to update item with new amount and exit dialog
                    val newAmount = "${dialogView.field_new_info.text}".toDouble()
                    val position = itemDisplay.adapterPosition
                    val item = listManager.getItemAt(position)
                    item.amount = newAmount
                    if (itemViewModel.update(item)) {
                        // update item display with new amount
                        listManager.notifyItemChanged(position)
                        Utility.printStyledMessage(
                            context,
                            "Amount for $name has been changed from $currentAmount to $newAmount",
                            arrayOf("$name", "$currentAmount", "$newAmount")
                        )
                        // exit dialog
                        alertDialog.dismiss()

                        // when item has been updated with new amount, update item view background based on new amount
                        val displayLayout = itemDisplay.linearLayout
                        if (item.amount <= 0) {
                            // item is empty so item view background is red
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context,
                                R.color.isEmpty
                            ))
                        } else {
                            // item is not empty so item view background is white
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context,
                                R.color.white
                            ))
                        }
                    } else {
                        item.amount = "$currentAmount".toDouble()
                        Utility.printStyledMessage(
                            context,
                            "Could not update amount for $name",
                            arrayOf("$name")
                        )
                    }
                } catch(e : NumberFormatException) {
                    Utility.printStyledMessage(context, "Amount must only be a number")
                }
            }

            // when cancel button in dialog is clicked, exit out of dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        /**
         * Prompts user to search items by name or keyword.
         *
         * @param   context         activity context
         * @param   listName        given list
         * @param   searchOption    popup item dropdown
         * @param   layout          dialog layout
         * @param   itemViewModel   View Model
         * @param   listManager     adapter for managing list
         * @param   title           dialog title
         * @param   description     description of how search field works
         * @param   hint            message telling user what to enter in field
         */
        fun showSearchWordDialog(
                context : Context,
                listName: String,
                searchOption : MenuItem,
                layout : Int,
                itemViewModel: ItemViewModel,
                listManager : ItemAdapter,
                title : Int,
                description : Int,
                hint : Int
        ) {
            // initialize variables for showing dialog
            val dialogView = LayoutInflater.from(context).inflate(layout, null)
            // show dialog
            val alertDialog : AlertDialog = AlertDialog.Builder(context).setView(dialogView).show()

            // build search field
            val searchField = dialogView.field_search_word
            lateinit var arrayAdapter: ArrayAdapter<String>
            if(searchOption.itemId == R.id.option_search_name) {
                arrayAdapter = ArrayAdapter(
                    context,
                    R.layout.dropdown_item_layout,
                    itemViewModel.getAllItemNames(ItemListDisplay.listName)
                )

                searchField.setAdapter(arrayAdapter)
                searchField.setOnClickListener {
                    searchField.showDropDown()
                }
            }

            // display dialog title
            dialogView.title.text = context.getString(title)
            // describe how search field works
            dialogView.description.text = context.getString(description)
            // notify user of what to enter in field
            searchField.hint = context.getString(hint)

            // when ok button in dialog was clicked
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
                        Utility.printStyledMessage(
                            context,
                            "Field cannot be empty"
                        )
                    }
                // otherwise, search method is invalid
                } catch (e : Exception) {
                    Utility.printStyledMessage(context, e.message!!)
                }
            }

            // when cancel button in dialog was clicked, exit dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        /**
         * Prompts user to search items by amount.
         *
         * @param   context                 activity context
         * @param   method                  popup item dropdown
         * @param   listName                given list
         * @param   listManager             adapter for managing list
         * @param   itemViewModel           View Model
         * @param   layoutDialog            dialog layout
         * @param   arrayComparisons        string array of comparision options
         * @param   dropdownItemLayout      dropdown layout
         */
        fun showSearchAmountDialog(
                context : Context,
                method : MenuItem,
                listName: String,
                listManager : ItemAdapter,
                itemViewModel: ItemViewModel,
                layoutDialog : Int,
                arrayComparisons : Int,
                dropdownItemLayout : Int
        ) {
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
            // Create an ArrayAdapter using the string array and a spinner layout
            ArrayAdapter.createFromResource(
                context, arrayComparisons, dropdownItemLayout
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(dropdownItemLayout)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }

            // when ok button in dialog was clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                val amount = "${dialogView.field_search_amount.text}".trim()
                if(amount.isEmpty()) {
                    Utility.printStyledMessage(
                        context,
                        "Amount must only be a number"
                    )
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

            // when cancel button in dialog was clicked, exit dialog
            dialogView.btn_dialog_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}