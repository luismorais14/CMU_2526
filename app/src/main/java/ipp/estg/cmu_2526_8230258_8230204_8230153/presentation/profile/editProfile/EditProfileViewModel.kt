package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.ClassificationPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel responsável pela lógica do ecrã de Edição de Perfil.
 *
 * Este ViewModel carrega os dados atuais do utilizador, gere o estado de edição dos campos
 * e coordena a validação e a gravação das alterações (incluindo peso e password)
 * através do [AuthRepository].
 *
 * @property authRepository Repositório para operações de autenticação e dados de utilizador.
 */
class EditProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    /**
     * Estado da UI de edição de perfil, observável pelo Composable [EditProfileScreen].
     */
    val uiState = _uiState.asStateFlow()

    private var currentUserData: UserData? = null

    init {
        loadCurrentUser()
    }

    /**
     * Carrega os dados atuais do utilizador a partir do repositório
     * e inicializa o estado da UI com esses valores.
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.getCurrentUserData()

            result.onSuccess { user ->
                currentUserData = user
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nome = user.name,
                        email = user.email,
                        altura = user.height.toString(),
                        peso = if (user.weight > 0) user.weight.toString() else ""
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessagem = "Erro ao carregar dados"
                    )
                }
            }
        }
    }

    /**
     * Processa os eventos de interação da UI disparados pelo ecrã de edição.
     *
     * @param event O evento ocorrido (alteração de campo ou clique em guardar).
     */
    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnNameChanged -> _uiState.update { it.copy(nome = event.name) }
            is EditProfileEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.email) }
            is EditProfileEvent.OnHeightChanged -> _uiState.update { it.copy(altura = event.height) }
            is EditProfileEvent.OnNewPasswordChanged -> _uiState.update { it.copy(passwordNova = event.newPassword) }
            is EditProfileEvent.OnConfirmPasswordChanged -> _uiState.update { it.copy(passwordConfirmacao = event.confirmPassword) }
            is EditProfileEvent.OnWeightChanged -> _uiState.update { it.copy(peso = event.weight) }
            EditProfileEvent.OnSaveClicked -> saveChanges()
        }
    }

    /**
     * Executa a validação dos campos e, se bem-sucedido, guarda as alterações.
     *
     * Este método trata de:
     * 1. Validação de password (coincidência e comprimento).
     * 2. Atualização dos dados biométricos no Firestore.
     * 3. Atualização da password no Firebase Auth (se fornecida).
     * 4. Registo de um novo peso e atribuição de pontos se o peso tiver sido alterado.
     */
    private fun saveChanges() {
        val currentState = _uiState.value
        val originalUser = currentUserData ?: return
        val newWeight = currentState.peso.toDoubleOrNull() ?: originalUser.weight

        if (currentState.passwordNova.isNotEmpty()) {
            if (currentState.passwordNova != currentState.passwordConfirmacao) {
                _uiState.update { it.copy(errorMessagem = "As passwords não coincidem.") }
                return
            }
            if (currentState.passwordNova.length < 6) {
                _uiState.update { it.copy(errorMessagem = "A password deve ter pelo menos 6 caracteres.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessagem = null) }

            val weightToPreserve = if (originalUser.initialWeight == 0.0) {
                originalUser.weight
            } else {
                originalUser.initialWeight
            }

            val updatedUser = originalUser.copy(
                name = currentState.nome,
                email = currentState.email,
                height = currentState.altura.toDoubleOrNull() ?: originalUser.height,
                weight = newWeight,
                initialWeight = weightToPreserve
            )

            val firestoreResult = authRepository.saveUserData(updatedUser)

            var passwordError: String? = null

            if (currentState.passwordNova.isNotEmpty()) {
                val passResult = authRepository.updatePassword(currentState.passwordNova)
                passResult.onFailure { e ->
                    passwordError = "Dados guardados, mas erro ao mudar password: ${e.message}"
                }
            }

            firestoreResult.onSuccess {
                if (originalUser.weight != newWeight) {
                    authRepository.addPoints(ClassificationPoints.UPDATE_WEIGHT)

                    val today = LocalDate.now().toString()
                    authRepository.saveWeight(newWeight, today)
                }

                currentUserData = updatedUser

                if (passwordError == null) {
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessagem = passwordError) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessagem = "Erro ao guardar dados: ${e.message}") }
            }
        }
    }
}