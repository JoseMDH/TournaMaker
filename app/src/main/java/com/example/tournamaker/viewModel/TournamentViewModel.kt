package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.data.repository.TournamentRepository
import kotlinx.coroutines.launch

class TournamentViewModel : ViewModel() {

    private val tournamentRepository = TournamentRepository()

    private val _tournaments = MutableLiveData<List<Tournament>>()
    val tournaments: LiveData<List<Tournament>> = _tournaments

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadAllTournaments() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _tournaments.postValue(tournamentRepository.getAllTournaments())
            } catch (e: Exception) {
                _tournaments.postValue(emptyList())
            }
            _loading.value = false
        }
    }
}