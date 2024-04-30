package com.example.foodsave

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.foodsave.databinding.ActivityFeedbackFormBinding

class FeedbackForm : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the dropdown menus
        setupDropdowns()

        // Set up the submit button click listener
        binding.btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun setupDropdowns() {
        // Set up the donor rating dropdown
        ArrayAdapter.createFromResource(
            this,
            R.array.donor_ratings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDonorRating.adapter = adapter
            binding.spinnerDonorRating.setSelection(0) // Set the default selection to "Choose from below"
        }

        // Set up the food quality dropdown
        ArrayAdapter.createFromResource(
            this,
            R.array.food_quality_ratings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFoodQuality.adapter = adapter
            binding.spinnerFoodQuality.setSelection(0) // Set the default selection to "Choose from below"
        }
    }

    private fun submitFeedback() {
        val donorName = binding.etDonorName.text.toString()
        val foodItem = binding.etFoodItem.text.toString()
        val donorRating = binding.spinnerDonorRating.selectedItemPosition // Get the selected index
        val foodQuality = binding.spinnerFoodQuality.selectedItemPosition // Get the selected index

        // Perform the necessary actions to submit the feedback
        // You can use the `donorRating` and `foodQuality` values (which will be 0 if "Choose from below" is selected)
    }
}