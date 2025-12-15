package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao

import androidx.room.*
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity) : Long

    @Query("SELECT * FROM foods WHERE date = :date AND mealType = :mealType")
    fun getFoodsByMeal(date: String, mealType: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE date = :date")
    fun getFoodsByDate(date: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    fun getFoodById(id: Long): FoodEntity?

    @Query("SELECT * FROM foods WHERE mealId = :mealId")
    fun getFoodsByMealId(mealId: Long): List<FoodEntity>

    @Query("DELETE FROM foods WHERE mealId = :mealId")
    suspend fun deleteFoodsByMealId(mealId: Long)

    @Delete
    suspend fun deleteFood(food: FoodEntity)

    @Query("DELETE FROM foods WHERE id = :foodId")
    suspend fun deleteFoodById(foodId: Long)
}