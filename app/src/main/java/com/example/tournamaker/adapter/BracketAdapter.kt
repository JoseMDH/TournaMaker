package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.ItemBracketMatchBinding
import com.example.tournamaker.utils.loadImage

class BracketAdapter(
    private var matches: List<Match>,
    private val onMatchClickListener: (Match) -> Unit
) : RecyclerView.Adapter<BracketAdapter.BracketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BracketViewHolder {
        val binding = ItemBracketMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BracketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BracketViewHolder, position: Int) {
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

    inner class BracketViewHolder(private val binding: ItemBracketMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            if (match.team1Id != null) {
                binding.tvTeam1NameBracket.text = match.team1Name
                binding.ivTeam1ImageBracket.loadImage(match.team1Image)
            } else {
                binding.tvTeam1NameBracket.text = "Waiting..."
            }

            if (match.team2Id != null) {
                binding.tvTeam2NameBracket.text = match.team2Name
                binding.ivTeam2ImageBracket.loadImage(match.team2Image)
            } else {
                binding.tvTeam2NameBracket.text = "Waiting..."
            }

            binding.tvScoreBracket.text = "${match.team1Score} - ${match.team2Score}"
        }
    }
}