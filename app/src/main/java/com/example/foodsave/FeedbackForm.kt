package com.example.foodsave

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodsave.databinding.ActivityFeedbackFormBinding

class FeedbackForm : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdowns()
        setupSubmitButton()
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

    private fun setupSubmitButton() {
        binding.btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val donorName = binding.etDonorName.text.toString()
        val foodItem = binding.etFoodItem.text.toString()
        val donorRating = binding.spinnerDonorRating.selectedItemPosition
        val foodQuality = binding.spinnerFoodQuality.selectedItemPosition

        if (donorRating == 0) {
            // "Choose from below" is selected for Donor Rating
        }

        if (foodQuality == 0) {
            // "Choose from below" is selected for Food Quality
        }

        // Perform the necessary actions to submit the feedback
        // You can use the `donorRating` and `foodQuality` values (which will be 0 if "Choose from below" is selected)
        Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()

        finish()
    }
}