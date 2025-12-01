package com.example.tournamaker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamaker.data.model.Match
import com.example.tournamaker.data.model.Team
import com.example.tournamaker.data.model.Tournament
import com.example.tournamaker.data.model.User
import com.example.tournamaker.data.repository.MatchRepository
import com.example.tournamaker.data.repository.TeamRepository
import com.example.tournamaker.data.repository.TournamentRepository
import com.example.tournamaker.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val tournamentRepository = TournamentRepository()
    private val teamRepository = TeamRepository()
    private val matchRepository = MatchRepository()
    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _userTournaments = MutableLiveData<List<Tournament>>()
    val userTournaments: LiveData<List<Tournament>> = _userTournaments

    private val _userTeams = MutableLiveData<List<Team>>()
    val userTeams: LiveData<List<Team>> = _userTeams

    private val _userMatches = MutableLiveData<List<Match>>()
    val userMatches: LiveData<List<Match>> = _userMatches

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUserProfile(userId: String, username: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val userProfile = userRepository.getById(userId)
                val tournaments = tournamentRepository.getTournamentsByCreator(userId)
                val teams = teamRepository.getTeamsByParticipant(username)
                val matches = matchRepository.getMatchesByCreator(userId)

                _user.postValue(userProfile)
                _userTournaments.postValue(tournaments)
                _userTeams.postValue(teams)
                _userMatches.postValue(matches)

            } catch (e: Exception) {
                _user.postValue(null)
                _userTournaments.postValue(emptyList())
                _userTeams.postValue(emptyList())
                _userMatches.postValue(emptyList())
            }
            _loading.value = false
        }
    }
}