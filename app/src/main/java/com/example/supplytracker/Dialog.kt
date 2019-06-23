package com.example.supplytracker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
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
                                   itemViewModel: ItemViewModel ?= null,
                                   listManager: ItemAdapter,
                                   method: MenuItem ?= null,
                                   itemDisplay : ItemAdapter.ItemHolder ?= null) {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)

                builder.apply {
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id ->
                        when(itemId) {
                            R.id.btn_delete -> {
                                listManager.removeItem(itemDisplay!!.adapterPosition)
                            }
                            else -> {
                                itemViewModel!!.delete(method!!, listName)
                                listManager.setItems(itemViewModel.getAllItems(listName))
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
                             hint : String,
                             itemViewModel: ItemViewModel,
                             listManager: ItemAdapter,
                             activity: Activity ?= null) {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(context)
                var selectedPosition = -1
                val input = AutoCompleteTextView(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.hint = hint
                val arrayAdapter = ArrayAdapter(
                    context,
                    R.layout.comparison_options,
                    itemViewModel.getAllSavedListNames()
                )
                input.setAdapter(arrayAdapter)
                input.setOnClickListener { input.showDropDown() }
                input.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView, view, position, l ->
                        selectedPosition = position
                    }
                builder.setView(input)

                builder.apply {
                    setPositiveButton(R.string.btn_dialog_ok) { dialog, id ->
                        when (itemId) {
                            R.id.option_save_list_as -> {
                                val name = "${input.text}".trim()
                                lateinit var styledText : SpannableStringBuilder
                                when {
                                    name.isEmpty() -> Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
                                    itemViewModel.getListNameCount(name) > 0 -> {
                                        styledText = TextStyle.bold(name, "$name already exists")
                                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        itemViewModel.add(listManager.getItems(), name)
                                        itemViewModel.delete("Unsaved")
                                        ItemListDisplay.listName = name
                                        (activity as ItemListDisplay).supportActionBar!!.title = name
                                        listManager.setItems(itemViewModel.getAllItems(name))
                                        styledText = TextStyle.bold(name, "This list has been saved as $name")
                                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else -> {
                                if(selectedPosition > -1) {
                                    input.setText(arrayAdapter.getItem(selectedPosition))
                                }
                                val listName = "${input.text}".trim()
                                val list = itemViewModel.getAllItems(listName)
                                lateinit var styledText : SpannableStringBuilder
                                if(itemId == R.id.option_open_list) {
                                    if(list.isNotEmpty()) {
                                        ItemListDisplay.listName = listName
                                        (activity as ItemListDisplay).supportActionBar!!.title = listName
                                        listManager.setItems(itemViewModel.getAllItems("${input.text}"))
                                    } else {
                                        styledText = TextStyle.bold(listName, "$listName does not exist")
                                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                                    }
                                } else {
                                    if(list.isNotEmpty()) {
                                        itemViewModel.delete(listName)
                                        if (listName == ItemListDisplay.listName)
                                            listManager.setItems(itemViewModel.getAllItems(listName))
                                        styledText = TextStyle.bold(listName, "$listName has been deleted")
                                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                                    } else {
                                        styledText = TextStyle.bold(listName, "$listName does not exist")
                                        Toast.makeText(context, styledText, LENGTH_SHORT).show()
                                    }
                                }
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
                val name = "${dialogView.field_new_info.text}".trim()
                if(name.isNotEmpty()) {
                    val items = listManager.getItems()
                    // try to update item with new name and exit dialog
                    items[position].name = name
                    if (itemViewModel.update(items[position])) {
                        // update list with new name
                        listManager.notifyItemChanged(position)
                        // exit dialog
                        alertDialog.dismiss()
                    } else {
                        items[position].name = "$currentName"
                    }
                } else {
                    Toast.makeText(context, "Name cannot be empty", LENGTH_SHORT).show()
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
                try {
                    items[position].amount = "${dialogView.field_new_info.text}".toDouble()
                    if (itemViewModel.update(items[position])) {
                        listManager.notifyItemChanged(position)
                        // exit dialog
                        alertDialog.dismiss()

                        // when item is being updated with new amount, check if item is empty or not
                        val displayLayout = itemDisplay.linearLayout
                        if (items[position].amount <= 0) {
                            // notify user that item is empty
                            val styledText = TextStyle.bold("$name", "Now, $name is empty")
                            Toast.makeText(context, styledText, LENGTH_SHORT).show()
                            // item is empty so layout is colored red
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.isEmpty))
                        } else {
                            // item is not empty so layout is colored white
                            displayLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                        }
                    } else {
                        items[position].amount = "$currentAmount".toDouble()
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

            // display dialog title
            dialogView.title.text = context.getString(title)
            // describe dialog purpose
            dialogView.description.text = context.getString(description)
            // notify user of what to enter in field
            dialogView.field_search_word.hint = context.getString(hint)

            // when ok button in dialog is clicked
            dialogView.btn_dialog_ok.setOnClickListener {
                // try to search items by name or keyword
                try {
                    // search items
                    listManager.setItems(
                        itemViewModel.search(
                            searchMethod = searchOption,
                            listName = listName,
                            word = "${dialogView.field_search_word.text}"
                        )
                    )
                    // exit dialog
                    alertDialog.dismiss()
                    // otherwise, search method is invalid
                } catch (e : Exception) {
                    Toast.makeText(context, e.message, LENGTH_SHORT).show()
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
                    Toast.makeText(context, "Amount must only be a number", LENGTH_SHORT).show()
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