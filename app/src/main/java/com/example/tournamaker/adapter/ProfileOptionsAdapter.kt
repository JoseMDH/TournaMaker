package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.databinding.ItemProfileOptionBinding

class ProfileOptionsAdapter(
    private val options: List<Pair<Int, String>>,
    private val onOptionClickListener: (String) -> Unit
) : RecyclerView.Adapter<ProfileOptionsAdapter.ProfileOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileOptionViewHolder {
        val binding = ItemProfileOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileOptionViewHolder, position: Int) {
        val (iconRes, title) = options[position]
        holder.bind(iconRes, title)
        holder.itemView.setOnClickListener {
            onOptionClickListener(title)
        }
    }

    override fun getItemCount(): Int = options.size

    inner class ProfileOptionViewHolder(private val binding: ItemProfileOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconRes: Int, title: String) {
            binding.ivOptionIcon.setImageResource(iconRes)
            binding.tvOptionTitle.text = title
        }
    }
}