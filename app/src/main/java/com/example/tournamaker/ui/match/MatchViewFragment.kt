package com.example.tournamaker.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.databinding.FragmentMatchViewBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.MatchViewModel

class MatchViewFragment : Fragment() {

    private var _binding: FragmentMatchViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private val args: MatchViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchViewBinding.inflate(inflater, container, false)
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
                binding.tvTeam1Name.text = match.team1Name
                binding.tvTeam2Name.text = match.team2Name
                binding.tvScore.text = "${match.team1Score} - ${match.team2Score}"
                binding.tvMatchStatus.text = match.status
                binding.tvMatchDate.text = match.date
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.contentGroup.hide()
            } else {
                binding.progressBar.hide()
                binding.contentGroup.show()
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