package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,  // Formato: "2024-01-15"
    val mealType: String,
    val createdAt: Long = System.currentTimeMillis()
)