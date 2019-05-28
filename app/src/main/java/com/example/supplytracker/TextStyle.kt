package com.example.supplytracker

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan

/**
 * This class has functions, which can be called directly, that bold text.
 */
class TextStyle {
    companion object {
        /**
         * Bolds the given string from the given text.
         *
         * @param   keystring       string
         * @param   text            text
         * @return                  styled text
         */
        fun bold(keystring: String, text : String) : SpannableStringBuilder {
            val styledText = SpannableStringBuilder(text)
            val boldSpan = StyleSpan(Typeface.BOLD)
            val start: Int = text.indexOf(keystring)
            styledText.setSpan(boldSpan, start, start + keystring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return styledText
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
    }
}