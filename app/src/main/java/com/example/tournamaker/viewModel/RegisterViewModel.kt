package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.User
import com.example.tournamaker.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun register(username: String, name: String, email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.registerUser(email, password, username, name)
                _registerResult.postValue(result)
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }
}