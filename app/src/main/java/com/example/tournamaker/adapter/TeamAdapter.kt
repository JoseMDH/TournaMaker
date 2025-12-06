package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.databinding.ItemTeamBinding
import com.example.tournamaker.utils.hide
import com.example.tournamaker.utils.loadImage
import com.example.tournamaker.utils.show

class TeamAdapter(
    private var teams: List<Team>,
    private val currentUserTeamId: String?,
    private val onTeamInteraction: (Team, String) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        holder.bind(team)
        holder.itemView.setOnClickListener {
            onTeamInteraction(team, "view")
        }
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<Team>) {
        teams = newTeams
        notifyDataSetChanged()
    }

    inner class TeamViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team) {
            binding.tvTeamName.text = team.name
            binding.ivTeamImage.loadImage(team.image)

            if (currentUserTeamId.isNullOrEmpty() && !team.requestedUsers.contains(currentUserTeamId)) {
                binding.btnJoinTeam.show()
            } else {
                binding.btnJoinTeam.hide()
            }

            binding.btnJoinTeam.setOnClickListener {
                onTeamInteraction(team, "join")
            }
        }
    }
}