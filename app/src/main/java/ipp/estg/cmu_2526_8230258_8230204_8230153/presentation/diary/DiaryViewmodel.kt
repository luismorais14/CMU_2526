package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ipp.estg.cmu_2526_8230258_8230204_8230153.BuildConfig
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.AppDatabase
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.UserPreferences
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.FoodDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.MealDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.RetrofitHelper
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.MealWithFoods
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.utils.ClassificationPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel para o ecrã do Diário Alimentar.
 *
 * Responsável por gerir os dados de nutrição do utilizador (refeições, alimentos)
 * para uma data selecionada, sincronizando dados entre a base de dados local (Room)
 * e a cloud (Firebase/Firestore), e integrando sugestões de receitas de uma API externa (Spoonacular).
 *
 * @property application Contexto da aplicação.
 * @property repository Repositório de autenticação e cloud.
 * @property userPreferences Preferências do utilizador (para obter o plano ativo).
 */
class DiaryViewModel(
    application: Application,
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application) {

    private val _selectedDate = MutableLiveData(LocalDate.now())
    /** Data atualmente selecionada no diário. */
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _meals = MutableLiveData<List<MealUi>>(emptyList())
    /** Lista de refeições para a data selecionada, formatadas para a UI. */
    val meals: LiveData<List<MealUi>> = _meals

    private val _dailyCalories = MutableLiveData<Int>(0)
    /** Total de calorias consumidas na data selecionada. */
    val dailyCalories: LiveData<Int> = _dailyCalories

    private val _dailyProtein = MutableLiveData<Int>(0)
    /** Total de proteína consumida na data selecionada. */
    val dailyProtein: LiveData<Int> = _dailyProtein

    private val _dailyCarbs = MutableLiveData<Int>(0)
    /** Total de hidratos de carbono consumidos na data selecionada. */
    val dailyCarbs: LiveData<Int> = _dailyCarbs

    private val _dailyFat = MutableLiveData<Int>(0)
    /** Total de gordura consumida na data selecionada. */
    val dailyFat: LiveData<Int> = _dailyFat

    private val _isLoading = MutableLiveData<Boolean>(false)
    /** Indica se alguma operação (carregamento/sincronização) está em curso. */
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    /** Mensagem de erro a ser exibida na UI. */
    val error: LiveData<String?> = _error

    private val mealDao: MealDao
    private val foodDao: FoodDao

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private var hasAwardedDailyPoints = false

    private val _suggestions = MutableLiveData<List<FoodUi>>(emptyList())
    /** Sugestões de alimentos/refeições baseadas no plano ativo. */
    val suggestions: LiveData<List<FoodUi>> = _suggestions

    private val API_KEY = BuildConfig.SPOONACULAR_API_KEY

    init {
        val database = AppDatabase.getDatabase(application)
        mealDao = database.mealDao()
        foodDao = database.foodDao()

        val today = LocalDate.now()
        _selectedDate.value = today

        loadDataForDate(today.format(dateFormatter))
        observeActivePlan()
    }

    /**
     * Observa o Flow do ID do plano ativo para carregar as sugestões relevantes.
     */
    private fun observeActivePlan() {
        viewModelScope.launch {
            userPreferences.activePlanId.collect { planId ->
                loadSuggestionsForPlan(planId)
            }
        }
    }

    /**
     * Altera a data selecionada e recarrega os dados do diário para essa nova data.
     * @param date A nova data a selecionar.
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDataForDate(date.format(dateFormatter))
    }

    /**
     * Carrega os dados do diário (refeições e alimentos) para a data especificada.
     *
     * Se o utilizador estiver autenticado, tenta primeiro sincronizar os dados da cloud
     * para garantir que a BD local está atualizada.
     *
     * @param date A data no formato String (ISO_LOCAL_DATE).
     */
    private fun loadDataForDate(date: String) {
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            if (repository.isUserLoggedIn()) {
                syncCloudDataToLocal(date)
            }

            try {
                val mealsLiveData = mealDao.getMealsByDate(date)

                mealsLiveData.observeForever { mealsList ->
                    if (mealsList != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val mealsWithFoods = mealsList.map { meal ->
                                val foods = foodDao.getFoodsByMealId(meal.id)
                                MealWithFoods(
                                    meal = meal,
                                    foods = foods,
                                    totalCalories = foods.sumOf { it.calories }
                                )
                            }

                            val mealsUi = mealsWithFoods.map { mealWithFoods ->
                                MealUi(
                                    id = mealWithFoods.meal.id.toString(),
                                    type = stringToMealType(mealWithFoods.meal.mealType),
                                    foods = mealWithFoods.foods.map { food ->
                                        FoodUi(
                                            id = food.id.toString(),
                                            name = food.name,
                                            calories = food.calories,
                                            protein = food.protein,
                                            carbs = food.carbs,
                                            fat = food.fat
                                        )
                                    },
                                    totalCalories = mealWithFoods.totalCalories
                                )
                            }

                            val allFoods = mealsWithFoods.flatMap { it.foods }
                            val totalCalories = allFoods.sumOf { it.calories }
                            val totalProtein = allFoods.sumOf { it.protein }
                            val totalCarbs = allFoods.sumOf { it.carbs }
                            val totalFat = allFoods.sumOf { it.fat }

                            withContext(Dispatchers.Main) {
                                _meals.value = mealsUi
                                _dailyCalories.value = totalCalories
                                _dailyProtein.value = totalProtein
                                _dailyCarbs.value = totalCarbs
                                _dailyFat.value = totalFat

                                if (totalCalories > 0 && repository.isUserLoggedIn()) {
                                    viewModelScope.launch {
                                        repository.saveDailySummary(totalCalories, date)
                                    }
                                }
                                _isLoading.value = false
                            }
                        }
                    } else {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Sincroniza os dados de alimentos da cloud (Firestore) para a base de dados local (Room).
     *
     * Evita duplicados e assegura que as refeições correspondentes existem.
     *
     * @param date A data a sincronizar.
     */
    private suspend fun syncCloudDataToLocal(date: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = repository.getFoodsFromCloud(date)

                result.onSuccess { cloudFoods ->
                    cloudFoods.forEach { data ->
                        val name = data["name"] as? String ?: return@forEach
                        val calories = (data["calories"] as? Number)?.toInt() ?: 0
                        val protein = (data["protein"] as? Number)?.toInt() ?: 0
                        val carbs = (data["carbs"] as? Number)?.toInt() ?: 0
                        val fat = (data["fat"] as? Number)?.toInt() ?: 0
                        val mealType = data["mealType"] as? String ?: "lunch"

                        var mealId: Long
                        val existingMeal = mealDao.getMealByDateAndType(date, mealType)

                        if (existingMeal != null) {
                            mealId = existingMeal.id
                        } else {
                            val newMeal = MealEntity(date = date, mealType = mealType)
                            mealId = mealDao.insertMeal(newMeal)
                        }

                        val exists = foodDao.getFoodsByMealId(mealId).any {
                            it.name == name && it.calories == calories
                        }

                        if (!exists) {
                            val foodEntity = FoodEntity(
                                mealId = mealId,
                                name = name,
                                calories = calories,
                                protein = protein,
                                carbs = carbs,
                                fat = fat,
                                date = date,
                                mealType = mealType
                            )
                            foodDao.insertFood(foodEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Adiciona um alimento sugerido (da Spoonacular) à refeição 'Outras' do dia atual.
     *
     * @param foodUi O alimento a ser adicionado.
     */
    fun addSuggestionToMeal(foodUi: FoodUi) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateToInsert = LocalDate.now().format(dateFormatter)
                val targetMealType = "other"

                val mealId: Long
                val existingMeal = mealDao.getMealByDateAndType(dateToInsert, targetMealType)

                if (existingMeal != null) {
                    mealId = existingMeal.id
                } else {
                    val meal = MealEntity(date = dateToInsert, mealType = targetMealType)
                    mealId = mealDao.insertMeal(meal)
                }

                val foodEntity = FoodEntity(
                    mealId = mealId,
                    name = foodUi.name,
                    calories = foodUi.calories,
                    protein = foodUi.protein,
                    carbs = foodUi.carbs,
                    fat = foodUi.fat,
                    date = dateToInsert,
                    mealType = targetMealType
                )

                val savedId = foodDao.insertFood(foodEntity)

                withContext(Dispatchers.Main) {
                    loadDataForDate(dateToInsert)
                }

                if (repository.isUserLoggedIn()) {
                    val foodToSync = foodEntity.copy(id = savedId)
                    repository.saveFoodToCloud(foodToSync)

                    withContext(Dispatchers.Main) {
                        val currentTotal = _dailyCalories.value ?: 0
                        repository.saveDailySummary(currentTotal + foodUi.calories, dateToInsert)
                    }

                    repository.addPoints(ClassificationPoints.REGISTER_MEAL)
                }

            } catch (e: Exception) {
                _error.postValue("Erro ao adicionar sugestão: ${e.message}")
            }
        }
    }

    /**
     * Apaga um alimento da base de dados local e da cloud (se logado).
     *
     * Se a refeição ficar vazia após a remoção, a refeição também é apagada.
     *
     * @param foodId O ID do alimento a apagar.
     * @param mealId O ID da refeição à qual o alimento pertence.
     */
    fun deleteFood(foodId: Long, mealId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val food = foodDao.getFoodById(foodId)

                if (food != null) {
                    foodDao.deleteFood(food)

                    if (repository.isUserLoggedIn()) {
                        repository.deleteFoodFromCloud(food)
                    }
                }

                val remainingFoods = foodDao.getFoodsByMealId(mealId)
                if (remainingFoods.isEmpty()) {
                    val meal = mealDao.getMealById(mealId)
                    meal?.let { mealDao.deleteMeal(it) }
                }

                withContext(Dispatchers.Main) {
                    val currentDateStr = _selectedDate.value!!.format(dateFormatter)
                    loadDataForDate(currentDateStr)
                }

            } catch (e: Exception) {
                _error.postValue("Erro ao apagar alimento: ${e.message}")
            }
        }
    }

    /**
     * Carrega sugestões de refeições da Spoonacular API, ajustadas ao plano ativo.
     *
     * @param planId O ID do plano ativo (1, 2, 3, 4 ou null).
     */
    private fun loadSuggestionsForPlan(planId: Int?) {
        viewModelScope.launch {
            try {
                val (diet, minProtein, maxPrice) = when (planId) {
                    1 -> Triple("ketogenic", null, null)
                    2 -> Triple(null, 25, null)
                    3 -> Triple("vegetarian", null, null)
                    else -> Triple(null, null, null)
                }

                val response = RetrofitHelper.spoonacularApi.getRecipesByDiet(
                    apiKey = API_KEY,
                    diet = diet,
                    minProtein = minProtein,
                    maxPrice = maxPrice,
                    number = 5
                )

                val suggestionsList = response.results.map { recipe ->
                    fun getNutrient(name: String): Int {
                        return recipe.nutrition?.nutrients
                            ?.find { it.name.equals(name, ignoreCase = true) }
                            ?.amount?.toInt() ?: 0
                    }

                    FoodUi(
                        id = "spoon_${recipe.id}",
                        name = recipe.title,
                        calories = getNutrient("Calories"),
                        protein = getNutrient("Protein"),
                        carbs = getNutrient("Carbohydrates"),
                        fat = getNutrient("Fat")
                    )
                }
                _suggestions.value = suggestionsList

            } catch (e: Exception) {
                e.printStackTrace()
                _suggestions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Verifica se o utilizador atingiu a meta calórica diária e atribui pontos
     * uma única vez por dia.
     *
     * @param currentCalories O total de calorias consumidas até agora.
     * @param goal O objetivo calórico diário do utilizador.
     */
    fun checkDailyGoal(currentCalories: Int, goal: Int) {
        if (currentCalories < goal) return
        if (hasAwardedDailyPoints) return

        viewModelScope.launch {
            val todayStr = LocalDate.now().format(dateFormatter)
            val userResult = repository.getCurrentUserData()

            userResult.onSuccess { user ->
                if (user.lastCaloriesUpdate != todayStr) {
                    repository.addPoints(ClassificationPoints.DAILY_CALORIE_GOAL)
                    repository.updateDailyBonusDate(todayStr)
                    hasAwardedDailyPoints = true
                }
            }
        }
    }

    /**
     * Converte o nome da refeição em String (usado no Room) para o enum [MealType] da UI.
     */
    private fun stringToMealType(type: String): MealType = when (type.lowercase()) {
        "breakfast" -> MealType.BREAKFAST
        "lunch" -> MealType.LUNCH
        "snack" -> MealType.SNACK
        "dinner" -> MealType.DINNER
        "other" -> MealType.OTHER
        else -> MealType.LUNCH
    }
}