package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.BuildConfig
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.AppDatabase
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.RetrofitHelper
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.DiaryRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.ClassificationPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel responsável pela lógica do ecrã de pesquisa de alimentos.
 *
 * Gere o estado da pesquisa ([SearchUiState]), comunica com a API externa de nutrição (USDA)
 * para obter dados de alimentos e coordena a persistência desses dados na base de dados local (Room)
 * e remota (Firestore) através dos repositórios.
 *
 * @property application Contexto da aplicação, necessário para inicializar a base de dados Room.
 * @property savedStateHandle Permite recuperar argumentos de navegação (tipo de refeição e data).
 * @property authRepository Repositório para gestão de autenticação e sincronização cloud.
 */
class SearchViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val repository: DiaryRepository
    private val mealType: String = savedStateHandle.get<String>("mealType") ?: "Almoço"

    private val selectedDate: LocalDate = savedStateHandle.get<String>("date")
        ?.let {
            try { LocalDate.parse(it) } catch (e: Exception) { LocalDate.now() }
        } ?: LocalDate.now()

    private val _uiState = MutableStateFlow(SearchUiState(mealType = mealType, selectedDate = selectedDate))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DiaryRepository(
            foodDao = database.foodDao(),
            mealDao = database.mealDao()
        )
    }

    /**
     * Processa os eventos de interação da UI disparados pelo ecrã de pesquisa.
     *
     * @param event O evento ocorrido (alteração de texto, clique em pesquisar ou seleção de alimento).
     */
    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                if (event.query.length >= 2) {
                    viewModelScope.launch {
                        performSearch(event.query)
                    }
                } else {
                    _uiState.update { it.copy(searchResults = emptyList()) }
                }
            }
            SearchEvent.OnSearch -> {
                performSearch(_uiState.value.searchQuery)
            }
            is SearchEvent.OnFoodClicked -> {
                addFoodToMeal(event.food, _uiState.value.selectedDate)
            }
        }
    }

    /**
     * Executa uma pesquisa na API de Nutrição com base na query fornecida.
     *
     * Filtra os resultados para ignorar itens sem calorias e mapeia a resposta da API
     * para objetos de domínio [FoodItem]. Atualiza o estado da UI com os resultados ou erros.
     *
     * @param query O termo de pesquisa inserido pelo utilizador.
     */
    private fun performSearch(query: String) {
        if (query.length < 2) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.NUTRICION_API_KEY

                val apiResponse = RetrofitHelper.nutritionApi.getNutrition(
                    apiKey = apiKey,
                    query = query,
                    pageSize = 10
                )

                val foodItems = apiResponse.foods.mapNotNull { usdaFood ->
                    val nutrients = usdaFood.foodNutrients.associateBy { it.nutrientName }
                    val calories = nutrients["Energy"]?.value?.toInt() ?: 0

                    if (calories > 0) {
                        FoodItem(
                            id = usdaFood.fdcId.toString(),
                            name = usdaFood.description.replaceFirstChar { it.uppercase() },
                            calories = calories,
                            source = "USDA",
                            protein = nutrients["Protein"]?.value?.toInt() ?: 0,
                            carbs = nutrients["Carbohydrate, by difference"]?.value?.toInt() ?: 0,
                            fat = nutrients["Total lipid (fat)"]?.value?.toInt() ?: 0,
                            servingSize = if (usdaFood.servingSize != null)
                                "${usdaFood.servingSize}${usdaFood.servingSizeUnit ?: "g"}"
                            else null
                        )
                    } else null
                }

                _uiState.update { it.copy(searchResults = foodItems, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro: ${e.message}") }
            }
        }
    }

    /**
     * Atualiza a data selecionada no estado da UI.
     * Isto é útil se a data for alterada externamente ou através de argumentos.
     *
     * @param date A nova data a ser considerada para adicionar a refeição.
     */
    fun updateSelectedDate(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }

    /**
     * Adiciona o alimento selecionado à base de dados local e, se aplicável, sincroniza com a cloud.
     *
     * O processo envolve:
     * 1. Verificar se já existe uma [MealEntity] (Refeição) para a data e tipo. Se não, cria uma.
     * 2. Inserir o alimento ([FoodEntity]) associado a essa refeição.
     * 3. Se o utilizador estiver logado, envia o alimento para o Firestore e atribui pontos de gamificação.
     *
     * @param foodItem O item alimentar selecionado da pesquisa.
     * @param date A data para a qual o alimento deve ser registado.
     */
    private fun addFoodToMeal(foodItem: FoodItem, date: LocalDate) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val dateString = date.format(dateFormatter)

                val mealTypeEnum = when (mealType.lowercase()) {
                    "pequeno-almoço", "breakfast" -> "breakfast"
                    "almoço", "lunch" -> "lunch"
                    "lanche", "snack" -> "snack"
                    "jantar", "dinner" -> "dinner"
                    "outros", "other" -> "other"
                    else -> "lunch"
                }

                withContext(Dispatchers.IO) {

                    var mealId: Long
                    val existingMeal = repository.getMealByDateAndType(dateString, mealTypeEnum)

                    if (existingMeal != null) {
                        mealId = existingMeal.id
                    } else {
                        val meal = MealEntity(date = dateString, mealType = mealTypeEnum)
                        mealId = repository.insertMeal(meal)
                    }

                    val foodEntity = FoodEntity(
                        mealId = mealId,
                        name = foodItem.name,
                        calories = foodItem.calories,
                        protein = foodItem.protein,
                        carbs = foodItem.carbs,
                        fat = foodItem.fat,
                        date = dateString,
                        mealType = mealTypeEnum
                    )

                    val savedId = repository.insertFood(foodEntity)

                    if (authRepository.isUserLoggedIn()) {
                        val foodToSync = foodEntity.copy(id = savedId)
                        authRepository.saveFoodToCloud(foodToSync)

                        authRepository.addPoints(ClassificationPoints.REGISTER_MEAL)
                    }
                }

                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        searchResults = emptyList(),
                        isLoading = false,
                        error = null,
                        isFoodAdded = true
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(isLoading = false, error = "Erro ao adicionar: ${e.message}")
                }
            }
        }
    }
}