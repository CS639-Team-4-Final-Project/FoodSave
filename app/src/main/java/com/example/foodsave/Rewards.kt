package com.example.foodsave

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Rewards : Fragment(){

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

    private fun showNextBadge() {
        currentBadgeIndex = (currentBadgeIndex + 1) % badges.size
        imageSwitcher.setImageResource(badges[currentBadgeIndex])
    }





}


