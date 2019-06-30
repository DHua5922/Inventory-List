package com.dylanhua.inventorylist

import androidx.appcompat.app.ActionBar
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

/**
 * This class holds useful functions that will be used in 1 or more classes.
 * These static functions can be accessed directly by referencing the class
 * name and then the function name.
 */
class Utility {
    companion object {
        /**
         * Sets the given title in the given action bar.
         *
         * @param   actionBar   given action bar
         * @param   title       given title
         */
        fun setTitle(actionBar: ActionBar?, title: String) {
            actionBar!!.title = "Inventory: $title"
        }

        /**
         * Displays the given message in the given activity context.
         * The given strings in an array from the message are bold.
         *
         * @param   context         given activity context
         * @param   message         given message to display
         * @param   stringsToBold   given array of strings to bold
         */
        fun printStyledMessage(context : Context, message : String, stringsToBold : Array<String> = arrayOf("")) {
            Toast.makeText(context, bold(message, stringsToBold), LENGTH_LONG).show()
        }

        /**
         * Bolds the given strings that are stored in an array, starting
         * from the beginning of the given text to the end.
         *
         * @param   stringsToBold   given strings that are stored in an array
         * @param   text            given text
         * @return                  text with given strings in bold
         */
        fun bold(text : String, stringsToBold: Array<String> = arrayOf("")) : SpannableStringBuilder {
            var start = 0; var end: Int
            val styledText = SpannableStringBuilder(text)

            // bold each given string in the array
            for (stringToBold in stringsToBold) {
                val boldSpan = StyleSpan(Typeface.BOLD)
                start = text.indexOf(stringToBold, start)
                end = start + stringToBold.length
                styledText.setSpan(boldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = end
            }

            return styledText
        }

        /**
         * Builds the AutoCompleteTextView widget dynamically.
         *
         * @param   context             activity context
         * @param   inputType           type of input
         * @param   hint                message telling user what to enter in field
         * @param   dropdownItemLayout  layout for dropdown
         * @param   list                list of dropdown items
         * @oaram   itemId              view id
         * @return                      AutoCompleteTextView with attributes and events binded
         */
        fun buildAutoCompleteTextView(
                input : AutoCompleteTextView,
                context : Context,
                inputType : Int,
                hint : String,
                dropdownItemLayout : Int,
                list : List<String>) {
            input.inputType = inputType
            input.hint = hint
            // set dropdown of field
            input.setAdapter(
                ArrayAdapter(
                    context,
                    dropdownItemLayout,
                    list
                )
            )
            // hide keyboard if user is opening or deleting list
            input.setOnClickListener {
                input.showDropDown()
            }
        }
    }
}