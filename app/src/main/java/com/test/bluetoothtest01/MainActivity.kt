package com.test.bluetoothtest01

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.*

class MainActivity : AppCompatActivity() {

    final val REQUEST_ENABLE_BT = 1;

    lateinit var myBluetoothAdapter: BluetoothAdapter
    lateinit var BTArrayAdapter : ArrayAdapter<String>
    lateinit var pairedDevices : Set<BluetoothDevice>
    lateinit var onBtn: Button
    lateinit var offBtn: Button
    lateinit var listBtn: Button
    lateinit var findBtn: Button
    lateinit var text: TextView
    lateinit var myListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(myBluetoothAdapter == null) {
            onBtn.setEnabled(false)
            offBtn.setEnabled(false)
            listBtn.setEnabled(false)
            findBtn.setEnabled(false)
            text.text = "Status: not supported"

            Toast.makeText(applicationContext, "Your device does not support Bluetooth", Toast.LENGTH_LONG).show()
        } else {
            text = findViewById(R.id.text)
            onBtn = findViewById(R.id.turnOn)
            onBtn.setOnClickListener(View.OnClickListener {
                on(it)
            })
            offBtn = findViewById(R.id.turnOff)
            offBtn.setOnClickListener(View.OnClickListener {
                off(it)
            })
            listBtn = findViewById(R.id.paired)
            listBtn.setOnClickListener(View.OnClickListener {
                list(it)
            })
            findBtn = findViewById(R.id.search)
            findBtn.setOnClickListener(View.OnClickListener {
                find(it)
            })
            myListView = findViewById(R.id.listView1)
            BTArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
            myListView.adapter = BTArrayAdapter
        }
    }

    fun on(view: View) {
        if(!myBluetoothAdapter.isEnabled) {
            var turnOnIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT)

            Toast.makeText(applicationContext, "Bluetooth turned on", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "Bluetooth is already on", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BT) {
            if(myBluetoothAdapter.isEnabled) {
                text.text = "Status: Enabled"
            } else {
                text.text = "Status: Disabled"
            }
        }
    }

    fun list(view: View) {
        pairedDevices = myBluetoothAdapter.bondedDevices

        for(device in pairedDevices) {
            BTArrayAdapter.add(device.name + "\n" + device.address)
            Toast.makeText(applicationContext, "Show Paired Devices", Toast.LENGTH_SHORT).show()
        }
    }

    var bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var action = intent?.action
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                var device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                BTArrayAdapter.add(device?.name + "\n" + device?.address)
                BTArrayAdapter.notifyDataSetChanged()
            }
        }
    }

    fun find(view: View) {
        if(myBluetoothAdapter.isDiscovering) {
            myBluetoothAdapter.cancelDiscovery()
        } else {
            BTArrayAdapter.clear()
            myBluetoothAdapter.startDiscovery()
            registerReceiver(bReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        }
    }

    fun off(view: View) {
        myBluetoothAdapter.disable()
        text.text = "Status: Disconnected"
        Toast.makeText(applicationContext, "Bluetooth turned off", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bReceiver)
    }
}