package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.data.repository.TeamRepository
import com.example.tournamaker.data.repository.TournamentRepository
import kotlinx.coroutines.launch

class TournamentViewModel : ViewModel() {

    private val tournamentRepository = TournamentRepository(TeamRepository())

    private val _tournaments = MutableLiveData<List<Tournament>>()
    val tournaments: LiveData<List<Tournament>> = _tournaments

    private val _selectedTournament = MutableLiveData<Tournament?>()
    val selectedTournament: LiveData<Tournament?> = _selectedTournament

    private val _creationResult = MutableLiveData<Result<Tournament>>()
    val creationResult: LiveData<Result<Tournament>> = _creationResult

    private val _joinResult = MutableLiveData<Result<Unit>>()
    val joinResult: LiveData<Result<Unit>> = _joinResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadAllTournaments(limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _tournaments.postValue(tournamentRepository.getAllTournaments(limit))
            } catch (e: Exception) {
                _tournaments.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun loadTournamentById(tournamentId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _selectedTournament.postValue(tournamentRepository.getTournamentById(tournamentId))
            } catch (e: Exception) {
                _selectedTournament.postValue(null)
            }
            _loading.value = false
        }
    }

    fun createTournament(tournament: Tournament) {
        viewModelScope.launch {
            _loading.value = true
            val result = tournamentRepository.createTournament(tournament)
            _creationResult.postValue(result)
            _loading.value = false
        }
    }

    fun joinTournament(tournamentId: String, teamId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = tournamentRepository.joinTournament(tournamentId, teamId)
            _joinResult.postValue(result)
            loadTournamentById(tournamentId)
            _loading.value = false
        }
    }
}