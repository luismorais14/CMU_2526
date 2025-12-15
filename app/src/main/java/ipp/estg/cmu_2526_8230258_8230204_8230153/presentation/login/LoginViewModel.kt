package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de negócio do ecrã de Login.
 *
 * Gere o estado da UI ([LoginUiState]), processa os eventos de interação do utilizador
 * e coordena a autenticação através do [AuthRepository]. Também emite eventos de
 * navegação ([NavigationEvent]) para o coordenador da navegação.
 *
 * @property repository O repositório responsável pelas operações de autenticação.
 */
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    /**
     * Estado da UI de login, observável pelo Composable [LoginScreen].
     */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    /**
     * Fluxo de eventos de navegação, usado para emitir eventos one-shot que
     * acionam a navegação no ecrã.
     */
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Processa os eventos (intenções) disparados pela Interface de Utilizador.
     *
     * @param event O evento específico ocorrido (ex: alteração de texto, clique em login).
     */
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, loginError = null) }
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, loginError = null) }
            }

            is LoginEvent.LoginClicked -> {
                this.login()
            }

            is LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateToForgetPassword)
                }
            }

            is LoginEvent.ContinueWithoutSession -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
            }

            LoginEvent.NavigateToRegistration -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateToRegistration)
                }
            }
        }
    }

    /**
     * Executa a tentativa de autenticação (login).
     *
     * 1. Valida se os campos estão preenchidos.
     * 2. Atualiza o estado para "carregando".
     * 3. Chama o [AuthRepository.login].
     * 4. Emite evento de navegação ([NavigateToHome]) em caso de sucesso ou
     * define o erro na UI em caso de falha.
     */
    private fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginError = "Email e password não podem estar vazios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            val result = repository.login(email, password)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                _navigationEvent.emit(NavigationEvent.NavigateToHome)
            }.onFailure {
                _uiState.update {
                    it.copy(loginError = "Email ou password incorretos")
                }
            }
        }
    }
}


/**
 * Eventos de navegação que ocorrem no fluxo de login e devem ser tratados pela UI.
 */
sealed interface NavigationEvent {
    /**
     * Navegar para o ecrã principal da aplicação (Home/Diário).
     */
    object NavigateToHome : NavigationEvent
    /**
     * Navegar para o ecrã de recuperação de palavra-passe.
     */
    object NavigateToForgetPassword : NavigationEvent
    /**
     * Navegar para o ecrã de perfil.
     */
    object NavigateToProfile : NavigationEvent
    /**
     * Navegar para o ecrã de registo de novo utilizador.
     */
    object NavigateToRegistration : NavigationEvent
}