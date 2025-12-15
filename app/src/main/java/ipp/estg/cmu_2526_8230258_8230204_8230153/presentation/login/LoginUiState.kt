package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Login.
 *
 * Esta classe contém todos os dados e flags de controlo que a UI necessita para
 * renderizar o ecrã de forma reativa, sendo gerida pelo [LoginViewModel].
 *
 * @property email O valor atual inserido no campo de email.
 * @property password O valor atual inserido no campo da palavra-passe.
 * @property isLoading Indica se uma operação de login está em curso (para exibir um indicador de progresso).
 * @property loginError Mensagem de erro a ser exibida ao utilizador, caso o login falhe (nula se não houver erro).
 */
data class LoginUiState(
    val email : String = "",
    val password : String = "",
    val isLoading : Boolean = false,
    val loginError : String? = null
)