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
import com.example.tournamaker.adapter.MatchAdapter
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentLandingBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.MatchViewModel
import com.example.tournamaker.viewModel.TournamentViewModel

class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    private val tournamentViewModel: TournamentViewModel by viewModels()
    private val matchViewModel: MatchViewModel by viewModels()

    private lateinit var tournamentAdapter: TournamentAdapter
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        loadData()
    }

    private fun setupUI() {
        // Setup Tournaments RecyclerView
        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val action = LandingFragmentDirections.actionLandingFragmentToTournamentView(tournament.id)
            findNavController().navigate(action)
        }
        binding.rvTournaments.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tournamentAdapter
        }

        // Setup Matches RecyclerView
        matchAdapter = MatchAdapter(emptyList()) { match ->
            val action = LandingFragmentDirections.actionLandingFragmentToMatchView(match.id)
            findNavController().navigate(action)
        }
        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = matchAdapter
        }

        // Setup 'See All' buttons
        binding.tvSeeAllTournaments.setOnClickListener {
            findNavController().navigate(R.id.allTournamentsFragment)
        }

        binding.tvSeeAllMatches.setOnClickListener {
            findNavController().navigate(R.id.allMatchesFragment)
        }
    }

    private fun setupObservers() {
        // Observe tournaments
        tournamentViewModel.tournaments.observe(viewLifecycleOwner) { tournaments ->
            tournamentAdapter.updateTournaments(tournaments)
        }

        // Observe matches
        matchViewModel.matches.observe(viewLifecycleOwner) { matches ->
            matchAdapter.updateMatches(matches)
        }

        // Observe loading states
        tournamentViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        matchViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // You might want to handle loading state more granularly
            // For now, any loading will show the progress bar
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadData() {
        // Load a limited number of items for the landing page
        tournamentViewModel.loadAllTournaments(limit = 5)
        matchViewModel.loadAllMatches(limit = 5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}