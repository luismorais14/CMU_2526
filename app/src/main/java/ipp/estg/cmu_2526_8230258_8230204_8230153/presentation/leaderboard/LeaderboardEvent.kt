package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã da Leaderboard.
 *
 * Estes eventos são disparados pela UI e enviados para o [LeaderboardViewModel]
 * para iniciar ações como carregar dados ou mudar o filtro da vista.
 */
sealed interface LeaderboardEvent {
    /**
     * Evento para alternar entre as vistas da Leaderboard (ex: Global para País).
     * (Pode ser substituído por [ViewChanged] se a lógica for mais complexa).
     */
    object ToggleView : LeaderboardEvent

    /**
     * Evento para solicitar um novo carregamento dos dados da Leaderboard.
     */
    object LoadLeaderboard : LeaderboardEvent

    /**
     * Evento disparado quando o filtro de vista da Leaderboard é alterado.
     * @property view O novo filtro de vista (ex: "Global", nome de um país).
     */
    data class ViewChanged(val view: String) : LeaderboardEvent

    /**
     * Evento disparado para atualizar o país do utilizador no estado, influenciando filtros.
     * @property country O novo nome do país do utilizador.
     */
    data class UserCountryChanged(val country: String) : LeaderboardEvent
}