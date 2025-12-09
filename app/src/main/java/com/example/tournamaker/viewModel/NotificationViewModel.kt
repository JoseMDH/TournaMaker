package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Notification
import com.example.tournamaker.data.repository.NotificationRepository
import com.example.tournamaker.utils.AuthManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val authManager: AuthManager) : ViewModel() {

    private val notificationRepository = NotificationRepository()
    private val userId = authManager.getUser()?.id

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    val unreadCount: LiveData<Int> = if (userId != null) {
        notificationRepository.getUnreadNotificationCount(userId).asLiveData()
    } else {
        MutableLiveData(0)
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun createNotification(userId: String, message: String) {
        viewModelScope.launch {
            val notification = Notification(userId = userId, message = message)
            notificationRepository.createNotification(notification)
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _loading.postValue(true)
            if (userId != null) {
                notificationRepository.getNotifications(userId).collect {
                    _notifications.postValue(it)
                    _loading.postValue(false)
                }
            } else {
                _notifications.postValue(emptyList())
                _loading.postValue(false)
            }
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            userId?.let {
                notificationRepository.markAllNotificationsAsRead(it)
            }
        }
    }
}