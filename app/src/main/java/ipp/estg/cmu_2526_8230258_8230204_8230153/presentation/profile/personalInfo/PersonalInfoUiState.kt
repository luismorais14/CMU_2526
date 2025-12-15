package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.personalInfo

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Informações Pessoais.
 *
 * Esta classe armazena os dados do perfil do utilizador de forma formatada e pronta a ser
 * exibida na UI, sendo gerida pelo [PersonalInfoViewModel].
 *
 * @property isLoading Indica se os dados do perfil estão a ser carregados (para exibir um indicador de progresso).
 * @property errorMessage Mensagem de erro a ser exibida (nula se não houver erro).
 * @property nome O nome do utilizador.
 * @property email O endereço de email do utilizador.
 * @property altura A altura do utilizador, já formatada com unidades (ex: "180 cm").
 * @property pesoInicial O peso inicial registado, já formatado com unidades (ex: "75 kg").
 * @property pesoAtual O peso mais recente registado, já formatado com unidades.
 * @property metaPeso A meta de peso ou fitness do utilizador (ex: "Perder Peso", "Manter/Ganhar").
 */
data class PersonalInfoUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val nome: String = "",
    val email: String = "",
    val altura: String = "",
    val pesoInicial: String = "",
    val pesoAtual: String = "",
    val metaPeso: String = "Não definido"
)