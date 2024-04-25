package com.example.foodsave

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodsave.databinding.DonateScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

class Donate : Fragment() {

    private var _binding: DonateScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DonateScreenBinding.inflate(inflater, container, false)
        return binding.root
       // return inflater.inflate(R.layout.donate_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.submit.setOnClickListener {
            val dFullName = binding.donorname.text.toString().trim()
            val dFoodItem = binding.fooditem.text.toString().trim()
            val dPhone = binding.phone.text.toString().trim()
            val dDescription = binding.description.text.toString().trim()

            if (validateInputs(dFullName, dFoodItem,dPhone, dDescription)) {
                        val userID = firebaseAuth.currentUser?.uid

                        if (userID != null) {
                            val documentReference = fStore.collection("donateRecords").document(userID)

                            val record = hashMapOf(
                                "name" to dFullName,
                                "email" to dFoodItem,
                                "phone" to dPhone,
                                "description" to dDescription,
                                "userId" to userID,
                                "location" to GeoPoint(30.588730, -87.163773),
                                "timeStamp" to Timestamp.now()
                            )
                            documentReference.set(record)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Donate Record created for userID: $userID")
                                    Toast.makeText(requireActivity(), "Added Successfully.", Toast.LENGTH_SHORT).show()

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

                        // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }else {
                        Toast.makeText(requireActivity() , "Please provide correct email!", Toast.LENGTH_SHORT).show()
            }
        }
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

        if (dDescription.isEmpty()) {
            binding.descriptionError.error = "Description is required"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}