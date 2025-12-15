package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.UserPreferences
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica do ecrã de Perfil do utilizador.
 *
 * Gere o estado da UI ([ProfileUiState]), carregando e mantendo as informações do perfil,
 * o histórico de peso/calorias em tempo real (usando Flows) e gerindo ações como logout e
 * a atualização do plano de treino ativo.
 *
 * @property repository Repositório de autenticação e dados de utilizador, responsável pela sincronização com o Firebase.
 * @property userPreferences DataStore para persistir informações leves e rápidas (ex: ID do plano ativo).
 */
class ProfileViewModel(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    /**
     * Estado da UI do perfil, observável pelo Composable [ProfileScreen].
     */
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<ProfileNavigationEvent>()
    /**
     * Fluxo de eventos de navegação (one-off events) para sinalizar à UI que deve navegar
     * (ex: após o logout).
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadUserProfile()
        observeActivePlan()
        observeChartsData()
    }

    /**
     * Observa o ID do plano de treino ativo armazenado localmente no DataStore.
     *
     * Este Flow garante que o estado da UI é atualizado automaticamente sempre que
     * o plano ativo é alterado (por exemplo, a partir do ecrã de Planos).
     */
    private fun observeActivePlan() {
        viewModelScope.launch {
            userPreferences.activePlanId.collect { planId ->
                _uiState.update { it.copy(activePlanId = planId) }
            }
        }
    }

    /**
     * Inicia a observação em tempo real (Realtime) dos dados necessários para os gráficos.
     *
     * Utiliza Flows do repositório, que estão ligados a 'snapshotListeners' do Firestore,
     * garantindo que os gráficos ([weightHistory] e [weeklyCalories]) se atualizem
     * automaticamente quando a base de dados remota muda.
     */
    private fun observeChartsData() {
        viewModelScope.launch {
            launch {
                repository.getWeightHistoryFlow().collect { history ->
                    _uiState.update { it.copy(weightHistory = history) }
                }
            }

            launch {
                repository.getWeeklyCaloriesFlow().collect { calories ->
                    _uiState.update { it.copy(weeklyCalories = calories) }
                }
            }
        }
    }

    /**
     * Carrega os dados estáticos do perfil do utilizador (nome, pontos, etc.)
     * e os dados iniciais do histórico de peso.
     *
     * Este é um carregamento inicial, não em tempo real.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getCurrentUserData().onSuccess { user ->
                _uiState.update {
                    it.copy(
                        userData = user,
                        points = user.points
                    )
                }
            }
            repository.getWeightHistory().onSuccess { history ->
                _uiState.update { it.copy(weightHistory = history) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Processa os eventos de interação da UI disparados pelo ecrã de Perfil.
     *
     * @param event O evento ocorrido (logout, atualização de plano, ou registo de novo peso).
     */
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLogout -> {
                viewModelScope.launch {
                    repository.logout()
                    _navigationEvent.send(ProfileNavigationEvent.NavigateToLogin)
                }
            }

            is ProfileEvent.UpdatePlan -> {
                viewModelScope.launch {
                    repository.updateActivePlan(event.planId)
                    userPreferences.saveActivePlan(event.planId)

                    _uiState.update { it.copy(activePlanId = event.planId) }
                }
            }

            is ProfileEvent.SaveWeight -> {
                viewModelScope.launch {
                    val weight = event.weight.toDoubleOrNull()
                    if (weight != null && weight > 0) {
                        val today = java.time.LocalDate.now().toString()
                        repository.saveWeight(weight, today)
                    }
                }
            }
        }
    }
}

/**
 * Eventos de navegação que ocorrem no fluxo do Perfil e devem ser tratados pela UI.
 */
sealed interface ProfileNavigationEvent {
    /**
     * Indica que o utilizador terminou a sessão e a aplicação deve navegar para o ecrã de Login.
     */
    object NavigateToLogin : ProfileNavigationEvent
}