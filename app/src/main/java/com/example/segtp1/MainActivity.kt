package com.example.segtp1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSIONS_CODE = 128
    var nomeArq = ""
    var dadosArq = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegistar.setOnClickListener {
            callAccessLocation(null)
        }

        btnListar.setOnClickListener {
            carregarLista()
        }
    }

    private val locationListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                dadosArq = "${convertToDms(location.latitude,location.longitude)}"
                nomeArq = "${currentDate}.crq"

                callWriteOnSDCard(null)
            }
            override fun onStatusChanged(
                provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    fun callAccessLocation(view: View?) {
        val permissionAFL = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionACL = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionAFL != PackageManager.PERMISSION_GRANTED &&
            permissionACL != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("É preciso liberar ACCESS_FINE_LOCATION",
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_CODE)
            }
        } else {
            readMyCurrentCoordinates()
        }
    }

    private fun callDialog(mensagem: String,
                           permissions: Array<String>) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this@MainActivity, permissions,
                    REQUEST_PERMISSIONS_CODE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    private fun readMyCurrentCoordinates() {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.d("Permissao", "Ative os serviços necessários")
        } else {
            if (isGPSEnabled) {
                try {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null)
                } catch(ex: SecurityException) {
                    Log.d("Permissao", "Security Exception")
                }
            }
            else if (isNetworkEnabled) {
                try {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null)
                } catch(ex: SecurityException) {
                    Log.d("Permissao", "Security Exception")
                }
            }
        }
    }

    private fun createDeleteFile() {
        var path = getExternalFilesDir(null)
        var dir = File(path, "/ARQSLOC")
        if (!dir.exists() || !dir.isDirectory) {
            val arqLoqDirectory = File(path, "ARQSLOC")
            arqLoqDirectory.mkdirs()
            dir = File(path, "/ARQSLOC")
        }
        val file = File(dir, nomeArq)
        if(file.exists()){
            file.delete()
        }
        else{
            try {
                val os: OutputStream = FileOutputStream(file)
                os.write(dadosArq.toByteArray())
                os.close()
                Toast.makeText(this@MainActivity,
                    "Arquivo criado",
                    Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Log.d("Permissao", "Erro de escrita em arquivo")
            }
        }
    }

    private fun callWriteOnSDCard(view: View?) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )) {
                callDialog(
                    "É preciso liberar WRITE_EXTERNAL_STORAGE",
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS_CODE)
            }
        } else {
            createDeleteFile()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                var i = 0
                while (i < permissions.size) {
                    if (permissions[i].equals(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            ignoreCase = true)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        readMyCurrentCoordinates()
                    } else if (permissions[i].equals(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ignoreCase = true)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        createDeleteFile()
                    }
                    i++
                }
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)
    }

    fun carregarLista(){

        var intent = Intent(this,ListaArq::class.java)
        startActivity(intent);
    }

    private fun convertToDms(latitude: Double, longitude: Double): String? {
        val builder = java.lang.StringBuilder()
        if (latitude < 0) {
            builder.append("S ")
        } else {
            builder.append("N ")
        }
        val latitudeDegrees = Location.convert(
            Math.abs(latitude),
            Location.FORMAT_SECONDS
        )
        val latitudeSplit = latitudeDegrees.split(":").toTypedArray()
        builder.append(latitudeSplit[0])
        builder.append("°")
        builder.append(latitudeSplit[1])
        builder.append("'")
        builder.append(latitudeSplit[2])
        builder.append("\"")
        builder.append(" ")
        if (longitude < 0) {
            builder.append("W ")
        } else {
            builder.append("E ")
        }
        val longitudeDegrees = Location.convert(
            Math.abs(longitude),
            Location.FORMAT_SECONDS
        )
        val longitudeSplit = longitudeDegrees.split(":").toTypedArray()
        builder.append(longitudeSplit[0])
        builder.append("°")
        builder.append(longitudeSplit[1])
        builder.append("'")
        builder.append(longitudeSplit[2])
        builder.append("\"")
        return builder.toString()
    }
}
