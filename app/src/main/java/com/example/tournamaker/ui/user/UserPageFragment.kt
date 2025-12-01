package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tournamaker.R
import com.example.tournamaker.adapter.ProfileOptionsAdapter
import com.example.tournamaker.databinding.FragmentUserPageBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.UserViewModel

class UserPageFragment : Fragment() {

    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        loadData()
    }

    private fun setupUI() {
        val options = listOf(
            Pair(android.R.drawable.ic_menu_edit, "Datos Personales"),
            Pair(android.R.drawable.ic_menu_myplaces, "Mis Equipos"),
            Pair(android.R.drawable.ic_menu_sort_by_size, "Mis Estadísticas"),
            Pair(android.R.drawable.ic_menu_agenda, "Mis Torneos"),
            Pair(android.R.drawable.ic_menu_today, "Mis Partidos")
        )

        val adapter = ProfileOptionsAdapter(options) { selectedOption ->
            when (selectedOption) {
                "Datos Personales" -> findNavController().navigate(R.id.action_userPageFragment_to_personalDataFragment)
                "Mis Equipos" -> findNavController().navigate(R.id.action_userPageFragment_to_myTeamsFragment)
                "Mis Torneos" -> findNavController().navigate(R.id.action_userPageFragment_to_myTournamentsFragment)
                "Mis Partidos" -> findNavController().navigate(R.id.action_userPageFragment_to_myMatchesFragment)
                else -> showToast("Opción seleccionada: $selectedOption")
            }
        }

        binding.rvProfileOptions.layoutManager = LinearLayoutManager(context)
        binding.rvProfileOptions.adapter = adapter

        binding.btnLogout.setOnClickListener {
            authManager.logout()
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.username
                Glide.with(this)
                    .load(it.avatar)
                    .circleCrop()
                    .into(binding.ivAvatar)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun loadData() {
        val user = authManager.getUser()
        if (user != null) {
            // Le pasamos el ID y el nombre de usuario, como requiere el ViewModel ahora
            viewModel.loadUserProfile(user.id, user.username)
        } else {
            authManager.logout()
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}