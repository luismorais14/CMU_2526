package ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.FoodDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.MealDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity

class DiaryRepository(
    private val foodDao: FoodDao,
    private val mealDao: MealDao
) {

    suspend fun insertMeal(meal: MealEntity): Long {
        return mealDao.insertMeal(meal)
    }

    suspend fun insertFood(food: FoodEntity): Long {
        return foodDao.insertFood(food)
    }

    suspend fun getMealByDateAndType(date: String, mealType: String): MealEntity? {
        return mealDao.getMealByDateAndType(date, mealType)
    }

    suspend fun getMealsWithFoodsByDate(date: String): List<MealWithFoods> {
        val meals = mealDao.getMealsByDateSync(date)

        return meals.map { meal ->
            val foods = foodDao.getFoodsByMealId(meal.id)

            MealWithFoods(
                meal = meal,
                foods = foods,
                totalCalories = foods.sumOf { it.calories }
            )
        }
    }

    suspend fun getFoodsByMealId(mealId: Long): List<FoodEntity> {
        return foodDao.getFoodsByMealId(mealId)
    }

    suspend fun getFoodById(foodId: Long): FoodEntity? {
        return foodDao.getFoodById(foodId)
    }

    suspend fun deleteFoodById(foodId: Long) {
        foodDao.deleteFoodById(foodId)
    }

    suspend fun deleteMealById(mealId: Long) {
        mealDao.deleteMealById(mealId)
    }
}