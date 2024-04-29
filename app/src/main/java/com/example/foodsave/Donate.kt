package com.example.foodsave

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodsave.databinding.DonateScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.Calendar

class Donate : Fragment(), OnMapReadyCallback {

    private var _binding: DonateScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DonateScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.expiry.setOnClickListener {
            showDatePickerDialog()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.submit.setOnClickListener {
            val dFullName = binding.donorname.text.toString().trim()
            val dFoodItem = binding.fooditem.text.toString().trim()
            val dPhone = binding.phone.text.toString().trim()
            val dDescription = binding.description.text.toString().trim()
            val dExpiry = binding.expiry.text.toString().trim()

            if (validateInputs(dFullName, dFoodItem, dPhone, dDescription)) {
                val userID = firebaseAuth.currentUser?.uid

                if (userID != null) {
                    val collectionReference = fStore.collection("donateRecords")

                    val record = hashMapOf(
                        "name" to dFullName,
                        "foodItem" to dFoodItem,
                        "isAvailable" to true,
                        "phone" to dPhone,
                        "description" to dDescription,
                        "expiry" to dExpiry,
                        "userId" to userID,
                        "location" to GeoPoint(40.71144, -74.00514),
                        //"location" to GeoPoint(currentLatLng!!.latitude, currentLatLng!!.longitude),
                        "timeStamp" to Timestamp.now()
                    )
                    collectionReference.add(record)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Donate Record created for userID: $userID")
                            Toast.makeText(requireActivity(), "Added Successfully.", Toast.LENGTH_SHORT).show()

                            //Adding 50 Points to user for donating food
                            updateRewardsPoints(userID)

                            // Navigate to the next screen or fragment
                            findNavController().navigate(R.id.action_Donate_to_Dashboard)
                        }
                        .addOnFailureListener { e ->
                            Log.e(ContentValues.TAG, "Error setting donate record: ${e.message}")
                            Toast.makeText(requireActivity(), "Error setting donate record.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e(ContentValues.TAG, "User ID is null.")
                    Toast.makeText(requireActivity(), "User ID is null.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireActivity(), "Error while saving data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.expiry.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun validateInputs(dFullName: String, dFoodItem: String, dPhone: String, dDescription: String): Boolean {
        if (dFullName.isEmpty()) {
            binding.nameError.error = "Name is required"
            return false
        }

        if (dFoodItem.isEmpty()) {
            binding.itemError.error = "Item is required"
            return false
        }

        if (dPhone.isEmpty()) {
            binding.phoneError.error = "Phone no is required"
            return false
        }
        if (dPhone.length != 10) {
            binding.phoneError.error = "Phone No must be 10 digits"
            return false
        }

        if (dDescription.isEmpty()) {
            binding.descriptionError.error = "Description is required"
            return false
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
                        // currentLatLng = LatLng(location.latitude, location.longitude)
                        currentLatLng = LatLng(40.71144, -74.00514)

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
                            .zoom(15f)
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

    private fun updateRewardsPoints(userID: String) {
        val userRef = fStore.collection("users").document(userID)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val currentPoints = documentSnapshot.getLong("rewards") ?: 0
                val newPoints = currentPoints + 50

                val updateData = mapOf(
                    "rewards" to newPoints
                )
                userRef.update(updateData)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Rewards updated for userID: $userID")
                        Toast.makeText(requireActivity(), "50 Points in Rewards Added Successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e(ContentValues.TAG, "Error updating rewards: ${e.message}")
                        Toast.makeText(requireActivity(), "Error updating rewards.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error getting user document: ${e.message}")
                Toast.makeText(requireActivity(), "Error getting user document.", Toast.LENGTH_SHORT).show()
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
