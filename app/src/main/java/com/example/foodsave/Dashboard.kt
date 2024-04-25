package com.example.foodsave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.foodsave.R
import com.example.foodsave.databinding.DashboardBinding

class Dashboard : Fragment() {

    private var _binding: DashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.Donate.setOnClickListener {
           findNavController().navigate(R.id.action_Dashboard_to_Donate)
       }
        binding.Receive.setOnClickListener {
            findNavController().navigate(R.id.action_Dashboard_to_Receive)
        }
        binding.Rewards.setOnClickListener {
            findNavController().navigate(R.id.action_Dashboard_to_Rewards)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
