package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Query("SELECT * FROM meals WHERE date = :date AND mealType = :type LIMIT 1")
    suspend fun getMealByDateAndType(date: String, type: String): MealEntity?

    @Query("SELECT * FROM meals WHERE date = :date")
    fun getMealsByDate(date: String): LiveData<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMealById(mealId: Long): MealEntity?

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Query("SELECT * FROM meals WHERE date = :date")
    suspend fun getMealsByDateSync(date: String): List<MealEntity>

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()
}