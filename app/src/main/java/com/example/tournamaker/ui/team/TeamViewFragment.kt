package com.example.tournamaker.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.JoinRequest
import com.example.tournamaker.data.model.RequestType
import com.example.tournamaker.databinding.FragmentTeamViewBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show
import com.example.tournamaker.utils.showToast
import com.example.tournamaker.viewModel.JoinRequestViewModel
import com.example.tournamaker.viewModel.TeamViewModel

class TeamViewFragment : Fragment() {

    private var _binding: FragmentTeamViewBinding? = null
    private val binding get() = _binding!!

    private val teamViewModel: TeamViewModel by viewModels()
    private val joinRequestViewModel: JoinRequestViewModel by viewModels()
    private val args: TeamViewFragmentArgs by navArgs()
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
                    binding.btnJoinRequest.setOnClickListener {
                        val request = JoinRequest(
                            type = RequestType.TEAM_JOIN,
                            requesterId = currentUser.id,
                            requesterName = currentUser.username,
                            targetId = team.id,
                            targetName = team.name,
                            ownerId = team.creatorId
                        )
                        joinRequestViewModel.createJoinRequest(request)
                    }
                } else {
                    binding.btnJoinRequest.hide()
                }
            }
        }

        teamViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        joinRequestViewModel.requestResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { showToast("Solicitud enviada correctamente") },
                onFailure = { error -> showToast("Error al enviar la solicitud: ${error.message}") }
            )
        }
    }

    private fun loadData() {
        teamViewModel.loadTeamById(args.teamId)
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