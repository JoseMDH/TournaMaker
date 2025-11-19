package com.example.tournamaker.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.databinding.FragmentCreateTeamBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.TeamViewModel

class CreateTeamFragment : Fragment() {
    private var _binding: FragmentCreateTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTeamBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnCreateTeam.setOnClickListener {
            createTeam()
        }
    }

    private fun setupObservers() {
        viewModel.creationResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showToast("Equipo creado con éxito")
                    findNavController().popBackStack()
                },
                onFailure = { error ->
                    showToast(error.message ?: "Error al crear el equipo")
                }
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.btnCreateTeam.isEnabled = false
            } else {
                binding.progressBar.hide()
                binding.btnCreateTeam.isEnabled = true
            }
        }
    }

    private fun createTeam() {
        val teamName = binding.etTeamName.text.toString().trim()
        val teamImage = binding.etTeamImage.text.toString().trim()
        val currentUser = authManager.getUser()

        if (teamName.isEmpty()) {
            showToast("El nombre del equipo es obligatorio")
            return
        }

        if (currentUser == null) {
            showToast("Debes iniciar sesión para crear un equipo")
            return
        }

        val team = Team(
            name = teamName,
            image = teamImage,
            creatorId = currentUser.id,
            participants = listOf(currentUser.username)
        )

        viewModel.createTeam(team)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}