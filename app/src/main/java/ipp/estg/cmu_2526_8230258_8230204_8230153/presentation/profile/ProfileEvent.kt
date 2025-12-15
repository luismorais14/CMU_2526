package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Perfil.
 *
 * Estes eventos são disparados pela UI ([ProfileScreen]) e enviados para o
 * [ProfileViewModel] para processamento.
 */
sealed interface ProfileEvent {

    /**
     * Evento disparado quando o utilizador solicita o término da sessão.
     */
    object OnLogout : ProfileEvent

    /**
     * Evento disparado quando o utilizador seleciona ou altera um plano de treino ativo.
     *
     * @property planId O ID do novo plano ativo.
     */
    data class UpdatePlan(val planId: Int) : ProfileEvent

    /**
     * Evento disparado quando o utilizador regista um novo peso.
     *
     * @property weight O valor do peso registado (em string para permitir validação no ViewModel).
     */
    data class SaveWeight(val weight: String) : ProfileEvent
}