package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.databinding.FragmentMyTeamsBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.UserViewModel

class MyTeamsFragment : Fragment() {

    private var _binding: FragmentMyTeamsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTeamsBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadData()
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(emptyList()) { team ->
            val action = MyTeamsFragmentDirections.actionMyTeamsFragmentToTeamViewFragment(team.id)
            findNavController().navigate(action)
        }
        binding.rvMyTeams.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = teamAdapter
        }
    }

    private fun setupObservers() {
        viewModel.userTeams.observe(viewLifecycleOwner) { teams ->
            if (teams.isEmpty()) {
                binding.tvNoTeams.show()
                binding.rvMyTeams.hide()
            } else {
                binding.tvNoTeams.hide()
                binding.rvMyTeams.show()
                teamAdapter.updateTeams(teams)
            }
        }
    }

    private fun loadData() {
        val user = authManager.getUser()
        user?.id?.let { viewModel.loadUserProfile(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}