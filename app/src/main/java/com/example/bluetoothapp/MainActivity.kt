package com.example.bluetoothapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//   lateinit var bluetoothAdapter: BluetoothAdapter
//    private val REQUEST_ENABLE_BT : Int = 1
//    lateinit var m_pairedDevices : Set<BluetoothDevice>
//    lateinit var device_name : Set<String>
//
//    companion object {
//        val EXTRA_ADDRESS : String = "Device_address"
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
////        txtPairedDevices = findViewById(R.id.txtPairedDevices)
//        if (bluetoothAdapter == null) {
//            // Device doesn't support Bluetooth
//            Toast.makeText(this@MainActivity , "bluetooth is not supported" , Toast.LENGTH_LONG).show()
//        }
//        if (bluetoothAdapter?.isEnabled == false) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
////            val intent = Intent(this@MainActivity,
////                MainActivity::class.java)
////            startActivity(intent)
//        }else{
//            val devices = bluetoothAdapter.bondedDevices
//            m_pairedDevices = bluetoothAdapter.bondedDevices
//
////            val list : ArrayList<BluetoothDevice> = ArrayList()
//            val list : ArrayList<String> = ArrayList()
//            if(!m_pairedDevices.isEmpty()){
//                for (device : BluetoothDevice in m_pairedDevices){
//                    list.add(device.name)
//                    list.add(device.address)
//
//
//
//                }
//            }else{
//                Toast.makeText(this@MainActivity , "no paired devices found" , Toast.LENGTH_LONG).show()
//            }
//            val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
//            select_device_list.adapter = adapter
//            select_device_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
////                val device : BluetoothDevice = list[position]
//                val address : String = list[position]
//
////                val address : String = device.address
//
//                val intent = Intent(this,ControlActivity::class.java)
//                intent.putExtra("EXTRA_ADDRESS",address)
//                startActivity(intent)
//
//            }
//
//        }
//
//    }
//
//    private fun pairedDeviceList(){
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when(requestCode){
//            REQUEST_ENABLE_BT ->
//                if(requestCode == Activity.RESULT_OK){
//                    Toast.makeText(this@MainActivity , "bluetooth is enabled" , Toast.LENGTH_LONG).show()
//
//                }else{
//                    Toast.makeText(this@MainActivity , "couldn't enable bluetooth" , Toast.LENGTH_LONG).show()
//
//                }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }










    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    lateinit var device_name : Set<String>


    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter == null) {
            Toast.makeText(this@MainActivity , "device doesnt support bluetooth" , Toast.LENGTH_LONG).show()
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener{ pairedDeviceList() }

    }

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<String> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device.name)
                list.add(device.address)

                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(this@MainActivity , "no devices found" , Toast.LENGTH_LONG).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val device: BluetoothDevice = list[position]
            if (position % 2 == 0) {
                val address: String = list[position + 1];
                val intent = Intent(this, ControlActivity::class.java)
                intent.putExtra("EXTRA_ADDRESS", address)
                startActivity(intent)

            } else {
                val address: String = list[position]


                val intent = Intent(this, ControlActivity::class.java)
                intent.putExtra("EXTRA_ADDRESS", address)
                startActivity(intent)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(this@MainActivity , " enable bluetooth" , Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity , "couldn't enable bluetooth" , Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this@MainActivity , "bluetooth enabling cancelled" , Toast.LENGTH_LONG).show()
            }
        }
    }


}


