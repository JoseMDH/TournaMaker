package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentMyTournamentsBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.UserViewModel

class MyTournamentsFragment : Fragment() {

    private var _binding: FragmentMyTournamentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager
    private lateinit var tournamentAdapter: TournamentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTournamentsBinding.inflate(inflater, container, false)
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
        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val action = MyTournamentsFragmentDirections.actionMyTournamentsFragmentToTournamentViewFragment(tournament.id)
            findNavController().navigate(action)
        }
        binding.rvMyTournaments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tournamentAdapter
        }
    }

    private fun setupObservers() {
        viewModel.userTournaments.observe(viewLifecycleOwner) { tournaments ->
            if (tournaments.isEmpty()) {
                binding.tvNoTournaments.show()
                binding.rvMyTournaments.hide()
            } else {
                binding.tvNoTournaments.hide()
                binding.rvMyTournaments.show()
                tournamentAdapter.updateTournaments(tournaments)
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