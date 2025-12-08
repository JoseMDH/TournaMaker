package com.example.tournamaker.ui.tournament

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.BracketAdapter
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.databinding.FragmentTournamentViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.MatchViewModel
import com.example.tournamaker.viewModel.TeamViewModel
import com.example.tournamaker.viewModel.TournamentViewModel
import com.example.tournamaker.viewModel.UserViewModel

class TournamentViewFragment : Fragment() {

    private var _binding: FragmentTournamentViewBinding? = null
    private val binding get() = _binding!!

    private val tournamentViewModel: TournamentViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val matchViewModel: MatchViewModel by viewModels()
    private val teamViewModel: TeamViewModel by viewModels()
    private val args: TournamentViewFragmentArgs by navArgs()
    private lateinit var authManager: AuthManager

    private lateinit var teamAdapter: TeamAdapter
    private lateinit var bracketAdapter: BracketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentViewBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupObservers()
        loadData()
    }

    private fun setupRecyclerViews() {
        teamAdapter = TeamAdapter(emptyList(), null) { team, _ ->
            // Navigate to team view
        }
        binding.rvTeams.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = teamAdapter
        }

        bracketAdapter = BracketAdapter(emptyList()) { match ->
            val action = TournamentViewFragmentDirections.actionTournamentViewFragmentToMatchViewFragment(match.id)
            findNavController().navigate(action)
        }
        binding.rvBracket.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bracketAdapter
        }
    }

    private fun setupObservers() {
        tournamentViewModel.selectedTournament.observe(viewLifecycleOwner) { tournament ->
            if (tournament != null) {
                binding.ivTournamentImage.loadImage(tournament.image)
                binding.tvTournamentName.text = tournament.name
                binding.tvTournamentDescription.text = tournament.description
                binding.tvOrganizer.text = tournament.organizer
                binding.tvPrizePool.text = tournament.prizePool

                teamViewModel.loadTeamsByIds(tournament.teams)
                matchViewModel.loadMatchesByIds(tournament.rounds["round1"] ?: emptyList())

                userViewModel.userTeams.observe(viewLifecycleOwner) { userTeams ->
                    val eligibleTeams = userTeams.filter { !tournament.teams.contains(it.id) }
                    if (eligibleTeams.isNotEmpty() && tournament.status == "open") {
                        binding.btnJoinRequest.show()
                        binding.btnJoinRequest.setOnClickListener {
                            showTeamSelectionDialog(eligibleTeams, tournament)
                        }
                    } else {
                        binding.btnJoinRequest.hide()
                    }
                }
            }
        }

        teamViewModel.teams.observe(viewLifecycleOwner) { teams ->
            teamAdapter.updateTeams(teams)
        }

        matchViewModel.matches.observe(viewLifecycleOwner) { matches ->
            bracketAdapter.updateMatches(matches)
        }

        tournamentViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        tournamentViewModel.requestJoinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast(getString(R.string.request_sent_successfully)) },
                onFailure = { error -> showToast("${getString(R.string.error_sending_request)}: ${error.message}") }
            )
        }
    }

    private fun showTeamSelectionDialog(teams: List<Team>, tournament: com.example.tournamaker.data.model.Tournament) {
        val teamNames = teams.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_a_team_to_join))
            .setItems(teamNames) { _, which ->
                val selectedTeam = teams[which]
                tournamentViewModel.requestToJoinTournament(tournament.id, selectedTeam.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun loadData() {
        tournamentViewModel.loadTournamentById(args.tournamentId)
        val currentUser = authManager.getUser()
        if (currentUser != null) {
            userViewModel.loadUserProfile(currentUser.id, currentUser.username)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}