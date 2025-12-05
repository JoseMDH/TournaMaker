package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.JoinRequest
import com.example.tournamaker.data.model.RequestStatus
import com.example.tournamaker.data.repository.JoinRequestRepository
import kotlinx.coroutines.launch

class JoinRequestViewModel : ViewModel() {

    private val repository = JoinRequestRepository()

    private val _pendingRequests = MutableLiveData<List<JoinRequest>>()
    val pendingRequests: LiveData<List<JoinRequest>> = _pendingRequests

    private val _requestResult = MutableLiveData<Result<Unit>>()
    val requestResult: LiveData<Result<Unit>> = _requestResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun createJoinRequest(request: JoinRequest) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.createJoinRequest(request)
            _requestResult.postValue(result)
            _loading.value = false
        }
    }

    fun loadPendingRequests(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val requests = repository.getPendingRequestsForUser(userId)
                _pendingRequests.postValue(requests)
            } catch (e: Exception) {
                _pendingRequests.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun acceptRequest(requestId: String) {
        updateRequestStatus(requestId, RequestStatus.ACCEPTED)
    }

    fun declineRequest(requestId: String) {
        updateRequestStatus(requestId, RequestStatus.DECLINED)
    }

    private fun updateRequestStatus(requestId: String, status: RequestStatus) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, status)
            // You might want to refresh the list of pending requests here
        }
    }
}