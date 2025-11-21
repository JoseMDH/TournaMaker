package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.databinding.ItemTournamentCardBinding

class TournamentAdapter(
    private var tournaments: List<Tournament>,
    private val onTournamentClickListener: (Tournament) -> Unit
) : RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val binding = ItemTournamentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TournamentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournament = tournaments[position]
        holder.bind(tournament)
        holder.itemView.setOnClickListener {
            onTournamentClickListener(tournament)
        }
    }

    override fun getItemCount(): Int = tournaments.size

    fun updateTournaments(newTournaments: List<Tournament>) {
        tournaments = newTournaments
        notifyDataSetChanged()
    }

    inner class TournamentViewHolder(private val binding: ItemTournamentCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tournament: Tournament) {
            binding.tvTournamentName.text = tournament.name
            binding.tvTournamentDescription.text = tournament.description

            // Use Glide to load the image, with a placeholder
            Glide.with(itemView.context)
                .load(tournament.imageUrl)
                .centerCrop()
                .into(binding.ivTournamentImage)
        }
    }
}