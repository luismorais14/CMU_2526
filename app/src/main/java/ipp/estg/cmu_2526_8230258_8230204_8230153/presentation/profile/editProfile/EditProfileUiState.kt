package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.profile.editProfile

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Edição de Perfil.
 *
 * Esta classe armazena os valores dos campos que podem ser editados pelo utilizador,
 * bem como flags de controlo de estado (carregamento, erros, sucesso na gravação).
 *
 * @property nome O nome do utilizador (valor em edição).
 * @property email O endereço de email (valor em edição).
 * @property altura A altura do utilizador (valor em edição, em cm).
 * @property passwordNova O novo valor da palavra-passe que o utilizador deseja definir.
 * @property passwordConfirmacao Confirmação da nova palavra-passe.
 * @property peso O peso atual do utilizador (valor em edição, em kg).
 * @property isLoading Indica se os dados estão a ser carregados ou guardados (true enquanto a operação de rede decorre).
 * @property errorMessagem Mensagem de erro a ser exibida na UI (nula se não houver erro).
 * @property saveSuccess Flag que indica se a última tentativa de gravação de dados foi bem-sucedida (usada para fechar o ecrã).
 */
data class EditProfileUiState(
    val nome: String = "",
    val email: String = "",
    val altura: String = "",
    val passwordNova: String = "",
    val passwordConfirmacao: String = "",
    val peso: String = "",
    val isLoading: Boolean = true,
    val errorMessagem: String? = null,
    val saveSuccess: Boolean = false
)