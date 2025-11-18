package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.User
import com.example.tournamaker.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun login(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val user = userRepository.getByEmail(email)
                if (user != null && user.password == password) {
                    _loginResult.postValue(Result.success(user))
                } else {
                    _loginResult.postValue(Result.failure(Exception("Email o contraseña incorrectos")))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
            _loading.postValue(false)
        }
    }
}