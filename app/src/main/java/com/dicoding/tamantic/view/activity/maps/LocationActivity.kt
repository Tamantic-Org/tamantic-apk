package com.dicoding.tamantic.view.activity.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.Alamat
import com.dicoding.tamantic.databinding.ActivityLocationBinding
import com.dicoding.tamantic.view.activity.alamat.AlamatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityLocationBinding
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupView()
        setupAction()
    }

    private fun setupAction() {

        binding.actionToAlamat.setOnClickListener {
            val intent = Intent(this, AlamatActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSaveAddress.setOnClickListener {
            selectedMarker?.let { marker ->
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0)
                        showAddressAlert(address)
                    } else {
                        showAddressAlert("Alamat tidak ditemukan")
                    }
                }
            } ?: showAddressAlert("Tidak ada lokasi yang di pilih")
        }

    }

    private fun showAddressAlert(address: String) {
        AlertDialog.Builder(this)
            .setTitle("Alamat")
            .setMessage(address)
            .setPositiveButton("Tambahkan Alamat") { dialog, _ ->
                addAddress(address)
                dialog.dismiss()
            }
            .show()
    }

    private fun addAddress(address: String) {
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/address/$fromId/").push()

        ref.get().addOnSuccessListener { snapshot ->
            val alamat = Alamat(ref.key!!, address)
            ref.setValue(alamat).addOnSuccessListener {
                popupAlertSuccess("Alamat ditambahkan")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        val areaSpace = LatLng(-6.216950169133128, 106.78094379011524)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(areaSpace, 10f))

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isIndoorLevelPickerEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        map.setOnMapClickListener { latLng ->
            selectedMarker?.remove()
            selectedMarker =
                map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        getMyLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun popupAlertSuccess(msg: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            dialog.dismiss()
        }
        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Berhasil"
        message.text = msg
    }

    private fun popupAlertFailed(errorMessage: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            dialog.dismiss()
        }

        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Gagal"
        message.text = errorMessage
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }

}