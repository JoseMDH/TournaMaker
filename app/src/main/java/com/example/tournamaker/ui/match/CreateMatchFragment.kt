package com.example.tournamaker.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.FragmentCreateMatchBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.MatchViewModel

class CreateMatchFragment : Fragment() {

    private var _binding: FragmentCreateMatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private val args: CreateMatchFragmentArgs by navArgs()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMatchBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnCreateMatch.setOnClickListener {
            createMatch()
        }
    }

    private fun setupObservers() {
        viewModel.creationResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showToast("Partido creado con éxito")
                    findNavController().popBackStack()
                },
                onFailure = { error ->
                    showToast(error.message ?: "Error al crear el partido")
                }
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.btnCreateMatch.isEnabled = false
            } else {
                binding.progressBar.hide()
                binding.btnCreateMatch.isEnabled = true
            }
        }
    }

    private fun createMatch() {
        val name = binding.etMatchName.text.toString().trim()
        val image = binding.etMatchImage.text.toString().trim()
        val team1Name = binding.etTeam1Name.text.toString().trim()
        val team2Name = binding.etTeam2Name.text.toString().trim()
        val date = binding.etMatchDate.text.toString().trim()
        val hour = binding.etMatchHour.text.toString().trim()
        val tournamentId = args.tournamentId

        val currentUser = authManager.getUser()
        if (currentUser == null) {
            showToast("Debes iniciar sesión para crear un partido")
            return
        }

        if (name.isEmpty() || team1Name.isEmpty() || team2Name.isEmpty() || date.isEmpty()) {
            showToast("El nombre del partido, los equipos y la fecha son obligatorios")
            return
        }

        val newMatch = Match(
            name = name,
            image = image,
            tournamentId = tournamentId ?: "",
            creatorId = currentUser.id,
            team1Name = team1Name,
            team2Name = team2Name,
            date = date,
            hour = hour,
            status = "PENDING"
        )

        viewModel.createMatch(newMatch)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}