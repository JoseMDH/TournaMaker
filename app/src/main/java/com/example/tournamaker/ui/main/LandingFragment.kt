package com.example.tournamaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.MatchAdapter
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentLandingBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.MatchViewModel
import com.example.tournamaker.viewModel.MatchViewModelFactory
import com.example.tournamaker.viewModel.NotificationViewModel
import com.example.tournamaker.viewModel.NotificationViewModelFactory
import com.example.tournamaker.viewModel.TeamViewModel
import com.example.tournamaker.viewModel.TeamViewModelFactory
import com.example.tournamaker.viewModel.TournamentViewModel
import com.example.tournamaker.viewModel.TournamentViewModelFactory
import com.example.tournamaker.viewModel.UserViewModel

class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels { 
        NotificationViewModelFactory(AuthManager.getInstance(requireContext())) 
    }
    private val teamViewModel: TeamViewModel by viewModels { 
        TeamViewModelFactory(notificationViewModel) 
    }
    private val tournamentViewModel: TournamentViewModel by viewModels { 
        TournamentViewModelFactory(notificationViewModel) 
    }
    private val matchViewModel: MatchViewModel by viewModels { 
        MatchViewModelFactory(notificationViewModel) 
    }

    private lateinit var tournamentAdapter: TournamentAdapter
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var authManager: AuthManager

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
        // Setup Tournaments RecyclerView
        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val bundle = bundleOf("tournamentId" to tournament.id)
            findNavController().navigate(R.id.action_landingFragment_to_tournamentView, bundle)
        }
        binding.rvTournaments.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tournamentAdapter
        }

        // Setup Matches RecyclerView
        matchAdapter = MatchAdapter(emptyList()) { match ->
            val bundle = bundleOf("matchId" to match.id)
            findNavController().navigate(R.id.action_landingFragment_to_matchView, bundle)
        }
        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = matchAdapter
        }

        // Setup Teams RecyclerView
        val user = authManager.getUser()
        teamAdapter = TeamAdapter(emptyList(), user?.username) { team, action ->
            if (action == "join") {
                user?.let { 
                    teamViewModel.joinTeam(team.id, it.id, it.username)
                }
            } else {
                val bundle = bundleOf("teamId" to team.id)
                findNavController().navigate(R.id.action_landingFragment_to_teamViewFragment, bundle)
            }
        }
        binding.rvTeamsLanding.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = teamAdapter
        }

        // Setup 'See All' buttons
        binding.tvSeeAllTournaments.setOnClickListener {
            findNavController().navigate(R.id.allTournamentsFragment)
        }

        binding.tvSeeAllMatches.setOnClickListener {
            findNavController().navigate(R.id.allMatchesFragment)
        }

        binding.tvSeeAllTeams.setOnClickListener {
            findNavController().navigate(R.id.allTeamsFragment)
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

        // Observe teams
        teamViewModel.teams.observe(viewLifecycleOwner) { teams ->
            teamAdapter.updateTeams(teams)
        }

        teamViewModel.joinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { 
                    showToast(getString(R.string.team_joined_successfully))
                    authManager.getUser()?.let { user ->
                        userViewModel.loadUserProfile(user.id, user.username)
                    }
                },
                onFailure = { error -> showToast("${getString(R.string.error_joining_team)}: ${error.message}") }
            )
            loadData()
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
        teamViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadData() {
        // Load a limited number of items for the landing page
        tournamentViewModel.loadAllTournaments(limit = 5)
        matchViewModel.loadAllMatches(limit = 5)
        teamViewModel.loadAllTeams(limit = 5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}