package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica e gestão do estado do ecrã da Leaderboard (Tabela de Classificação).
 *
 * Obtém a lista de utilizadores em tempo real do [AuthRepository], aplica filtros
 * (Global vs. Local/País) e mapeia os dados para o estado da UI ([LeaderboardUiState]).
 *
 * @property repository O repositório responsável por aceder aos dados de utilizador e à leaderboard.
 */
class LeaderboardViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    /**
     * Estado da UI da Leaderboard, observável pelo Composable [LeaderboardScreen].
     */
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<UserData>>(emptyList())

    init {
        observeRealtimeLeaderboard()
    }

    /**
     * Processa os eventos de interação da UI e alterações de estado.
     *
     * @param event O evento disparado pela UI.
     */
    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.ViewChanged -> {
                _uiState.update { it.copy(currentView = event.view) }
                updateUiList()
            }

            LeaderboardEvent.ToggleView -> {
                _uiState.update { currentState ->
                    val newView = if (currentState.currentView == "Global") "Local" else "Global"
                    currentState.copy(currentView = newView)
                }
                updateUiList()
            }

            LeaderboardEvent.LoadLeaderboard -> {

            }

            is LeaderboardEvent.UserCountryChanged -> {
                _uiState.update { it.copy(userCountry = event.country) }
                updateUiList()
            }
        }
    }

    /**
     * Inicia a observação da leaderboard em tempo real a partir do repositório.
     *
     * Qualquer alteração de pontos ou registo de novo utilizador reflete-se automaticamente
     * na lista [_leaderboard] e, subsequentemente, na UI.
     */
    private fun observeRealtimeLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getLeaderboard().collect { users ->
                _leaderboard.value = users
                updateUiList()
            }
        }
    }

    /**
     * Aplica os filtros de vista ([currentView]) à lista de utilizadores
     * e mapeia os dados para o formato de exibição da UI ([LeaderboardItem]).
     */
    private fun updateUiList() {
        val allUsers = _leaderboard.value
        val currentState = _uiState.value
        val currentUserId = repository.getCurrentUserId()

        val filteredUsers = if (currentState.currentView == "Local") {
            allUsers.filter { it.country == currentState.userCountry }
        } else {
            allUsers
        }

        val uiItems = filteredUsers.mapIndexed { index, data ->
            LeaderboardItem(
                position = index + 1,
                username = data.name,
                countryName = data.country,
                countryCode = data.country,
                score = data.points,
                flagEmoji = getFlagEmoji(data.country),
                isCurrentUser = data.id == currentUserId
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                leaderboardItems = uiItems
            )
        }
    }

    /**
     * Converte um código de país de 2 letras (ISO 3166-1 alpha-2) para o respetivo emoji de bandeira.
     *
     * @param countryCode O código do país (ex: "PT", "BR").
     * @return O emoji da bandeira ou "🌍" se não for um código válido.
     */
    private fun getFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) {
            return "🌍"
        }
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}