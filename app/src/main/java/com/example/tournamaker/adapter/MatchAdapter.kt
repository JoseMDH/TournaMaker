package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.databinding.ItemMatchBinding
import com.example.tournamaker.utils.loadImage

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
            binding.tvMatchName.text = match.name
            binding.ivMatchImage.loadImage(match.image)
        }
    }
}