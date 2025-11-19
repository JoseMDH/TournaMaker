package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.ItemMatchBinding

class MatchAdapter(
    private var matches: List<Match>,
    private val onMatchClickListener: (Match) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.bind(match)
        holder.itemView.setOnClickListener {
            onMatchClickListener(match)
        }
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<Match>) {
        matches = newMatches
        notifyDataSetChanged()
    }

    inner class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            binding.tvTeam1Name.text = match.team1Name
            binding.tvTeam2Name.text = match.team2Name
            binding.tvScore.text = "${match.team1Score} - ${match.team2Score}"
            binding.tvMatchStatus.text = match.status
        }
    }
}