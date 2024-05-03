package com.example.foodsave


import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.foodsave.databinding.ActivityFeedbackFormBinding
import com.bumptech.glide.Glide

class FeedbackForm : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackFormBinding
    private lateinit var imageUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Set the app bar title
        val title = "Feedback Form"
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, title.length, 0)
        supportActionBar?.title = spannableTitle

        // Enable the back button in the app bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, android.R.color.black))
        imageUrl = "https://media.gettyimages.com/id/1457889029/photo/group-of-food-with-high-content-of-dietary-fiber-arranged-side-by-side.jpg?s=612x612&w=0&k=20&c=SEyObHsbBsrd1XZlgEg389VT86BMFKZKfKReKyVPAk4="

        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageView)

        setupDropdowns()
        setupSubmitButton()

        // Disable the submit button initially
        binding.btnSubmitFeedback.isEnabled = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
            binding.spinnerDonorRating.setSelection(0) // Set the default selection to the first item (0)
        }

        // Set up the food quality dropdown
        ArrayAdapter.createFromResource(
            this,
            R.array.food_quality_ratings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFoodQuality.adapter = adapter
            binding.spinnerFoodQuality.setSelection(0) // Set the default selection to the first item (0)
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }

        // Enabled the submit button when the user has filled out the form
        binding.etDonorName.addTextChangedListener {
            enableSubmitButton()
        }
        binding.etFoodItem.addTextChangedListener {
            enableSubmitButton()
        }
        binding.spinnerDonorRating.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                enableSubmitButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinnerFoodQuality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                enableSubmitButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun enableSubmitButton() {
        val donorName = binding.etDonorName.text.toString().isNotEmpty()
        val foodItem = binding.etFoodItem.text.toString().isNotEmpty()
        val donorRating = binding.spinnerDonorRating.selectedItemPosition != 0
        val foodQuality = binding.spinnerFoodQuality.selectedItemPosition != 0

        binding.btnSubmitFeedback.isEnabled = donorName && foodItem && donorRating && foodQuality
    }

    private fun submitFeedback() {
        Toast.makeText(this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
        onBackPressed()


    }
}