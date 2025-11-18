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
                val existingUser = userRepository.getByEmail(email)
                if (existingUser != null) {
                    _registerResult.postValue(Result.failure(Exception("El correo electrónico ya está en uso")))
                    _loading.postValue(false)
                    return@launch
                }

                val newUser = User(username = username, name = name, email = email, password = password)
                val creationResult = userRepository.create(newUser)

                _registerResult.postValue(creationResult)

            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }
}