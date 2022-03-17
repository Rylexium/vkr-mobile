package com.example.vkr.activity.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vkr.R
import com.example.vkr.databinding.GoogleMapsActivityBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: GoogleMapsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GoogleMapsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val arena = LatLng(53.21367303980368, 50.17687812145955)
        val northArena = LatLng(53.212010990171, 50.17739215331539)
        val southArena = LatLng(53.22265361550286, 50.17283051851983)
        mMap.addMarker(MarkerOptions().position(arena).title("Приёмная комиссия"))
        mMap.addMarker(MarkerOptions().position(northArena).title("Самарский университет"))
        mMap.addMarker(MarkerOptions().position(southArena).title("Самарский университет"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arena, 16f))
    }
}