package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "foods",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealId: Long,
    val name: String,
    val calories: Int=0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val date: String,  // Formato: "2024-01-15"
    val mealType: String,
    val addedAt: Long = System.currentTimeMillis()
)