package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.login

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Login.
 *
 * Estes eventos representam todas as "ações" que o utilizador
 * pode fazer no ecrã de login, sendo processados pelo [LoginViewModel].
 */
sealed interface LoginEvent {
    /**
     * Evento disparado quando o valor do campo de email é alterado.
     * @property email O novo valor do email.
     */
    data class EmailChanged(val email : String) : LoginEvent

    /**
     * Evento disparado quando o valor do campo da password é alterado.
     * @property password O novo valor da password.
     */
    data class PasswordChanged(val password : String) : LoginEvent

    /**
     * Evento disparado quando o utilizador clica em "Esqueceu-se da Password?".
     */
    object ForgotPasswordClicked : LoginEvent

    /**
     * Evento disparado quando o utilizador clica no botão principal de "Login".
     */
    object LoginClicked : LoginEvent

    /**
     * Evento disparado quando o utilizador decide continuar a usar a aplicação sem autenticação.
     */
    object ContinueWithoutSession : LoginEvent

    /**
     * Evento disparado quando o utilizador clica para navegar para o ecrã de Registo.
     */
    object NavigateToRegistration : LoginEvent
}