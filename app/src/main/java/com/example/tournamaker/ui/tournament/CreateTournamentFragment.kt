package com.example.tournamaker.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.databinding.FragmentCreateTournamentBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.TournamentViewModel

class CreateTournamentFragment : Fragment() {

    private var _binding: FragmentCreateTournamentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TournamentViewModel by viewModels()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTournamentBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnCreateTournament.setOnClickListener {
            createTournament()
        }
    }

    private fun setupObservers() {
        viewModel.creationResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { newTournament ->
                    showToast("Torneo creado con éxito")
                    val action = CreateTournamentFragmentDirections.actionCreateTournamentFragmentToTournamentView(newTournament.id)
                    findNavController().navigate(action)
                },
                onFailure = { error ->
                    showToast(error.message ?: "Error al crear el torneo")
                }
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.btnCreateTournament.isEnabled = false
            } else {
                binding.progressBar.hide()
                binding.btnCreateTournament.isEnabled = true
            }
        }
    }

    private fun createTournament() {
        val name = binding.etTournamentName.text.toString().trim()
        val description = binding.etTournamentDescription.text.toString().trim()
        val image = binding.etTournamentImage.text.toString().trim()
        val organizer = binding.etOrganizer.text.toString().trim()
        val prizePool = binding.etPrizePool.text.toString().trim()
        val entryTax = binding.etEntryTax.text.toString().trim()
        val date = binding.etTournamentDate.text.toString().trim()
        val place = binding.etTournamentPlace.text.toString().trim()
        val teamsNum = binding.etTeamsNum.text.toString().toIntOrNull() ?: 0

        if (name.isEmpty() || organizer.isEmpty()) {
            showToast("El nombre y el organizador son obligatorios")
            return
        }

        val newTournament = Tournament(
            name = name,
            description = description,
            image = image,
            organizer = organizer,
            prizePool = prizePool,
            entryTax = entryTax,
            date = date,
            place = place,
            teamsNum = teamsNum
        )

        viewModel.createTournament(newTournament)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}