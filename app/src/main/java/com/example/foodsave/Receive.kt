package com.example.foodsave

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodsave.databinding.ReceiveScreenBinding
import com.google.firebase.firestore.FirebaseFirestore

class Receive : Fragment() {

    private var _binding: ReceiveScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var fStore: FirebaseFirestore

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
    }

    private fun searchDonationDetails(searchQuery: String) {
        fStore.collection("donateRecords")
            .whereEqualTo("foodItem", searchQuery)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.descriptionText.setText("Item not found")
                } else {
                    for (document in documents) {
                        val description = document.getString("description")
                        binding.descriptionText.setText(description)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
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