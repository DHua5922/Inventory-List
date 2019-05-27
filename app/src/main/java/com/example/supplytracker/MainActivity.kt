package com.example.supplytracker

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * This class starts the application and loads the splash screen for the application.
 * The splash screen will be displayed for a given time period. After that time period
 * has passed, it will transition to the supply list page (SupplyList.kt).
 */
class MainActivity : AppCompatActivity() {

    /**
     * Creates and displays splash screen.
     * This method is called when this activity is first created.
     *
     * @param   savedInstanceState  Bundle containing activity's previously frozen state, if there was one
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a splash screen.
        val splashScreen = object : Thread() {
            // Set up behavior for splash screen.
            override fun run() {
                try {
                    // Try to load this splash screen for 0.5 seconds.
                    sleep(500)
                    val intent = Intent(baseContext, SupplyList::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    // An error occurs while displaying this splash screen.
                    e.printStackTrace()
                }
            }
        }
        // Display this splash screen.
        splashScreen.start()
    }
}
