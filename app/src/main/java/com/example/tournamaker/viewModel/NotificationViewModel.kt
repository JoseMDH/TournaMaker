package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Notification
import com.example.tournamaker.data.repository.NotificationRepository
import com.example.tournamaker.utils.AuthManager
import kotlinx.coroutines.launch

class NotificationViewModel(private val authManager: AuthManager) : ViewModel() {

    private val notificationRepository = NotificationRepository()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadNotifications() {
        viewModelScope.launch {
            _loading.postValue(true)
            val userId = authManager.getUser()?.id
            if (userId != null) {
                val userNotifications = notificationRepository.getNotificationsForUser(userId)
                _notifications.postValue(userNotifications)
            }
            _loading.postValue(false)
        }
    }

    fun createNotification(userId: String, message: String) {
        viewModelScope.launch {
            val notification = Notification(userId = userId, message = message)
            notificationRepository.createNotification(notification)
        }
    }
}