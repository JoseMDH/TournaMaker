package com.example.tournamaker.ui.match

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.R
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.FragmentMatchViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
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
        loadData()
    }

    private fun setupObservers() {
        viewModel.selectedMatch.observe(viewLifecycleOwner) { match ->
            if (match != null) {
                updateUi(match)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.requestJoinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast(getString(R.string.request_sent_successfully)) },
                onFailure = { error -> showToast("${getString(R.string.error_sending_request)}: ${error.message}") }
            )
        }
    }

    private fun updateUi(match: Match) {
        binding.tvMatchName.text = match.name
        binding.ivMatchImage.loadImage(match.image)
        binding.tvMatchDateTime.text = "${match.date} - ${match.hour}"
        binding.tvMatchStatus.text = match.status

        // Team 1
        if (match.team1Id != null) {
            binding.tvTeam1Name.text = match.team1Name
            binding.ivTeam1Image.loadImage(match.team1Image)
            binding.ivTeam1Placeholder.hide()
        } else {
            binding.tvTeam1Name.text = getString(R.string.waiting)
            binding.ivTeam1Image.setImageResource(R.color.grey)
            binding.ivTeam1Placeholder.show()
        }

        // Team 2
        if (match.team2Id != null) {
            binding.tvTeam2Name.text = match.team2Name
            binding.ivTeam2Image.loadImage(match.team2Image)
            binding.ivTeam2Placeholder.hide()
        } else {
            binding.tvTeam2Name.text = getString(R.string.waiting)
            binding.ivTeam2Image.setImageResource(R.color.grey)
            binding.ivTeam2Placeholder.show()
        }

        binding.tvScore.text = "${match.team1Score} - ${match.team2Score}"

        handleMatchState(match)
    }

    private fun handleMatchState(match: Match) {
        val currentUser = authManager.getUser()
        val isCreator = currentUser?.id == match.creatorId

        when (match.status) {
            "PENDING" -> {
                binding.btnStartMatch.visibility = if (isCreator && match.team1Id != null && match.team2Id != null) View.VISIBLE else View.GONE
                binding.btnFinishMatch.hide()
                binding.btnJoinMatch.visibility = if (!isCreator && (match.team1Id == null || match.team2Id == null)) View.VISIBLE else View.GONE
            }
            "IN_PROGRESS" -> {
                binding.btnStartMatch.hide()
                binding.btnFinishMatch.visibility = if (isCreator) View.VISIBLE else View.GONE
                binding.btnJoinMatch.hide()
                if (isCreator) {
                    binding.tvScore.setOnClickListener { showUpdateScoreDialog(match) }
                }
            }
            "FINISHED" -> {
                binding.btnStartMatch.hide()
                binding.btnFinishMatch.hide()
                binding.btnJoinMatch.hide()
            }
        }

        binding.btnStartMatch.setOnClickListener {
            viewModel.updateMatchStatus(match.id, "IN_PROGRESS")
        }

        binding.btnFinishMatch.setOnClickListener {
            viewModel.updateMatchStatus(match.id, "FINISHED")
        }

        binding.btnJoinMatch.setOnClickListener {
            // Implement join logic
        }
    }

    private fun showUpdateScoreDialog(match: Match) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_score, null)
        val etTeam1Score = dialogView.findViewById<EditText>(R.id.et_team1_score)
        val etTeam2Score = dialogView.findViewById<EditText>(R.id.et_team2_score)

        etTeam1Score.setText(match.team1Score.toString())
        etTeam2Score.setText(match.team2Score.toString())

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.update_score))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                val newScore1 = etTeam1Score.text.toString().toIntOrNull() ?: match.team1Score
                val newScore2 = etTeam2Score.text.toString().toIntOrNull() ?: match.team2Score
                viewModel.updateMatchScore(match.id, newScore1, newScore2)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun loadData() {
        viewModel.loadMatchById(args.matchId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}