package com.example.tournamaker.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.databinding.FragmentAllTeamsBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.TeamViewModel

class AllTeamsFragment : Fragment() {

    private var _binding: FragmentAllTeamsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllTeamsBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadAllTeams()
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(emptyList(), authManager.getUser()?.teamId) { team, action ->
            if (action == "join") {
                authManager.getUser()?.id?.let {
                    viewModel.requestToJoinTeam(team.id, it)
                }
            }
        }
        binding.rvTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.teams.observe(viewLifecycleOwner) { teams ->
            teamAdapter.updateTeams(teams)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                binding.rvTeams.hide()
            } else {
                binding.progressBar.hide()
                binding.rvTeams.show()
            }
        }

        viewModel.requestJoinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast(getString(R.string.request_sent_successfully)) },
                onFailure = { showToast(getString(R.string.error_sending_request)) }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}