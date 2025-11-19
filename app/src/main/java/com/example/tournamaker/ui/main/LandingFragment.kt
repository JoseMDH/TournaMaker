package com.example.tournamaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentLandingBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.TournamentViewModel

class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    private val tournamentViewModel: TournamentViewModel by viewModels()
    private lateinit var authManager: AuthManager
    private lateinit var tournamentAdapter: TournamentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
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
        val currentUser = authManager.getUser()
        binding.tvWelcome.text = "¡Hola, ${currentUser?.name ?: "Usuario"}!"

        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val bundle = Bundle().apply {
                putString("tournamentId", tournament.id)
            }
            findNavController().navigate(R.id.action_landingFragment_to_tournamentView, bundle)
        }

        binding.rvTournaments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tournamentAdapter
        }
    }

    private fun setupObservers() {
        tournamentViewModel.tournaments.observe(viewLifecycleOwner) { tournaments ->
            if (tournaments.isEmpty()) {
                binding.rvTournaments.hide()
            } else {
                binding.rvTournaments.show()
                tournamentAdapter.updateTournaments(tournaments)
            }
        }

        tournamentViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        }
    }

    private fun loadData() {
        tournamentViewModel.loadAllTournaments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}