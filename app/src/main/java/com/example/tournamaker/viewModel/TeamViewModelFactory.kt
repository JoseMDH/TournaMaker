package com.example.tournamaker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TeamViewModelFactory(private val notificationViewModel: NotificationViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeamViewModel(notificationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}