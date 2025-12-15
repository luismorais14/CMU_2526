package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary

import ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans.PlanMealSuggestion
import java.time.LocalDate

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã do Diário Alimentar.
 *
 * Contém todos os dados e métricas necessárias para renderizar o diário,
 * incluindo o balanço nutricional, as refeições e o estado de carregamento.
 */
data class DiaryUiState(
    /** A data atualmente selecionada e exibida no diário. */
    val selectedDate: LocalDate = LocalDate.now(),

    /** Total de calorias consumidas na data selecionada. */
    val dailyCalories : Int = 700,
    /** O objetivo calórico diário definido pelo utilizador. */
    val goalCalories : Int = 2000,
    /** Total de proteína consumida (em gramas). */
    val dailyProtein : Int = 45,
    /** Total de hidratos de carbono consumidos (em gramas). */
    val dailyCarbs : Int = 150,
    /** Total de gordura consumida (em gramas). */
    val dailyFat : Int = 40,

    /** Lista de refeições registadas para a data selecionada. */
    val meals: List<MealUi> = emptyList(),
    /** Indica se os dados do diário estão a ser carregados/sincronizados. */
    val isLoading : Boolean = false,
    /** Mensagem de erro a ser exibida na UI (nula se não houver erro). */
    val error : String? = null,
    /** Sugestões de alimentos/refeições obtidas da API ou plano ativo. */
    val suggestions : List<FoodUi> = emptyList()
)

/**
 * Modelo de dados para representar uma Refeição na UI.
 *
 * @property id Identificador da refeição.
 * @property type O tipo de refeição (Pequeno Almoço, Almoço, etc.).
 * @property foods Lista de [FoodUi] que compõem esta refeição.
 * @property totalCalories O total de calorias desta refeição.
 */
data class MealUi(
    val id: String,
    val type: MealType,
    val foods: List<FoodUi> = emptyList(),
    val totalCalories: Int = 0
)

/**
 * Modelo de dados para representar um Alimento na UI.
 *
 * @property id Identificador do alimento (pode ser da base de dados local ou da API externa).
 * @property name O nome do alimento.
 * @property calories O valor calórico.
 * @property protein Quantidade de proteína em gramas.
 * @property carbs Quantidade de hidratos de carbono em gramas.
 * @property fat Quantidade de gordura em gramas.
 */
data class FoodUi(
    val id: String,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

/**
 * Enumeração dos diferentes tipos de refeição.
 */
enum class MealType {
    BREAKFAST, LUNCH, SNACK, DINNER, OTHER
}