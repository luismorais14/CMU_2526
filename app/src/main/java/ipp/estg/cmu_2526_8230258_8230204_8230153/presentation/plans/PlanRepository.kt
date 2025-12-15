package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.plans

import ipp.estg.cmu_2526_8230258_8230204_8230153.BuildConfig
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.RetrofitHelper

/**
 * Modelo de dados que representa uma sugestão de refeição para um plano alimentar.
 *
 * Contém informações básicas para apresentar uma sugestão de refeição no ecrã de detalhes do plano.
 *
 * @property id Identificador único da receita (da Spoonacular API).
 * @property mealType O tipo de refeição sugerida (ex: "Pequeno Almoço", "Almoço", "Jantar").
 * @property foodName O nome da receita.
 * @property calories O valor calórico estimado da receita.
 * @property imageUrl O URL da imagem da receita.
 */
data class PlanMealSuggestion(
    val id: Int,
    val mealType: String,
    val foodName: String,
    val calories: Int,
    val imageUrl: String?
)

/**
 * Objeto Repositório responsável pela lógica de obtenção de sugestões de refeições
 * a partir da API externa (Spoonacular) com base no ID do plano selecionado.
 */
object PlanRepository {
    private const val API_KEY = BuildConfig.SPOONACULAR_API_KEY

    /**
     * Obtém uma lista de sugestões de refeições para um plano alimentar específico.
     *
     * Define os parâmetros de pesquisa (dieta, proteína mínima, preço máximo) com base
     * no [planId] e utiliza a [RetrofitHelper.spoonacularApi] para obter as receitas.
     *
     * @param planId O ID do plano de refeição (ex: 1 para Limpa, 2 para Económico, etc.).
     * @return Uma lista de [PlanMealSuggestion] ou uma lista vazia em caso de erro.
     */
    suspend fun getSuggestionsForPlan(planId: Int): List<PlanMealSuggestion> {
        return try {
            var diet: String? = null
            var minProtein: Int? = null
            var maxPrice: Int? = null

            when (planId) {
                2 -> maxPrice = 150
                3 -> minProtein = 30
                4 -> diet = "ketogenic"
                else -> diet = "vegetarian"
            }

            val response = RetrofitHelper.spoonacularApi.getRecipesByDiet(
                apiKey = API_KEY,
                diet = diet,
                minProtein = minProtein,
                maxPrice = maxPrice,
                number = 3
            )
            println("DEBUG_APP: Sucesso! Recebidas ${response.results.size} receitas.")

            response.results.mapIndexed { index, recipe ->

                val calories = recipe.nutrition?.nutrients?.find {
                    it.name == "Calories"
                }?.amount?.toInt() ?: 0

                val type = when (index) {
                    0 -> "Pequeno Almoço"
                    1 -> "Almoço"
                    else -> "Jantar"
                }

                PlanMealSuggestion(
                    id = recipe.id,
                    mealType = type,
                    foodName = recipe.title,
                    calories = calories,
                    imageUrl = recipe.image
                )
            }
        } catch (e: Exception) {
            println("DEBUG_APP: ERRO NA API: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}