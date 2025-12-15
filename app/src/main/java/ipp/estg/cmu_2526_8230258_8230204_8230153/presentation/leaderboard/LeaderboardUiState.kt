package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.leaderboard

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã da Leaderboard (Tabela de Classificação).
 *
 * Esta classe contém as informações necessárias para exibir a classificação dos utilizadores,
 * o estado de carregamento e o contexto da vista atual.
 *
 * @property currentView O filtro da vista atual (ex: "Global", "País").
 * @property leaderboardItems Lista de [LeaderboardItem] a serem exibidos.
 * @property isLoading Indica se os dados da classificação estão a ser carregados.
 * @property userCountry O país do utilizador atual (usado para aplicar o filtro de país por defeito).
 */
data class LeaderboardUiState(
    val currentView: String = "Global",
    val leaderboardItems: List<LeaderboardItem> = emptyList(),
    val isLoading: Boolean = false,
    val userCountry: String = "Portugal"
)

/**
 * Modelo de dados que representa uma entrada individual na Leaderboard.
 *
 * @property position A posição (ranking) do utilizador na lista.
 * @property username O nome de utilizador a ser exibido.
 * @property countryName O nome completo do país do utilizador.
 * @property countryCode O código ISO do país (ex: "PT").
 * @property score A pontuação total de gamificação do utilizador.
 * @property flagEmoji O emoji da bandeira do país.
 * @property isCurrentUser Flag que indica se esta entrada é a do utilizador logado (para destaque na UI).
 */
data class LeaderboardItem(
    val position: Int,
    val username: String,
    val countryName: String,
    val countryCode: String,
    val score: Int,
    val flagEmoji: String,
    val isCurrentUser: Boolean = false
)