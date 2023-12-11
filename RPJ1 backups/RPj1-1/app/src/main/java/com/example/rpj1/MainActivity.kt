package com.example.rpj1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Double.max
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {


    var myVars = vars()
    val myUtils= utils()
    lateinit var blescanner: BluetoothLeScanner
    var blegatt: BluetoothGatt? = null


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    //######################################################################################################################################################################


    fun scan(bluetoothAdapter: BluetoothAdapter, bluetoothManager: BluetoothManager){
        if (bluetoothAdapter != null && bluetoothManager != null && bluetoothAdapter.bluetoothLeScanner != null) {



            blescanner = bluetoothAdapter.bluetoothLeScanner

            Log.d("MyApp", "Bluetooth is enabled.")

            if (myVars.isscanning == true && myVars.connected != true){
                blescanner.stopScan(scanCallback)
                myVars.isscanning = false
            }

            val scanFilter = ScanFilter.Builder()
                .setDeviceName("noscope")
                .build()
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            blescanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
            Log.d("LOG", "Scanning Started")
        }
        else{



            Log.d(
                "LOG",
                bluetoothAdapter.toString() + bluetoothManager.toString() + "something ==== null ===================================================="
            )

        }
    }




    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val device = result.device
            Log.d("UUIDS:", device.name.toString())
            blescanner.stopScan(this)
            device.connectGatt(this@MainActivity, false, gattCallback)
        }
    }


    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                myVars.connected = true
                runOnUiThread {
                    findViewById<TextView>(R.id.connectionstate).text =
                        Editable.Factory.getInstance().newEditable("Connected")
                }
                Log.d("LOG", "Connected to GATT server.")
                blegatt = gatt
                Log.d("Services:", gatt?.discoverServices().toString())

                // Discover services
                //gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                myVars.connected = false
                runOnUiThread {
                    findViewById<TextView>(R.id.connectionstate).text =
                        Editable.Factory.getInstance().newEditable("Disconnected")
                }
                Log.d("LOG", "Disconnected from GATT server.")

            }
        }
    }





    fun send(data:ByteArray){


        val serviceUuidstring = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
        val characteristicUuidstring = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"

        val serviceUuid = UUID.fromString(serviceUuidstring)
        val characteristicUuid = UUID.fromString(characteristicUuidstring)

        val service = blegatt?.getService(serviceUuid)

        val characteristic = service?.getCharacteristic(characteristicUuid)
        characteristic?.value = data

        val writeSuccess = blegatt?.writeCharacteristic(characteristic)
        if (writeSuccess == true) {
            Log.d("Characteristic:", "Data sent successfully for Characteristic UUID: $characteristicUuid")
        }
        else if (writeSuccess == false) {
            Log.d("Characteristic:", "Failed to send data for Characteristic UUID: $characteristicUuid")
        }
        else {
            Log.d("Characteristic:", "Failed to send data for Characteristic UUID: $characteristicUuid")
        }


    }



    //###############################################################################################################################################################################x



    @SuppressLint("MissingPermission")
    private fun checkLocationPermission(){
        val task : Task<Location> = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener {
            if(it != null){
                myVars.latitude   = it.latitude.toFloat()
                myVars.longtitude = it.longitude.toFloat()
                myVars.altitude   = it.altitude.toFloat()
            }
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

    }





    override fun onStart() {
        super.onStart()



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return


        }

    }





    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()



        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        myVars.planet = findViewById(R.id.planet_name)


        val referencia1 = findViewById<EditText>(R.id.referencia1)
        val referencia2 = findViewById<EditText>(R.id.referencia2)
        val referencia3 = findViewById<EditText>(R.id.referencia3)

        myVars.planet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

                if (myVars.objects.contains(s.toString().lowercase())) {

                    myVars.planet.setTextColor(Color.WHITE)
                    val textView = findViewById<TextView>(R.id.latitude)
                    textView.text = ""
                    myVars.loop = true



                    currentScope.launch(Dispatchers.IO) {
                        async {
                            while (myVars.loop) {

                                var planet =
                                    findViewById<TextView>(R.id.planet_name).text.toString()

                                val (newLatitude, newLongitude, mag) = myUtils.calculate_azi(
                                    planet,
                                    myVars.latitude,
                                    myVars.longtitude,
                                    myVars.altitude
                                )

                                myVars.lat1 = newLatitude.toFloat()
                                myVars.long1 = newLongitude.toFloat()


                                runOnUiThread {
                                    val latitudeview = findViewById<TextView>(R.id.latitude)
                                    latitudeview.text = "latitude   : " + myVars.lat1.toString()

                                    val longtitudeview = findViewById<TextView>(R.id.longtitude)
                                    longtitudeview.text = "longtitude: " + myVars.long1.toString()

                                    val magnitude = findViewById<TextView>(R.id.mag)
                                    if (mag == 0.0f) {
                                        magnitude.text = "unknown"
                                    } else {
                                        magnitude.text = "magnitude  : " + mag.toString()
                                    }
                                }
                                if (myVars.lat1 > 10 && myVars.connected) {

                                    myVars.rozdiel1 =
                                        (myVars.lat1.toDouble() - myVars.orientacia[0].toDouble()).toFloat()
                                    Log.d("MyApp", myVars.rozdiel1.toString() + "rozdiel")

                                    myVars.rozdiel2 =
                                        (myVars.long1.toDouble() - myVars.orientacia[1].toDouble()).toFloat()
                                    Log.d("MyApp", myVars.rozdiel2.toString() + "rozdiel")




                                    if (myVars.rozdiel1 > 0.25 || myVars.rozdiel2 > 0.25 || myVars.rozdiel1 < -0.25 || myVars.rozdiel2 < -0.25) {




                                        var string = String.format(
                                            "%.3f",
                                            myVars.rozdiel1
                                        ) + " " + String.format("%.3f", myVars.rozdiel2)
                                        Log.d("Data to be transmitted", string)


                                        send(string.toByteArray())

                                        myVars.orientacia[0] = myVars.lat1
                                        myVars.orientacia[1] = myVars.long1

                                        Log.d(
                                            "MyApp",
                                            "motor started ======================================================================="
                                        )


                                    }

                                } else {
                                    runOnUiThread {
                                        if (myVars.lat1 < 10) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Objekt je pod horizontom",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            runOnUiThread {

                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Device Disconnected.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                                delay(10000)
                            }

                        }
                    }
                }





                    else {
                        myVars.loop = false
                        myVars.planet.setTextColor(Color.RED)

                        val latitudeview = findViewById<TextView>(R.id.latitude)
                        latitudeview.text = "wrong name"
                        val longtitudeview = findViewById<TextView>(R.id.longtitude)
                        longtitudeview.text = ""
                        val magnitude = findViewById<TextView>(R.id.mag)
                        magnitude.text = ""
                    }

            }

            override fun afterTextChanged(s: Editable?) {
            }



        })






        val myButton = findViewById<Button>(R.id.connect)
        myButton.setOnClickListener {
            myUtils.checkAndRequestPermissions(activity = this)
            // Do something when the button is clicked
            scan(bluetoothAdapter, bluetoothManager)
        }



        val zamerané1 = findViewById<Button>(R.id.zamerané1)
        zamerané1.setOnClickListener{
            var planet = findViewById<EditText>(R.id.referencia1).text.toString()

            if(myVars.objects.contains(planet.lowercase()))
            {
               var vysledok = myUtils.calculate_azi(planet, myVars.latitude, myVars.longtitude, myVars.altitude)
                    myVars.orientacia = floatArrayOf(vysledok.first,vysledok.second)
                    Log.d("MyApp", myVars.orientacia.toString() + "orientacia")
                    Toast.makeText(this@MainActivity, "Zamerané", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity, "Nerozoznaný objekt", Toast.LENGTH_SHORT).show()
                }
                }



        val zamerané2 = findViewById<Button>(R.id.zamerané2)
        zamerané2.setOnClickListener{

            var planet = findViewById<EditText>(R.id.referencia2).text.toString()

            if(myVars.objects.contains(planet.lowercase())){

                    val vysledok = myUtils.calculate_azi(planet, myVars.latitude, myVars.longtitude, myVars.altitude)
                    val priemer: FloatArray = floatArrayOf((vysledok.first + myVars.orientacia[0]) / 2,(vysledok.second + myVars.orientacia[1])/2)
                    myVars.orientacia = priemer
                    Log.d("MyApp", myVars.orientacia[0].toString() + "=" +  myVars.orientacia[1].toString() + "Priemer =======================================================================================================")
                    Toast.makeText(this@MainActivity, "Zamerané", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity, "Nerozoznaný objekt", Toast.LENGTH_SHORT).show()
                }

        }

        val zamerané3 = findViewById<Button>(R.id.zamerané3)
        zamerané3.setOnClickListener{
            var planet = findViewById<EditText>(R.id.referencia3).text.toString()
            if(myVars.objects.contains(planet.lowercase())){


                    val vysledok            = myUtils.calculate_azi(planet, myVars.latitude, myVars.longtitude, myVars.altitude)
                    val priemer: FloatArray = floatArrayOf((vysledok.first + myVars.orientacia[0]) / 2,(vysledok.second + myVars.orientacia[1])/2)
                    myVars.orientacia       = priemer
                    Log.d("MyApp", myVars.orientacia[0].toString() + "=" +  myVars.orientacia[1].toString() + "Priemer =======================================================================================================")
                    Toast.makeText(this@MainActivity, "Zamerané", Toast.LENGTH_SHORT).show()

                }
                else {
                    Toast.makeText(this@MainActivity, "Nerozoznaný objekt", Toast.LENGTH_SHORT).show()
                }


        }


//=====================================================================================================================================

        var preciznost = findViewById<EditText> (R.id.preciznost)
        preciznost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
                myVars.presnost = s?.toString()?.toFloatOrNull() ?: 0f
            }
        })

        val hore = findViewById<ImageButton>(R.id.hore)
        hore.setOnClickListener{
            if (myVars.orientacia[0] < 85-myVars.presnost){
                myVars.orientacia[0] += myVars.presnost
                var string =    myVars.presnost.toString()+" 0.0"
                Log.d("Data to be transmitted",string)
                send(string.toByteArray())
            }
            Log.d("MyApp", "lat = " +  myVars.orientacia[0] + ", long = " + myVars.orientacia[1])
        }

        val dole = findViewById<ImageButton>(R.id.dole)
        dole.setOnClickListener {
            //latitude

            if (myVars.orientacia[0] > -5+myVars.presnost) {
                myVars.orientacia[0] -= myVars.presnost
                var string = (-1*myVars.presnost).toString()+" 0.0"
                Log.d("Data to be transmitted",string)
                send(string.toByteArray())
                Log.d("MyApp", "lat = " + myVars.orientacia[0] + ", long = " + myVars.orientacia[1])
            }
        }



        val vlavo = findViewById<ImageButton>(R.id.vlavo)
        vlavo.setOnClickListener {
            if (myVars.orientacia[1] > myVars.presnost){
                myVars.orientacia[1] -= myVars.presnost


                var string = "0.0 " + (-1*myVars.presnost).toString()
                Log.d("Data to be transmitted",string)
                send(string.toByteArray())

            }
            else{
                myVars.orientacia[1] = 360 + (myVars.orientacia[0] - myVars.presnost)
            }
            Log.d("MyApp", "lat = " +  myVars.orientacia[0] + ", long = " + myVars.orientacia[1])
        }

        val vpravo = findViewById<ImageButton>(R.id.vpravo)
        vpravo.setOnClickListener {
            if (myVars.orientacia[1] < 360-myVars.presnost){
                myVars.orientacia[1] += myVars.presnost


                var string = "0.0 " + myVars.presnost.toString()
                Log.d("Data to be transmitted",string)
                send(string.toByteArray())
            }
            else{
                myVars.orientacia[1] = (360-myVars.orientacia[1])-myVars.presnost
            }
            Log.d("MyApp", "lat = " +  myVars.orientacia[0] + ", long = " + myVars.orientacia[1])

        }




        referencia1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (myVars.objects.contains(s.toString().lowercase())){
                    referencia1.setTextColor(Color.WHITE)
                }
                else{
                    referencia1.setTextColor(Color.RED)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        referencia2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (myVars.objects.contains(s.toString().lowercase())){
                    referencia2.setTextColor(Color.WHITE)
                }
                else{
                    referencia2.setTextColor(Color.RED)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed
            }
        })

        referencia3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (myVars.objects.contains(s.toString().lowercase())){
                    referencia3.setTextColor(Color.WHITE)
                }
                else{
                    referencia3.setTextColor(Color.RED)
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })



    }


}

