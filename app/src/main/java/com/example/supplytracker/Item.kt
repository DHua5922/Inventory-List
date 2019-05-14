package com.example.supplytracker

import java.lang.IllegalArgumentException

class Item {

    var name: String = ""
        set(value: String) {
            if(value.trim().isEmpty())
                throw IllegalArgumentException("Item name cannot be empty")
            else
                field = value
        }

    var quantity: String = ""
        set(value: String) {
            if(value.trim().isEmpty())
                throw IllegalArgumentException("Quantity cannot be empty")
            else
                field = value
        }
}