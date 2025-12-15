package ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity

data class MealWithFoods(
    val meal: MealEntity,
    val foods: List<FoodEntity>,
    val totalCalories: Int
)