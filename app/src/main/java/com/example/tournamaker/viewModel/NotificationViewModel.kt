package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Notification
import com.example.tournamaker.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val notificationRepository = NotificationRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadNotifications() {
        viewModelScope.launch {
            _loading.postValue(true)
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                notificationRepository.getNotifications(userId).collect {
                    _notifications.postValue(it)
                    _loading.postValue(false)
                }
            } else {
                _loading.postValue(false)
            }
        }
    }
}