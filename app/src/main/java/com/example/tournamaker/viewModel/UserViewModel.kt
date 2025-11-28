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

    private val userRepository = UserRepository()
    private val tournamentRepository = TournamentRepository()
    private val teamRepository = TeamRepository()
    private val matchRepository = MatchRepository()

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

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUser = userRepository.getById(userId)
                _user.postValue(currentUser)

                if (currentUser != null) {
                    // Load tournaments created by the user
                    val tournaments = tournamentRepository.getTournamentsByCreator(userId)
                    _userTournaments.postValue(tournaments)

                    // Load teams the user participates in
                    val teams = teamRepository.getTeamsByUserParticipation(currentUser.username)
                    _userTeams.postValue(teams)

                    // Load matches for the teams the user is in
                    if (teams.isNotEmpty()) {
                        val teamIds = teams.map { it.id }
                        val matches = matchRepository.getMatchesByTeamIds(teamIds)
                        _userMatches.postValue(matches)
                    } else {
                        _userMatches.postValue(emptyList()) // Post empty list if user has no teams
                    }
                }
            } catch (e: Exception) {
                _user.postValue(null)
                _userTournaments.postValue(emptyList())
                _userTeams.postValue(emptyList())
                _userMatches.postValue(emptyList()) // Post empty list on error
            }
            _loading.value = false
        }
    }
}