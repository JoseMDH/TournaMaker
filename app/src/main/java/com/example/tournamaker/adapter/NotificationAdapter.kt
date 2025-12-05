package com.example.tournamaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamaker.data.model.JoinRequest
import com.example.tournamaker.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var requests: List<JoinRequest>,
    private val onAccept: (JoinRequest) -> Unit,
    private val onDecline: (JoinRequest) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int = requests.size

    fun updateRequests(newRequests: List<JoinRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: JoinRequest) {
            val message = "'${request.requesterName}' quiere unirse a '${request.targetName}'"
            binding.tvNotificationMessage.text = message

            binding.btnAccept.setOnClickListener { onAccept(request) }
            binding.btnDecline.setOnClickListener { onDecline(request) }
        }
    }
}