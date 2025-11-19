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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadAllMatches() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _matches.postValue(matchRepository.getAllMatches())
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

    fun createMatch(match: Match) {
        viewModelScope.launch {
            _loading.value = true
            val result = matchRepository.createMatch(match)
            _creationResult.postValue(result)
            _loading.value = false
        }
    }
}