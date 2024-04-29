package com.example.foodsave

import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodsave.databinding.FoodMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class FoodMap : Fragment(), OnMapReadyCallback {

    private var _binding: FoodMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null

    private lateinit var mLastLocation: ExerciseRoute.Location
    private val cloudstorage: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val TAG = "FoodMap"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FoodMapBinding.inflate(inflater, container, false)
        val view = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocation()

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Enable location button on the map
            mMap.isMyLocationEnabled = true

            // Get last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Check if location is not null
                    if (location != null) {
                     //   currentLatLng = LatLng(location.latitude, location.longitude)
                        currentLatLng= LatLng(40.71144, -74.00514)
                        // Add a marker at the current location
                        mMap.addMarker(
                            MarkerOptions()
                                .position(currentLatLng!!)
                                .title("My Location")
                                .snippet("Current Location")
                        )

                        // Move the camera to the current location
                        val cameraPosition = CameraPosition.Builder()
                            .target(currentLatLng!!)
                            .zoom(13f)
                            .build()
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to get current location.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                requireContext(),
                "Location permission not granted.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showLocation() {
        cloudstorage.collection("donateRecords")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        if (document.contains("location") && document.contains("name") && document.contains("description") && document.get("isAvailable")== true) {
                            val location = document.getGeoPoint("location")
                            val title = document.getString("name")
                            val foodItem = document.getString("foodItem")
                            val expiry = document.getString("expiry")
                            val description = document.getString("description")

                                Log.d(TAG, "$location Success $title")
                                val latLng = LatLng(location!!.latitude, location.longitude)
                                mMap.addMarker(
                                    MarkerOptions().position(latLng)
                                        .title("$title ($foodItem) :$expiry")
                                        .snippet(description)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                )
                        }
                    }
                } else {
                    Log.d(TAG, "Error fetching data: ", task.exception)
                }
            }
    }
}