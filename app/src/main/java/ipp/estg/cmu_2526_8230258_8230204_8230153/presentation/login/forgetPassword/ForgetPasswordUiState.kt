package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Recuperação de Password.
 *
 * Esta classe contém o valor do campo de email e flags de controlo de estado
 * (carregamento e erros), sendo gerida pelo [ForgetPasswordViewModel].
 *
 * @property email O valor atual inserido no campo de email.
 * @property emailError Mensagem de erro a ser exibida relacionada com o email (nula se não houver erro).
 * @property isLoading Indica se o processo de envio do email de reposição está em curso (para exibir um indicador de progresso).
 */
data class ForgetPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
)