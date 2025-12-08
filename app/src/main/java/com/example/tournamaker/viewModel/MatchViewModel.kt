package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.data.repository.MatchRepository
import kotlinx.coroutines.launch

class MatchViewModel : ViewModel() {

    private val matchRepository = MatchRepository()

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> = _matches

    private val _selectedMatch = MutableLiveData<Match?>()
    val selectedMatch: LiveData<Match?> = _selectedMatch

    private val _creationResult = MutableLiveData<Result<Match>>()
    val creationResult: LiveData<Result<Match>> = _creationResult

    private val _requestJoinResult = MutableLiveData<Result<Unit>>()
    val requestJoinResult: LiveData<Result<Unit>> = _requestJoinResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadAllMatches(limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _matches.postValue(matchRepository.getAllMatches(limit))
            } catch (e: Exception) {
                _matches.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun loadMatchById(matchId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _selectedMatch.postValue(matchRepository.getMatchById(matchId))
            } catch (e: Exception) {
                _selectedMatch.postValue(null)
            }
            _loading.value = false
        }
    }

    fun loadMatchesByIds(matchIds: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _matches.postValue(matchRepository.getMatchesByIds(matchIds))
            } catch (e: Exception) {
                _matches.postValue(emptyList())
            }
            _loading.value = false
        }
    }

    fun createMatch(match: Match) {
        viewModelScope.launch {
            _loading.value = true
            val result = matchRepository.createMatch(match)
            _creationResult.postValue(result)
            _loading.value = false
        }
    }

    fun requestToJoinMatch(matchId: String, teamId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = matchRepository.requestToJoinMatch(matchId, teamId)
            _requestJoinResult.postValue(result)
            _loading.value = false
        }
    }

    fun updateMatchStatus(matchId: String, status: String) {
        viewModelScope.launch {
            _loading.value = true
            matchRepository.updateMatchStatus(matchId, status)
            loadMatchById(matchId)
            _loading.value = false
        }
    }

    fun updateMatchScore(matchId: String, score1: Int, score2: Int) {
        viewModelScope.launch {
            _loading.value = true
            matchRepository.updateMatchScore(matchId, score1, score2)
            loadMatchById(matchId)
            _loading.value = false
        }
    }
}