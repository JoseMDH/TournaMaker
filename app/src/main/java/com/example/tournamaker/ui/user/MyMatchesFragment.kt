package com.example.tournamaker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamaker.adapter.MatchAdapter
import com.example.tournamaker.databinding.FragmentMyMatchesBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.show
import com.example.tournamaker.viewModel.UserViewModel

class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyMatchesBinding.inflate(inflater, container, false)
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
        matchAdapter = MatchAdapter(emptyList()) { match ->
            val action = MyMatchesFragmentDirections.actionMyMatchesFragmentToMatchViewFragment(match.id)
            findNavController().navigate(action)
        }
        binding.rvMyMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchAdapter
        }
    }

    private fun setupObservers() {
        viewModel.userMatches.observe(viewLifecycleOwner) { matches ->
            if (matches.isEmpty()) {
                binding.tvNoMatches.show()
                binding.rvMyMatches.hide()
            } else {
                binding.tvNoMatches.hide()
                binding.rvMyMatches.show()
                matchAdapter.updateMatches(matches)
            }
        }
    }

    private fun loadData() {
        val user = authManager.getUser()
        if (user != null) {
            viewModel.loadUserProfile(
                user.id,
                username = user.username
            )
        } else {
            findNavController().navigate(MyMatchesFragmentDirections.actionGlobalLoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}