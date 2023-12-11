package com.example.rpj1

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import java.util.*


class utils {
    val myVars = vars()

    fun calculate_azi(planet: String,latitude: Float,longtitude: Float,altitude: Float): Triple<Float, Float, Float>{

        val lat1    :  Double
        val long1   :  Double
        val mag     :  Double

        val python = com.chaquo.python.Python.getInstance()

        val module = python.getModule("uhly")
        val result = module.callAttr("planet_coordinate", planet, latitude,longtitude,altitude).toString()



        val str     = result
        val numbers = str
            .substring(1, str.length - 1) // remove the parentheses at the beginning and end
            .split(",") // split the string into an array of substrings
            .map { it.trim().toDouble() } // convert each substring to a double



        lat1    = numbers[0]
        long1   = numbers[1]
        mag     = numbers[2]
        Log.d("MyApp", lat1.toString()+"lat")
        Log.d("MyApp", long1.toString()+"long")

        return Triple(lat1.toFloat(), long1.toFloat(), mag.toFloat())
    }

//#################################################################################################################################

    fun checkAndRequestPermissions(activity: Activity) {


        val notGrantedPermissions = myVars.permissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), 1)
        }


        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBluetoothIntent, 1)

    }

    fun isobject(object_name: String): Boolean{
        return myVars.objects.contains(object_name.lowercase())
    }



}