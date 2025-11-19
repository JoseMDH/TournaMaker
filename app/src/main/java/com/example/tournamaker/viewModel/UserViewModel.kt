package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.data.repository.TeamRepository
import com.example.tournamaker.data.repository.TournamentRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val tournamentRepository = TournamentRepository()
    private val teamRepository = TeamRepository()

    private val _userTournaments = MutableLiveData<List<Tournament>>()
    val userTournaments: LiveData<List<Tournament>> = _userTournaments

    private val _userTeams = MutableLiveData<List<Team>>()
    val userTeams: LiveData<List<Team>> = _userTeams

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val tournaments = tournamentRepository.getTournamentsByCreator(userId)
                val teams = teamRepository.getTeamsByCreator(userId)
                _userTournaments.postValue(tournaments)
                _userTeams.postValue(teams)
            } catch (e: Exception) {
                _userTournaments.postValue(emptyList())
                _userTeams.postValue(emptyList())
            }
            _loading.value = false
        }
    }
}