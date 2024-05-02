package com.example.foodsave

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.foodsave.databinding.LoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.bumptech.glide.Glide

class Login : Fragment() {

    private var _binding: LoginScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var imageUrl:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginScreenBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        imageUrl = "https://st4.depositphotos.com/3457277/26637/i/450/depositphotos_266372298-stock-photo-zero-waste-vegetables-and-food.jpg"

        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageView)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_SignIn_to_Dashboard)
                        } else {
                            val errorMessage = when (task.exception) {
                                is FirebaseAuthInvalidUserException -> "User not found. Please check your email."
                                is FirebaseAuthInvalidCredentialsException -> "Invalid password. Please try again."
                                else -> "Login failed. Please try again."
                            }
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Login", "Login failed: $errorMessage")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            requireContext(),
                            "Login failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Login", "Login failed: ${exception.message}")
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter email and password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}