package com.example.tournamaker.ui.auth

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tournamaker.R
import com.example.tournamaker.databinding.FragmentRegisterBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.RegisterViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { user ->
                    authManager.setUser(user)
                    showToast("¡Registro exitoso! Bienvenido ${user.name}")
                    findNavController().navigate(R.id.action_registerFragment_to_landingFragment)
                },
                onFailure = { error ->
                    showToast(error.message ?: "Error al registrarse")
                }
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.btnRegister.isEnabled = false
            } else {
                binding.progressBar.hide()
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.register(username, name, email, password)
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvGoToLogin.setOnHoverListener { v, event ->
            val textView = v as TextView
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    true
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    textView.paintFlags = textView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}