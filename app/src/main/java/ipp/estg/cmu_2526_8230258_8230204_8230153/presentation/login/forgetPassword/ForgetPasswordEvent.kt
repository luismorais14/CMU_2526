package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login.forgetPassword

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Recuperação de Password.
 *
 * Estes eventos são disparados pela UI e enviados para o [ForgetPasswordViewModel]
 * para iniciar o processo de reposição de password.
 */
sealed interface ForgetPasswordEvent {
    /**
     * Evento disparado quando o valor do campo de email é alterado.
     * @property email O novo valor do email.
     */
    data class EmailChanged(val email : String) : ForgetPasswordEvent

    /**
     * Evento disparado quando o utilizador clica no botão para submeter o pedido
     * de recuperação de password.
     */
    object Submit : ForgetPasswordEvent
}