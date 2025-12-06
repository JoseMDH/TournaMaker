package com.example.tournamaker.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.adapter.TournamentAdapter
import com.example.tournamaker.databinding.FragmentAllTournamentsBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.TournamentViewModel

class AllTournamentsFragment : Fragment() {
    private var _binding: FragmentAllTournamentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TournamentViewModel by viewModels()
    private lateinit var tournamentAdapter: TournamentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllTournamentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- CÓDIGO CLAVE AÑADIDO ---
        // Configura la ActionBar/Toolbar de la Activity para este fragmento
        (activity as? AppCompatActivity)?.supportActionBar?.let { actionBar ->
            NavigationUI.setupActionBarWithNavController(activity as AppCompatActivity, findNavController())
            actionBar.title = "Torneos Disponibles"
        }
        // -------------------------

        setupRecyclerView()
        setupObservers()
        viewModel.loadAllTournaments()
    }

    private fun setupRecyclerView() {
        tournamentAdapter = TournamentAdapter(emptyList()) { tournament ->
            val action = AllTournamentsFragmentDirections
                .actionAllTournamentsFragmentToTournamentView(tournament.id)
            findNavController().navigate(action)
        }
        binding.rvTournaments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tournamentAdapter
        }
    }

    private fun setupObservers() {
        viewModel.tournaments.observe(viewLifecycleOwner) { tournaments ->
            tournamentAdapter.updateTournaments(tournaments)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
