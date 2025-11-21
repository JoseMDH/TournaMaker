package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.User
import com.example.tournamaker.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = userRepository.getById(userId)
                _user.postValue(user)
            } catch (e: Exception) {
                _user.postValue(null)
            }
            _loading.value = false
        }
    }
}