package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Edição de Perfil.
 *
 * Estes eventos são disparados pela UI e enviados para o [EditProfileViewModel]
 * para processamento das alterações nos campos do perfil.
 */
sealed interface EditProfileEvent {

    /**
     * Evento disparado quando o campo do nome é alterado.
     * @property name O novo valor do nome.
     */
    data class OnNameChanged(val name: String) : EditProfileEvent

    /**
     * Evento disparado quando o campo do email é alterado.
     * @property email O novo valor do email.
     */
    data class OnEmailChanged(val email: String) : EditProfileEvent

    /**
     * Evento disparado quando o campo da altura é alterado.
     * @property height O novo valor da altura (em string).
     */
    data class OnHeightChanged(val height: String) : EditProfileEvent

    /**
     * Evento disparado quando o campo da nova password é alterado.
     * @property newPassword O novo valor da palavra-passe.
     */
    data class OnNewPasswordChanged(val newPassword: String) : EditProfileEvent

    /**
     * Evento disparado quando o campo de confirmação da password é alterado.
     * @property confirmPassword O valor de confirmação da palavra-passe.
     */
    data class OnConfirmPasswordChanged(val confirmPassword: String) : EditProfileEvent

    /**
     * Evento disparado quando o campo do peso é alterado.
     * @property weight O novo valor do peso (em string).
     */
    data class OnWeightChanged(val weight: String) : EditProfileEvent

    /**
     * Evento disparado quando o utilizador clica no botão para guardar todas as alterações.
     */
    object OnSaveClicked : EditProfileEvent
}