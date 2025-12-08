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

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

    private val _selectedTeam = MutableLiveData<Team?>()
    val selectedTeam: LiveData<Team?> = _selectedTeam

    private val _creationResult = MutableLiveData<Result<Team>>()
    val creationResult: LiveData<Result<Team>> = _creationResult

    private val _requestJoinResult = MutableLiveData<Result<Unit>>()
    val requestJoinResult: LiveData<Result<Unit>> = _requestJoinResult

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

    fun loadTeamById(teamId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _selectedTeam.postValue(teamRepository.getTeamById(teamId))
            } catch (e: Exception) {
                _selectedTeam.postValue(null)
            }
            _loading.value = false
        }
    }

    fun loadAllTeams(limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _teams.postValue(teamRepository.getAllTeams(limit))
            } catch (e: Exception) {
                _teams.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun loadTeamsByIds(teamIds: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _teams.postValue(teamRepository.getTeamsByIds(teamIds))
            } catch (e: Exception) {
                _teams.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun requestToJoinTeam(teamId: String, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = teamRepository.requestToJoinTeam(teamId, userId)
            _requestJoinResult.postValue(result)
            _loading.value = false
        }
    }
}