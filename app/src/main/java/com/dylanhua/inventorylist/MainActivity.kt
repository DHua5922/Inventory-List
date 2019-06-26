package com.dylanhua.inventorylist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.supplytracker.R

/**
 * This class starts the application and loads the splash screen for the application.
 * The splash screen will be displayed for a given time period. After that time period
 * has passed, it will transition to the supply list page (SupplyList.kt).
 */
class MainActivity : AppCompatActivity() {

    /**
     * Creates and displays splash screen.
     * This method is called when splash screen is first created.
     *
     * @param   savedInstanceState  Bundle containing activity's previously frozen state, if there was one
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create splash screen
        val splashScreen = object : Thread() {
            // set up behavior for splash screen
            override fun run() {
                try {
                    // try to load splash screen for 0.5 seconds
                    sleep(500)
                    val intent = Intent(baseContext, ItemListDisplay::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    // error occurs while displaying splash screen
                    e.printStackTrace()
                }
            }
        }
        // display splash screen
        splashScreen.start()
    }
}
