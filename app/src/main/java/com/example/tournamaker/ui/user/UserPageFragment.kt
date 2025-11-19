package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.R
import com.example.tournamaker.adapter.TeamAdapter
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentUserPageBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.UserViewModel

class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager
    private lateinit var tournamentAdapter: TournamentAdapter
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerViews()
        setupListeners()
        setupObservers()
        loadData()
    }

    private fun setupUI() {
        val user = authManager.getUser()
        if (user != null) {
            binding.tvUsername.text = user.username
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.ivAvatar.loadImage(user.avatar)
        } else {
            navigateToLogin()
        }
    }

    private fun setupRecyclerViews() {
        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val action = UserPageFragmentDirections.actionUserPageFragmentToTournamentViewFragment(tournament.id)
            findNavController().navigate(action)
        }
        binding.rvUserTournaments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tournamentAdapter
        }

        teamAdapter = TeamAdapter(emptyList()) { team ->
            val action = UserPageFragmentDirections.actionUserPageFragmentToTeamViewFragment(team.id)
            findNavController().navigate(action)
        }
        binding.rvUserTeams.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = teamAdapter
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            authManager.logout()
            navigateToLogin()
        }
    }

    private fun setupObservers() {
        viewModel.userTournaments.observe(viewLifecycleOwner) { tournaments ->
            tournamentAdapter.updateTournaments(tournaments)
        }

        viewModel.userTeams.observe(viewLifecycleOwner) { teams ->
            teamAdapter.updateTeams(teams)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        }
    }

    private fun loadData() {
        val user = authManager.getUser()
        user?.id?.let {
            viewModel.loadUserProfile(it)
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_userPageFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}