package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.FoodDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.MealDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.MealEntity

@Database(
    entities = [FoodEntity::class, MealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodLogger.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}