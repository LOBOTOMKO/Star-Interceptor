package com.example.rpj1

import android.Manifest
import android.widget.EditText
import java.io.Serializable

class vars : Serializable {

    var connected          = false

    lateinit var planet: EditText
    var isscanning         = false

    var latitude:    Float = 48.8744648f
    var longtitude:  Float = 18.0492584f
    var altitude:    Float = 200.0f
    var loop               = false

    var lat1        = 0.0f
    var long1       = 0.0f
    var rozdiel1    = 0.0f
    var rozdiel2    = 0.0f

    //aritmetizácia
    var orientacia: FloatArray = floatArrayOf(0.0f, 0.0f)

    var presnost    = 1f

    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    val objects = listOf("mercury","venus","moon","mars","jupiter","saturn","uranus","neptune","europa","ganymede","callisto","io","titan","enceladus","rhea","dione","tethys","mimas","phobos","deimos","polaris","sirius","betelgeuse","rigel","antares","vega","altair","deneb","spica","arcturus")

}