package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.data.repository.TeamRepository
import kotlinx.coroutines.launch

class TeamViewModel(
    private val notificationViewModel: NotificationViewModel
) : ViewModel() {

    private val teamRepository = TeamRepository()

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

    private val _selectedTeam = MutableLiveData<Team?>()
    val selectedTeam: LiveData<Team?> = _selectedTeam

    private val _creationResult = MutableLiveData<Result<Team>>()
    val creationResult: LiveData<Result<Team>> = _creationResult

    private val _joinResult = MutableLiveData<Result<Unit>>()
    val joinResult: LiveData<Result<Unit>> = _joinResult

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

    fun joinTeam(teamId: String, userId: String, username: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = teamRepository.joinTeam(teamId, username)
            if (result.isSuccess) {
                val team = teamRepository.getTeamById(teamId)
                if (team != null) {
                    notificationViewModel.createNotification(
                        userId = team.creatorId,
                        message = "$username se ha unido a tu equipo ${team.name}"
                    )
                }
            }
            _joinResult.postValue(result)
            _loading.value = false
        }
    }
}