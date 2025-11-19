package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.data.repository.TeamRepository
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {

    private val teamRepository = TeamRepository()

    private val _creationResult = MutableLiveData<Result<Team>>()
    val creationResult: LiveData<Result<Team>> = _creationResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun createTeam(team: Team) {
        viewModelScope.launch {
            _loading.value = true
            val result = teamRepository.createTeam(team)
            _creationResult.postValue(result)
            _loading.value = false
        }
    }
}