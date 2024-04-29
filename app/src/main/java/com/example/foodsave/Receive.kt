package com.example.foodsave

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodsave.databinding.ReceiveScreenBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Receive : Fragment() {

    private var _binding: ReceiveScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var fStore: FirebaseFirestore
    private lateinit var receiverName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReceiveScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fStore = FirebaseFirestore.getInstance()

        binding.searchButton.setOnClickListener {
            val searchQuery = binding.searchText.text.toString()
            searchDonationDetails(searchQuery)
        }
        binding.submit.setOnClickListener {
            val foodItem = binding.searchText.text.toString()
            val description = binding.descriptionText.text.toString()
            val expiry = binding.expiryText.text.toString()
            receiverName = binding.receiverNameText.text.toString()
            updateDonationStatus(foodItem, description, expiry, receiverName)
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
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                Log.d(TAG, "Error getting documents: ", exception)

            }
    }

    private fun updateDonationStatus(foodItem: String, description: String, expiry: String, receiverName: String) {
        // Get the document reference for the donation record
        val donationRef = fStore.collection("donateRecords").document(foodItem)

        val donationStatus= hashMapOf(
            "status" to "Received"
        )


        // Create a new document reference for the order history
        val orderHistoryRef = fStore.collection("orderHistory").document()

        // Create a map with the order details
        val orderDetails = hashMapOf(
            "foodItem" to foodItem,
            "description" to description,
            "expiry" to expiry,
            "receiverName" to receiverName,

        )

        deleteAndAddToHistory(donationRef, orderHistoryRef, orderDetails, 3)

    }
    private fun deleteAndAddToHistory(
        donationRef: DocumentReference,
        orderHistoryRef: DocumentReference,
        orderDetails: Map<String, Any>,
        retryCount: Int
    ) {
        donationRef.delete()
            .addOnSuccessListener {
                orderHistoryRef.set(orderDetails)
                    .addOnSuccessListener {
                        findNavController().navigate(R.id.action_Receive_to_Dashboard)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error adding to order history: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                if (retryCount > 0) {
                    Log.d(TAG, "Retrying deletion... Attempts left: $retryCount")
                    deleteAndAddToHistory(donationRef, orderHistoryRef, orderDetails, retryCount - 1)
                } else {
                    Log.d(TAG, "Error deleting donation record: ", exception)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ReceiveFragment"
    }
}