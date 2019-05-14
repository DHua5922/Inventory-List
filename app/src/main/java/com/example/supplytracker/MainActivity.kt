package com.example.supplytracker

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splashScreen = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(1000);
                    val intent = Intent(baseContext, SupplyList::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        splashScreen.start()
    }
}
