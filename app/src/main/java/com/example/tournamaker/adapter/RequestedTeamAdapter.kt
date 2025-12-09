package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.databinding.ItemRequestedTeamBinding
import com.example.tournamaker.utils.loadImage

class RequestedTeamAdapter(
    private var teams: List<Team>,
    private val onAction: (Team, String) -> Unit
) : RecyclerView.Adapter<RequestedTeamAdapter.RequestedTeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestedTeamViewHolder {
        val binding = ItemRequestedTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestedTeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestedTeamViewHolder, position: Int) {
        val team = teams[position]
        holder.bind(team)
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()
    }

    inner class RequestedTeamViewHolder(private val binding: ItemRequestedTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team) {
            binding.tvTeamNameRequest.text = team.name
            binding.ivTeamImageRequest.loadImage(team.image)

            binding.btnAcceptRequest.setOnClickListener {
                onAction(team, "accept")
            }

            binding.btnRejectRequest.setOnClickListener {
                onAction(team, "reject")
            }
        }
    }
}