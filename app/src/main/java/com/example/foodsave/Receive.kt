package com.example.foodsave

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.example.foodsave.databinding.ReceiveScreenBinding
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
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

class Receive : Fragment(), OnMapReadyCallback {

    private var _binding: ReceiveScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var fStore: FirebaseFirestore
    private lateinit var receiverName: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null
    private lateinit var mMap: GoogleMap
    private val cloudstorage: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReceiveScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.searchButton.isEnabled = false
        binding.searchButton.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            android.R.color.darker_gray
        )

        // Add a TextWatcher to the search text field
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                // Enable the search button if there is text in the search field, otherwise disable it
                binding.searchButton.isEnabled = !s.isNullOrBlank()
                // Change button color based on text input
                if (s.isNullOrBlank()) {
                    binding.searchButton.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(),
                        android.R.color.darker_gray
                    )
                } else {
                    // Change it back to the original color
                    binding.searchButton.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.register
                    )
                }
            }
        })

        binding.searchButton.setOnClickListener {
            val searchQuery = binding.searchText.text.toString()
            searchDonationDetails(searchQuery)
        }

        binding.submit.isEnabled = false

        binding.submit.setOnClickListener {
            val foodItem = binding.searchText.text.toString()
            val description = binding.descriptionText.text.toString()
            val expiry = binding.expiryText.text.toString()
            // receiverName = binding.receiverNameText.text.toString()
            updateDonationStatus(foodItem, description, expiry)

            findNavController().navigate(R.id.action_Receive_to_Dashboard)

        }
    }

    private fun searchDonationDetails(searchQuery: String) {
        fStore.collection("donateRecords")
            .whereEqualTo("foodItem", searchQuery)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.descriptionText.setText("Item not found")
                    binding.expiryText.setText("Expiry not found")
                } else {
                    for (document in documents) {
                        val description = document.getString("description")
                        val expiry = document.getString("expiry")
                        binding.descriptionText.setText(description)
                        binding.expiryText.setText(expiry)
                    }
                }

                binding.submit.isEnabled = true // Enable submit button
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun updateDonationStatus(foodItem: String, description: String, expiry: String) {
        val orderHistoryRef = fStore.collection("orderHistory").document()
        val userID = firebaseAuth.currentUser?.uid

        val userRef = fStore.collection("users").document(userID.toString())

        fStore.collection("donateRecords")
            .whereEqualTo("foodItem", foodItem)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val documentRef = document.reference

                    val updateData = mapOf(
                        "isAvailable" to false
                    )

                    documentRef.update(updateData)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Donation record updated")
                        }
                        .addOnFailureListener { e ->
                            Log.e(ContentValues.TAG, "Error Updating Donation record: $e")
                        }
                } else {
                    Log.e(ContentValues.TAG, "Donation record not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error getting donation record: $e")
            }

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val receiver = documentSnapshot.get("name")}
        // Create a map with the order details
        val orderDetails = hashMapOf(
            "foodItem" to foodItem,
            "description" to description,
            "expiry" to expiry,
            "userId" to userID
        )

        // Set the order details in the document
        orderHistoryRef.set(orderDetails)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Order History Updated")
                Toast.makeText(requireActivity(), "Order History Updated.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Order History failed to Updated")
                Toast.makeText(requireActivity(), "Order History failed to Updated\"", Toast.LENGTH_SHORT).show()
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ReceiveFragment"
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
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_GREEN))
                            )
                        }
                    }
                } else {
                    Log.d(TAG, "Error fetching data: ", task.exception)
                }
            }
    }
}