package com.example.foodsave

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Rewards : Fragment(){

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rewards, container, false)
    }

    private lateinit var imageSwitcher: ImageSwitcher
    private var currentBadgeIndex = 0
    private val badges = listOf(
        R.drawable.badge1,
        R.drawable.badge2,
        R.drawable.badge3,
        R.drawable.badge4,
        R.drawable.badge5,
        // Add more badge images here
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userRef = fStore.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val currentPoints = documentSnapshot.getLong("rewards")?.toInt() ?: 0
                updatePointsDisplay(view, currentPoints)

                view.findViewById<Button>(R.id.redeemButton).setOnClickListener {
                    reducePointsAndUpdate(userRef, currentPoints, view)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Rewards failed to fetch")
            }

        imageSwitcher = view.findViewById(R.id.badgeImage_switcher)
        imageSwitcher.setFactory {
            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        imageSwitcher.setImageResource(badges[currentBadgeIndex])

        view.findViewById<Button>(R.id.nextBadgeButton).setOnClickListener {
            showNextBadge()
        }
    }

    private fun reducePointsAndUpdate(userRef: DocumentReference, currentPoints: Int, view: View) {
        val newPoints = currentPoints - 100
        if (newPoints >= 0) {
            userRef.update("rewards", newPoints)
                .addOnSuccessListener {
                    updatePointsDisplay(view, newPoints)
                    Toast.makeText(requireContext(), "Points redeemed successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Failed to update points: $exception")
                }
        } else {
            Toast.makeText(requireContext(), "Insufficient points!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNextBadge() {
        currentBadgeIndex = (currentBadgeIndex + 1) % badges.size
        imageSwitcher.setImageResource(badges[currentBadgeIndex])
    }


    private fun updatePointsDisplay(view: View, points: Int) {

        view.findViewById<TextView>(R.id.numberDisplay)?.text = points.toString()
    }
}