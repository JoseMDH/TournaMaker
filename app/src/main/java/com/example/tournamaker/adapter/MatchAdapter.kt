package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.ItemMatchCardBinding

class MatchAdapter(
    private var matches: List<Match>,
    private val onMatchClickListener: (Match) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MatchViewHolder(private val binding: ItemMatchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            binding.tvMatchName.text = "${match.team1Name} vs ${match.team2Name}"
            binding.tvMatchDescription.text = "${match.team1Score} - ${match.team2Score} (${match.status})"

            // Use Glide to load the image, with a placeholder
            Glide.with(itemView.context)
                .load(match.imageUrl)
                .centerCrop()
                .into(binding.ivMatchImage)
        }
    }
}