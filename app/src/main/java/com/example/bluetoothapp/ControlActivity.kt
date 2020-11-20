package com.example.bluetoothapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*


class ControlActivity:AppCompatActivity(),TextToSpeech.OnInitListener{
    private var tts : TextToSpeech? = null
    lateinit var mic_on : ImageView
    lateinit var mic_text : TextView
    lateinit var locked : ImageView
    lateinit var unlocked : ImageView
    lateinit var led_on : Button
    lateinit var led_off : Button
    lateinit var led_disconnect : Button
    private val RQ_SPEECH_REC = 102



    companion object{
        var m_myUUID : UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        var m_bluetoothSocket : BluetoothSocket? = null
        lateinit var m_progress : ProgressDialog
        lateinit var bluetoothAdapter : BluetoothAdapter
        var is_Connected : Boolean = false
        lateinit var m_address: String
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)



//        led_on = findViewById(R.id.led_on)
        tts = TextToSpeech(this,this)
        mic_on = findViewById(R.id.mic_on)
//        led_off = findViewById(R.id.led_off)
        mic_text = findViewById(R.id.mic_text)
        locked = findViewById(R.id.locked)
        unlocked = findViewById(R.id.unlocked)
        led_disconnect = findViewById(R.id.led_disconnect)
        m_address = intent.getStringExtra("EXTRA_ADDRESS")
        println("address is $m_address")
        ConnectToDevice(this).execute()

//        led_on.setOnClickListener {
//            sendCommand("a")
//        }
//        led_off.setOnClickListener {
//            sendCommand("b")
//        }
        led_disconnect.setOnClickListener {
            disconnect()

        }

        mic_on.setOnClickListener {
//            val intent = Intent(this,MicActivity::class.java)
//            startActivity(intent)
            askSpeechInput()


        }
        locked.setOnClickListener {
            sendCommand("1")
            val text1 = "the lock has been unlocked"
            Texttospeech(text1)


            locked.visibility = View.GONE
            unlocked.visibility = View.VISIBLE
        }
        unlocked.setOnClickListener {
            sendCommand("0")
            val text1 = "the lock has been locked"
            Texttospeech(text1)


            locked.visibility = View.VISIBLE
            unlocked.visibility = View.GONE
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            mic_text.text = result?.get(0).toString()
            sendCommand(result?.get(0).toString())


        }
    }

    private fun askSpeechInput(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"speech rec not available",Toast.LENGTH_LONG).show()

        }else{
            val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something")
            startActivityForResult(intent,RQ_SPEECH_REC)
        }
    }
    private fun sendCommand(input : String){

        if(m_bluetoothSocket!=null){
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e:IOException){
                e.printStackTrace()
            }
        }

    }

    private fun disconnect(){
        if(m_bluetoothSocket!=null){
            try{
            m_bluetoothSocket!!.close()
            m_bluetoothSocket = null
            is_Connected = false

        }catch (e:IOException){
            e.printStackTrace()}
        }
        finish()
    }


    private class ConnectToDevice(c:Context) : AsyncTask<Void,Void,String>(){

        private var connectSuccess : Boolean = true
        private val context : Context
        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Connecting...","please wait")
        }

        override fun doInBackground(vararg params: Void?): String? {
          try{

              if(m_bluetoothSocket == null || !is_Connected){
                  bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                  val device : BluetoothDevice = bluetoothAdapter.getRemoteDevice(m_address)

                  m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)

                  println(m_myUUID)
                  BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                  m_bluetoothSocket!!.connect()
                  if(m_bluetoothSocket!!.isConnected){

                  }

              }

          }catch(e: IOException){

              connectSuccess = false
              e.printStackTrace()


          }
            return null

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Toast.makeText(context ,"unable to connect" , Toast.LENGTH_LONG).show()
            }
            else{
                is_Connected = true
            }
            m_progress.dismiss()

        }

    }


    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this,"the language specified is not supported",Toast.LENGTH_LONG).show()


            }

        }else{
            Toast.makeText(this,"initialisation failed",Toast.LENGTH_LONG).show()


        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun Texttospeech(string: String){
        tts?.speak(string,TextToSpeech.QUEUE_FLUSH,null,"")


    }

}