package com.example.tournamaker.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tournamaker.databinding.FragmentTournamentViewBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.TournamentViewModel

class TournamentViewFragment : Fragment() {

    private var _binding: FragmentTournamentViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TournamentViewModel by viewModels()
    private val args: TournamentViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        loadData()
    }

    private fun setupObservers() {
        viewModel.selectedTournament.observe(viewLifecycleOwner) { tournament ->
            if (tournament != null) {
                binding.ivTournamentImage.loadImage(tournament.image)
                binding.tvTournamentName.text = tournament.name
                binding.tvTournamentDescription.text = tournament.description
                binding.tvOrganizer.text = tournament.organizer
                binding.tvPrizePool.text = tournament.prizePool
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.show()
                // Ocultamos el contenido principal mientras se carga
            } else {
                binding.progressBar.hide()
                // Mostramos el contenido una vez cargado
            }
        }
    }

    private fun loadData() {
        viewModel.loadTournamentById(args.tournamentId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}