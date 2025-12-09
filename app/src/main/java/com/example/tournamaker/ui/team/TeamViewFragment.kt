package com.example.tournamaker.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.R
import com.example.tournamaker.databinding.FragmentTeamViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.NotificationViewModel
import com.example.tournamaker.viewModel.NotificationViewModelFactory
import com.example.tournamaker.viewModel.TeamViewModel
import com.example.tournamaker.viewModel.TeamViewModelFactory

class TeamViewFragment : Fragment() {

    private var _binding: FragmentTeamViewBinding? = null
    private val binding get() = _binding!!

    private val notificationViewModel: NotificationViewModel by viewModels { 
        NotificationViewModelFactory(AuthManager.getInstance(requireContext())) 
    }
    private val teamViewModel: TeamViewModel by viewModels { 
        TeamViewModelFactory(notificationViewModel) 
    }
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamViewBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        loadData()
    }

    private fun setupObservers() {
        teamViewModel.selectedTeam.observe(viewLifecycleOwner) { team ->
            if (team != null) {
                binding.tvTeamName.text = team.name
                binding.ivTeamImage.loadImage(team.image)

                val adapter = ParticipantsAdapter(team.participants)
                binding.rvTeamParticipants.adapter = adapter

                // Logic to show/hide the join button
                val currentUser = authManager.getUser()
                if (currentUser != null && currentUser.id != team.creatorId && !team.participants.contains(currentUser.username)) {
                    binding.btnJoinRequest.show()
                    binding.btnJoinRequest.text = getString(R.string.request_to_join)
                    binding.btnJoinRequest.setOnClickListener {
                        teamViewModel.joinTeam(team.id, currentUser.id, currentUser.username)
                    }
                } else {
                    binding.btnJoinRequest.hide()
                }
            }
        }

        teamViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        teamViewModel.joinResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showToast(getString(R.string.team_joined_successfully))
                    loadData() // Recargar datos para ocultar el botón
                },
                onFailure = { error -> showToast("${getString(R.string.error_joining_team)}: ${error.message}") }
            )
        }
    }

    private fun loadData() {
        arguments?.getString("teamId")?.let { teamId ->
            teamViewModel.loadTeamById(teamId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ParticipantsAdapter(private val participants: List<String>) :
        RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

        inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return ViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = participants[position]
        }

        override fun getItemCount() = participants.size
    }
}