package com.example.tournamaker.ui.tournament

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.data.model.JoinRequest
import com.example.tournamaker.data.model.RequestType
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.databinding.FragmentTournamentViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.JoinRequestViewModel
import com.example.tournamaker.viewModel.TournamentViewModel
import com.example.tournamaker.viewModel.UserViewModel

class TournamentViewFragment : Fragment() {

    private var _binding: FragmentTournamentViewBinding? = null
    private val binding get() = _binding!!

    private val tournamentViewModel: TournamentViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val joinRequestViewModel: JoinRequestViewModel by viewModels()
    private val args: TournamentViewFragmentArgs by navArgs()
    private lateinit var authManager: AuthManager

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
        setupObservers()
        loadData()
    }

    private fun setupObservers() {
        tournamentViewModel.selectedTournament.observe(viewLifecycleOwner) { tournament ->
            if (tournament != null) {
                binding.ivTournamentImage.loadImage(tournament.image)
                binding.tvTournamentName.text = tournament.name
                binding.tvTournamentDescription.text = tournament.description

                // Check user's teams to decide if the join button should be shown
                userViewModel.userTeams.observe(viewLifecycleOwner) { userTeams ->
                    val eligibleTeams = userTeams.filter { !tournament.teams .contains(it.id) }
                    if (eligibleTeams.isNotEmpty() && authManager.getUser()?.id != tournament.creatorId) {
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

        tournamentViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        joinRequestViewModel.requestResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast("Solicitud enviada correctamente") },
                onFailure = { error -> showToast("Error al enviar la solicitud: ${error.message}") }
            )
        }
    }

    private fun showTeamSelectionDialog(teams: List<Team>, tournament: com.example.tournamaker.data.model.Tournament) {
        val teamNames = teams.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona un equipo para unirte")
            .setItems(teamNames) { _, which ->
                val selectedTeam = teams[which]
                val currentUser = authManager.getUser()!!

                val request = JoinRequest(
                    type = RequestType.TOURNAMENT_JOIN,
                    requesterId = selectedTeam.id, // The team is the requester
                    requesterName = selectedTeam.name,
                    targetId = tournament.id,
                    targetName = tournament.name,
                    ownerId = tournament.creatorId // The tournament creator is the owner
                )
                joinRequestViewModel.createJoinRequest(request)
            }
            .setNegativeButton("Cancelar", null)
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