package com.example.tournamaker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TournamentViewModelFactory(private val notificationViewModel: NotificationViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TournamentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TournamentViewModel(notificationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}