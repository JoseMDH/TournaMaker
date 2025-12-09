package com.example.tournamaker.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.databinding.FragmentAllTeamsBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.NotificationViewModel
import com.example.tournamaker.viewModel.NotificationViewModelFactory
import com.example.tournamaker.viewModel.TeamViewModel
import com.example.tournamaker.viewModel.TeamViewModelFactory

class AllTeamsFragment : Fragment() {

    private var _binding: FragmentAllTeamsBinding? = null
    private val binding get() = _binding!!

    private val notificationViewModel: NotificationViewModel by viewModels { 
        NotificationViewModelFactory(AuthManager.getInstance(requireContext())) 
    }
    private val viewModel: TeamViewModel by viewModels { 
        TeamViewModelFactory(notificationViewModel) 
    }
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
        val user = authManager.getUser()
        teamAdapter = TeamAdapter(emptyList(), user?.username) { team, action ->
            if (action == "join") {
                user?.let {
                    viewModel.joinTeam(team.id, it.id, it.username)
                }
            } else {
                val bundle = bundleOf("teamId" to team.id)
                findNavController().navigate(R.id.action_allTeamsFragment_to_teamViewFragment, bundle)
            }
        }
        binding.rvTeams.apply {
            adapter = teamAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
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

        viewModel.joinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast(getString(R.string.team_joined_successfully)) },
                onFailure = { error -> showToast("${getString(R.string.error_joining_team)}: ${error.message}") }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}