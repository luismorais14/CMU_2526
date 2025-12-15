package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Registo.
 *
 * Estes eventos são disparados pela UI ([RegistrationScreen]) e enviados para o
 * [RegistrationViewModel] para processamento, seguindo o fluxo unidirecional de dados.
 */
sealed interface RegistrationEvent {

    /**
     * Evento disparado quando o campo de nome é alterado.
     * @property name O novo valor do nome.
     */
    data class NameChanged(val name: String) : RegistrationEvent

    /**
     * Evento disparado quando o campo de email é alterado.
     * @property email O novo valor do email.
     */
    data class EmailChanged(val email: String) : RegistrationEvent

    /**
     * Evento disparado quando o campo de palavra-passe é alterado.
     * @property password O novo valor da palavra-passe.
     */
    data class PasswordChanged(val password: String) : RegistrationEvent

    /**
     * Evento disparado quando o campo de idade é alterado.
     * @property age O novo valor da idade.
     */
    data class AgeChanged(val age: String) : RegistrationEvent

    /**
     * Evento disparado quando o campo de peso é alterado.
     * @property weight O novo valor do peso.
     */
    data class WeightChanged(val weight: String) : RegistrationEvent

    /**
     * Evento disparado quando o campo de altura é alterado.
     * @property height O novo valor da altura.
     */
    data class HeightChanged(val height: String) : RegistrationEvent

    /**
     * Evento disparado quando o objetivo de fitness é selecionado.
     * @property goal O objetivo de fitness selecionado.
     */
    data class FitnessGoalChanged(val goal: String) : RegistrationEvent

    /**
     * Evento disparado quando o nível de atividade é selecionado.
     * @property level O nível de atividade selecionado.
     */
    data class ActivityLevelChanged(val level: String) : RegistrationEvent

    /**
     * Evento disparado para avançar para o próximo passo no assistente de registo.
     * Aciona a validação dos campos do passo atual.
     */
    object NextStep : RegistrationEvent

    /**
     * Evento disparado para voltar ao passo anterior no assistente de registo.
     */
    object PreviousStep : RegistrationEvent

    /**
     * Evento disparado para finalizar o processo, criar a conta e submeter os dados.
     */
    object SubmitRegistration : RegistrationEvent
}