package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de negócio do ecrã de Recuperação de Password.
 *
 * Gere o estado da UI ([ForgetPasswordUiState]), processa eventos do utilizador
 * e coordena o envio do email de reposição de password através do [AuthRepository].
 *
 * @property repository O repositório responsável pelas operações de autenticação.
 */
class ForgetPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgetPasswordUiState())
    /**
     * Estado da UI de recuperação de password, observável pelo Composable.
     */
    val uiState: StateFlow<ForgetPasswordUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    /**
     * Fluxo de eventos de navegação, usado para emitir eventos one-shot que
     * acionam a navegação no ecrã (ex: após o sucesso do envio do email).
     */
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Processa os eventos (intenções) disparados pela Interface de Utilizador.
     *
     * @param event O evento específico ocorrido (ex: alteração de texto, clique em submeter).
     */
    fun onEvent(event: ForgetPasswordEvent) {
        when (event) {
            is ForgetPasswordEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, emailError = null) }
            }

            is ForgetPasswordEvent.Submit -> {
                sendEmail()
            }
        }
    }

    /**
     * Executa a tentativa de enviar o email de reposição de password.
     *
     * 1. Valida se o email está preenchido.
     * 2. Atualiza o estado para "carregando".
     * 3. Chama o [AuthRepository.sendPasswordReset].
     * 4. Emite evento de navegação ([NavigateToLogin]) em caso de sucesso ou
     * define o erro na UI em caso de falha.
     */
    private fun sendEmail() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "O email não pode estar vazio")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailError = null) }
            val result = repository.sendPasswordReset(email)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                _navigationEvent.emit(NavigationEvent.NavigateToLogin)
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        emailError = exception.message ?: "Erro ao enviar email"
                    )
                }
            }
        }
    }
}


/**
 * Eventos de navegação que ocorrem no fluxo de recuperação de password e devem ser tratados pela UI.
 */
sealed interface NavigationEvent {
    /**
     * Navegar de volta para o ecrã de Login (normalmente após o envio bem-sucedido do email).
     */
    object NavigateToLogin : NavigationEvent
}