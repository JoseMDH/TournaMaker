package com.example.tournamaker.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.databinding.FragmentMatchViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.MatchViewModel

class MatchViewFragment : Fragment() {

    private var _binding: FragmentMatchViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private val args: MatchViewFragmentArgs by navArgs()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchViewBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupObservers() {
        viewModel.selectedMatch.observe(viewLifecycleOwner) { match ->
            if (match != null) {
                binding.tvTeam1Name.text = match.team1Name
                binding.tvTeam2Name.text = match.team2Name
                binding.tvScore.text = "${match.team1Score} - ${match.team2Score}"
                binding.tvMatchStatus.text = match.status
                binding.tvMatchDate.text = match.date

                val teamId = authManager.getUser()?.teamId
                if (!teamId.isNullOrEmpty() && !match.requestedTeams.contains(teamId)) {
                    binding.btnRequestToJoin.show()
                } else {
                    binding.btnRequestToJoin.hide()
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.tvTeam1Name.hide()
                binding.tvTeam2Name.hide()
                binding.tvScore.hide()
                binding.tvMatchStatusLabel.hide()
                binding.tvMatchStatus.hide()
                binding.tvMatchDateLabel.hide()
                binding.tvMatchDate.hide()
                binding.btnRequestToJoin.hide()
            } else {
                binding.progressBar.hide()
                binding.tvTeam1Name.show()
                binding.tvTeam2Name.show()
                binding.tvScore.show()
                binding.tvMatchStatusLabel.show()
                binding.tvMatchStatus.show()
                binding.tvMatchDateLabel.show()
                binding.tvMatchDate.show()
            }
        }

        viewModel.requestJoinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showToast("Request sent successfully")
                    binding.btnRequestToJoin.hide()
                },
                onFailure = { showToast("Error sending request") }
            )
        }
    }

    private fun setupListeners() {
        binding.btnRequestToJoin.setOnClickListener {
            authManager.getUser()?.teamId?.let { teamId ->
                viewModel.requestToJoinMatch(args.matchId, teamId)
            }
        }
    }

    private fun loadData() {
        viewModel.loadMatchById(args.matchId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}