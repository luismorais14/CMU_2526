package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search

import java.time.LocalDate

/**
 * Modelo de dados que representa um item alimentar individual nos resultados da pesquisa.
 *
 * Contém informações nutricionais essenciais e metadados sobre o alimento, normalizados
 * a partir da resposta da API externa ou de dados locais.
 *
 * @property id Identificador único do alimento (geralmente o ID da API externa).
 * @property name Nome ou descrição do alimento.
 * @property calories Valor energético do alimento em kcal (padrão: 0).
 * @property source A origem da informação (ex: "USDA", "Local").
 * @property protein Quantidade de proteína em gramas (padrão: 0).
 * @property carbs Quantidade de hidratos de carbono em gramas (padrão: 0).
 * @property fat Quantidade de lípidos/gordura em gramas (padrão: 0).
 * @property servingSize Descrição textual do tamanho da porção (ex: "100g", "1 unidade"), se disponível.
 */
data class FoodItem(
    val id: String,
    val name: String,
    val calories: Int=0,
    val source: String,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val servingSize: String? = null
)

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã de Pesquisa.
 *
 * Este estado é gerido pelo [SearchViewModel] e observado pela [SearchScreen] para renderizar
 * a interface de forma reativa.
 *
 * @property mealType O tipo de refeição para o qual se está a adicionar o alimento (ex: "Almoço", "Jantar").
 * @property selectedDate A data selecionada para o registo da refeição.
 * @property searchQuery O texto atual inserido pelo utilizador na barra de pesquisa.
 * @property isLoading Indica se uma operação de pesquisa ou de gravação está em curso (para mostrar spinners).
 * @property searchResults Lista de [FoodItem] encontrados correspondentes à pesquisa.
 * @property error Mensagem de erro a ser exibida na UI, caso ocorra uma falha (null se não houver erro).
 * @property isFoodAdded Flag que indica se um alimento foi adicionado com sucesso (usada para acionar navegação ou feedback).
 */
data class SearchUiState(
    val mealType: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val searchResults: List<FoodItem> = emptyList(),
    val error: String? = null,
    val isFoodAdded: Boolean = false
)