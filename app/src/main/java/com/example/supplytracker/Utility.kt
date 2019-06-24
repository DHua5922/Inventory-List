package com.example.supplytracker

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class Utility {
    companion object {
        fun hideKeyboard(context : Context, view: View) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun printStyledMessage(context : Context, message : String, stringsToBold : Array<String> = arrayOf("")) {
            Toast.makeText(context, bold(stringsToBold, message), LENGTH_SHORT).show()
        }

        /**
         * Bolds the given strings that are stored in an array, starting from the beginning of the text to the end
         *
         * @param   stringsToBold   given strings that are stored in an array
         * @param   text            text
         * @return                  styled text
         */
        fun bold(stringsToBold: Array<String>, text : String) : SpannableStringBuilder {
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

        fun buildAutoCompleteTextView(context : Context, inputType : Int, hint : String, dropdownItemLayout : Int, list : List<String>, itemId : Int) : AutoCompleteTextView {
            val input = AutoCompleteTextView(context)
            input.inputType = inputType
            input.hint = hint
            input.setAdapter(
                ArrayAdapter(
                    context,
                    dropdownItemLayout,
                    list
                )
            )
            input.setOnClickListener {
                input.showDropDown()
                if(itemId == R.id.option_open_list || itemId == R.id.option_delete_list) {
                    hideKeyboard(context, input)
                }
            }
            return input
        }
    }
}