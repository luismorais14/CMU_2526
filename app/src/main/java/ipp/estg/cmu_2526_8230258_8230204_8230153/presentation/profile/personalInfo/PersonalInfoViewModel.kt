package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.personalInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de carregamento e gestão dos dados de
 * informações pessoais do utilizador.
 *
 * O seu principal objetivo é obter os dados mais recentes do utilizador autenticado
 * através do [AuthRepository] e mapeá-los para o estado da UI ([PersonalInfoUiState]).
 *
 * @property repository O repositório responsável por aceder aos dados de autenticação e de utilizador.
 */
class PersonalInfoViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInfoUiState())
    /**
     * Estado da UI de informações pessoais, observável pelo Composable [PersonalInfoScreen].
     */
    val uiState: StateFlow<PersonalInfoUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Carrega os dados do perfil do utilizador a partir do repositório.
     *
     * Após o carregamento, mapeia os dados brutos ([UserData]) para um formato
     * amigável à exibição na UI (ex: adiciona unidades de medida ou valores por defeito).
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.getCurrentUserData()

            result.onSuccess { userData ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nome = userData.name,
                        email = userData.email,

                        altura = if (userData.height > 0) "${userData.height} cm" else "Não definida",

                        pesoInicial = if (userData.initialWeight > 0) "${userData.initialWeight} kg" else "-- kg",
                        pesoAtual = if (userData.weight > 0) "${userData.weight} kg" else "-- kg",

                        metaPeso = userData.fitnessGoal.ifEmpty {
                            if (userData.weight < userData.initialWeight) "Perder Peso" else "Manter/Ganhar"
                        }
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados"
                    )
                }
            }
        }
    }

    /**
     * Processa eventos de interação (atualmente, nenhum evento é definido para este ecrã estático).
     *
     * @param event O evento disparado pela UI.
     */
    fun onEvent(event: PersonalInfoEvent) {
    }
}