package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.ClassificationPoints
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de negócio do processo de registo de utilizador.
 *
 * Gere um fluxo de registo multi-etapa (wizard), mantendo o estado de cada campo
 * (nome, email, password, dados biométricos), executando validações passo a passo
 * e coordenando a criação da conta e persistência dos dados via [AuthRepository].
 *
 * @property repository O repositório responsável pelas operações de autenticação e base de dados.
 */
class RegistrationViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    /**
     * Estado atual da UI do registo, observável pela View.
     * Contém os valores dos campos, o passo atual, mensagens de erro e estado de carregamento.
     */
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RegistrationNavigationEvent>()
    /**
     * Fluxo de eventos de navegação (one-off events) para sinalizar à View quando deve
     * transitar de ecrã (ex: registo concluído com sucesso).
     */
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Processa os eventos (intenções) disparados pela Interface de Utilizador.
     *
     * @param event O evento específico ocorrido (ex: alteração de texto, clique em 'Próximo', submissão).
     */
    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.name, errorMessage = null) }
            }
            is RegistrationEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }
            is RegistrationEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is RegistrationEvent.AgeChanged -> {
                _uiState.update { it.copy(age = event.age, errorMessage = null) }
            }
            is RegistrationEvent.WeightChanged -> {
                _uiState.update { it.copy(weight = event.weight, errorMessage = null) }
            }
            is RegistrationEvent.HeightChanged -> {
                _uiState.update { it.copy(height = event.height, errorMessage = null) }
            }
            is RegistrationEvent.FitnessGoalChanged -> {
                _uiState.update { it.copy(fitnessGoal = event.goal) }
            }
            is RegistrationEvent.ActivityLevelChanged -> {
                _uiState.update { it.copy(activityLevel = event.level) }
            }
            RegistrationEvent.NextStep -> {
                validateAndAdvance()
            }
            RegistrationEvent.PreviousStep -> {
                if (_uiState.value.currentStep > 0) {
                    _uiState.update {
                        it.copy(currentStep = it.currentStep - 1, errorMessage = null)
                    }
                }
            }
            RegistrationEvent.SubmitRegistration -> {
                registerUser()
            }
        }
    }

    /**
     * Valida os dados inseridos no passo atual antes de permitir o avanço.
     *
     * Se os dados forem válidos, incrementa o índice do passo atual (`currentStep`).
     * Caso contrário, atualiza o `uiState` com uma mensagem de erro apropriada.
     */
    private fun validateAndAdvance() {
        val currentState = _uiState.value
        var isValid = false
        var error: String? = null

        when (currentState.currentStep) {
            0 -> {
                if (currentState.name.isNotBlank()) isValid = true else error = "O nome é obrigatório"
            }
            1 -> {
                if (currentState.email.contains("@")) isValid = true else error = "Email inválido"
            }
            2 -> {
                if (currentState.password.length >= 6) isValid = true else error = "A password deve ter pelo menos 6 caracteres"
            }
            3 -> {
                if (currentState.age.isNotBlank()) isValid = true else error = "Indique a sua idade"
            }
            4 -> {
                if (currentState.weight.isNotBlank()) isValid = true else error = "Indique o seu peso"
            }
            5 -> {
                if (currentState.height.isNotBlank()) isValid = true else error = "Indique a sua altura"
            }
            else -> isValid = true
        }

        if (isValid) {
            if (currentState.currentStep < 7)
                _uiState.update { it.copy(currentStep = it.currentStep + 1, errorMessage = null) }
            else
                registerUser()
        } else {
            _uiState.update { it.copy(errorMessage = error) }
        }
    }

    /**
     * Executa o processo de registo final.
     *
     * 1. Tenta criar a conta de autenticação (Firebase Auth).
     * 2. Se bem-sucedido, cria o objeto [UserData] com todos os detalhes recolhidos.
     * 3. Grava os detalhes do utilizador na base de dados remota (Firestore).
     * 4. Atribui pontos iniciais de gamificação.
     * 5. Emite o evento de navegação em caso de sucesso ou define mensagem de erro em caso de falha.
     */
    private fun registerUser() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val authResult = repository.register(currentState.email, currentState.password, currentState.name)

            authResult.onSuccess {
                val userId = repository.getCurrentUserId()

                if (userId != null) {
                    val userData = UserData(
                        id = userId,
                        name = currentState.name,
                        email = currentState.email,
                        age = currentState.age.toIntOrNull() ?: 0,
                        weight = currentState.weight.toDoubleOrNull() ?: 0.0,
                        initialWeight = currentState.weight.toDoubleOrNull() ?: 0.0,
                        height = currentState.height.toDoubleOrNull() ?: 0.0,
                        fitnessGoal = currentState.fitnessGoal,
                        activityLevel = currentState.activityLevel,
                        points = ClassificationPoints.COMPLETE_PROFILE,
                        country = "PT"
                    )

                    val firestoreResult = repository.saveUserData(userData)

                    firestoreResult.onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        _navigationEvent.emit(RegistrationNavigationEvent.RegistrationComplete)
                    }.onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Conta criada, mas erro ao guardar detalhes: ${e.message}"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Erro ao obter ID do utilizador.")
                    }
                }

            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao registar"
                    )
                }
            }
        }
    }
}

/**
 * Eventos de navegação que ocorrem no fluxo de registo e devem ser tratados pela UI.
 */
sealed interface RegistrationNavigationEvent {
    /**
     * Indica que o registo foi concluído com sucesso e a app deve navegar para o ecrã principal.
     */
    object RegistrationComplete : RegistrationNavigationEvent
}