package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Perfil.
 *
 * Este estado é gerido pelo [ProfileViewModel] e observado pela [ProfileScreen] para
 * apresentar dados do utilizador, gráficos de progresso e estado da aplicação de forma reativa.
 *
 * @property isLoading Indica se os dados do perfil ou gráficos estão a ser carregados (para mostrar spinners).
 * @property userData Os dados detalhados do utilizador logado ([UserData]), nulo se não houver dados.
 * @property activePlanId O identificador do plano de treino ativo atualmente selecionado pelo utilizador (null se não houver plano).
 * @property errorMessage Mensagem de erro a ser exibida na UI, caso ocorra uma falha (null se não houver erro).
 * @property weightHistory Lista de registos de peso do utilizador ao longo do tempo, usada para desenhar o gráfico de progresso.
 * @property weeklyCalories Lista de somatórios calóricos diários dos últimos 7 dias, usada para o gráfico de balanço energético.
 * @property points A pontuação total de gamificação do utilizador.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userData : UserData? = null,
    val activePlanId: Int? = null,
    val errorMessage: String? = null,
    val weightHistory: List<Double> = emptyList(),
    val weeklyCalories: List<Float> = emptyList(),
    val points: Int = 0
)