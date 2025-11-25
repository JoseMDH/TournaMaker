package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.databinding.ItemTournamentBinding
import com.example.tournamaker.utils.loadImage

class TournamentAdapter(
    private var tournaments: List<Tournament>,
    private val onTournamentClickListener: (Tournament) -> Unit
) : RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val binding = ItemTournamentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class TournamentViewHolder(private val binding: ItemTournamentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tournament: Tournament) {
            binding.tvTournamentName.text = tournament.name
            binding.tvOrganizer.text = tournament.organizer
            binding.ivTournamentImage.loadImage(tournament.image)
        }
    }
}