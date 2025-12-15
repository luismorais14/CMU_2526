package ipp.estg.cmu_2526_8230258_8230204_8230153.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferences(private val context: Context) {

    companion object {
        val ACTIVE_PLAN_KEY = intPreferencesKey("active_plan_id")
    }

    val activePlanId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[ACTIVE_PLAN_KEY]
        }

    suspend fun saveActivePlan(planId: Int) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_PLAN_KEY] = planId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}