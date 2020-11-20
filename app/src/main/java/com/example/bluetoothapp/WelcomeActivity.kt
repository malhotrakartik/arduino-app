package com.example.bluetoothapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Exception

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val background = object : Thread(){
            override fun run() {
                try {
                    Thread.sleep(1000)
                    val intent = Intent(this@WelcomeActivity , MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }catch (e : Exception){
                    e.printStackTrace()

                }
            }
        }
        background.start()
    }
}
