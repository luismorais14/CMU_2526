package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.registration

/**
 * Representa o estado da Interface de Utilizador (UI) para o fluxo de registo.
 *
 * Esta classe armazena todos os dados inseridos pelo utilizador ao longo das várias etapas
 * do assistente de registo (wizard), bem como o estado de controlo da UI (passo atual, carregamento, erros).
 *
 * @property name O nome completo inserido pelo utilizador.
 * @property email O endereço de email inserido.
 * @property password A palavra-passe escolhida pelo utilizador.
 * @property age A idade inserida como texto (para permitir validação de input antes da conversão numérica).
 * @property weight O peso inserido como texto.
 * @property height A altura inserida como texto.
 * @property fitnessGoal O objetivo de fitness selecionado (ex: "Perder Peso", "Ganhar Músculo").
 * @property activityLevel O nível de atividade física selecionado (ex: "Sedentário", "Ativo").
 * @property currentStep O índice do passo atual no processo de registo (inicia em 0).
 * @property isLoading Indica se uma operação de rede ou processamento está em curso (para exibir spinners).
 * @property errorMessage Mensagem de erro a ser exibida ao utilizador (null se não houver erros).
 */
data class RegistrationUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val fitnessGoal: String = "",
    val activityLevel: String = "",
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)