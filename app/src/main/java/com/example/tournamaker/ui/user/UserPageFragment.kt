package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tournamaker.R
import com.example.tournamaker.databinding.FragmentUserPageBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.loadImage

class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
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
            binding.tvUsername.text = user.username
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.ivAvatar.loadImage(user.avatar)
        } else {
            navigateToLogin()
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            authManager.logout()
            navigateToLogin()
        }

        binding.cardMyTournaments.setOnClickListener {
            findNavController().navigate(UserPageFragmentDirections.actionUserPageFragmentToMyTournamentsFragment())
        }

        binding.cardMyTeams.setOnClickListener {
            findNavController().navigate(UserPageFragmentDirections.actionUserPageFragmentToMyTeamsFragment())
        }

        binding.cardMyMatches.setOnClickListener {
            findNavController().navigate(UserPageFragmentDirections.actionUserPageFragmentToMyMatchesFragment())
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_global_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}