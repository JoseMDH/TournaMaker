package com.example.tournamaker.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.adapter.MatchAdapter
import com.example.tournamaker.databinding.FragmentAllMatchesBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.MatchViewModel

class AllMatchesFragment : Fragment() {
    private var _binding: FragmentAllMatchesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchViewModel by viewModels()
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadAllMatches()
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter(emptyList()) { match ->
            val action = AllMatchesFragmentDirections
                .actionAllMatchesFragmentToMatchView(match.id)
            findNavController().navigate(action)
        }
        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchAdapter
        }
    }

    private fun setupObservers() {
        viewModel.matches.observe(viewLifecycleOwner) { matches ->
            matchAdapter.updateMatches(matches)
            binding.tvMatchesCount.text = "Partidos: ${matches.size}"
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