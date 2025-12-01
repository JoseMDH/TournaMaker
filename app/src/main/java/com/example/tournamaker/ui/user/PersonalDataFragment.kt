package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tournamaker.databinding.FragmentPersonalDataBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.showToast

class PersonalDataFragment : Fragment() {

    private var _binding: FragmentPersonalDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val user = authManager.getUser()
        if (user != null) {
            binding.etName.setText(user.name)
            binding.etUsername.setText(user.username)
            binding.etEmail.setText(user.email)
        } else {
            // Si no hay usuario, no deberíamos estar aquí, pero por seguridad...
            showToast("Error: Usuario no encontrado")
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnChangePassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                sendPasswordReset(email)
            } else {
                showToast("No se ha podido encontrar el email del usuario.")
            }
        }
    }

    private fun sendPasswordReset(email: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnChangePassword.isEnabled = false

        authManager.sendPasswordResetEmail(email) {
            binding.progressBar.visibility = View.GONE
            binding.btnChangePassword.isEnabled = true
            showToast("Se ha enviado un correo a $email para restablecer la contraseña.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}