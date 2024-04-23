package com.example.foodsave

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.foodsave.databinding.SignUpBinding
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SignUp : Fragment() {

    private var _binding: SignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignUpBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (validateInputs(name, email,phone, password)) {
                // Firebase Auth
             firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(requireActivity()) {
        if(it.isSuccessful){
            Toast.makeText(requireActivity() , "User Created!", Toast.LENGTH_SHORT).show()

            val userID = firebaseAuth.currentUser?.uid

            if (userID != null) {
                val documentReference = fStore.collection("users").document(userID)

                val user = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone
                )
                documentReference.set(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "User profile created for userID: $userID")
                        Toast.makeText(requireActivity(), "Registered Successfully.", Toast.LENGTH_SHORT).show()

                        // Navigate to the next screen or fragment
                        findNavController().navigate(R.id.action_SignUp_to_SignIn)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error setting user data: ${e.message}")
                        Toast.makeText(requireActivity(), "Error setting user data.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Log.e(TAG, "User ID is null.")
                Toast.makeText(requireActivity(), "User ID is null.", Toast.LENGTH_SHORT).show()
            }

           // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }else {
           Toast.makeText(requireActivity() , "Please provide correct email!", Toast.LENGTH_SHORT).show()
        }
             }
            }
        }

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_SignUp_to_SignIn)

        }
    }

    private fun validateInputs(name: String, email: String, phone: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.nameError.error = "Name is required"
            return false
        }

        if (email.isEmpty()) {
            binding.emailError.error = "Email is required"
            return false
        }

        if (phone.isEmpty()) {
            binding.phoneError.error = "Phone no is required"
            return false
        }

        if (password.isEmpty()) {
            binding.passError.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.passError.error = "Password must be at least 6 characters"
            return false
        }
        if (phone.length != 10) {
            binding.passError.error = "Phone No must be 10 digits"
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
