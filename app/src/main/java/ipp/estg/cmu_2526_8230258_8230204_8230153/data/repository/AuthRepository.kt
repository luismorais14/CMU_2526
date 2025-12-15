package ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.UserPreferences
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.dao.MealDao
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.FirebaseAuthSource
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.FirestoreSource
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val firestoreSource: FirestoreSource,
    private val firebaseAuthSource: FirebaseAuthSource = FirebaseAuthSource(),
    private val userPreferences: UserPreferences,
    private val mealDao: MealDao
) {

    /**
     * Observa o estado de autenticação em tempo real.
     * Emite o utilizador atual ou null se não estiver logado.
     */
    fun getAuthState(): Flow<FirebaseUser?> {
        return firebaseAuthSource.observeAuthState()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    suspend fun getCurrentUserData(): Result<UserData> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.getUserData(uid)
    }

    fun getLeaderboard(): Flow<List<UserData>> {
        return firestoreSource.getLeaderboardRealTime()
    }


    suspend fun addPoints(points: Int): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.addPoints(uid, points)
    }

    suspend fun updateDailyBonusDate(date: String): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.updateLastCaloriesUpdate(uid, date)
    }

    suspend fun updateActivePlan(planId: Int): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.updateActivePlan(uid, planId)
    }

    suspend fun getWeightHistory(): Result<List<Double>> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.getWeightHistory(uid)
    }

    suspend fun saveWeight(weight: Double, date: String): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.saveWeightRecord(uid, weight, date)
    }

    suspend fun getWeeklyCalories(): Result<List<Float>> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.getWeeklyCalories(uid)
    }

    suspend fun saveDailySummary(totalCalories: Int, date: String): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.saveDailySummary(uid, date, totalCalories)
    }

    suspend fun saveFoodToCloud(food: FoodEntity): Result<Boolean> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.saveFoodToCloud(uid, food)
    }

    suspend fun getFoodsFromCloud(date: String): Result<List<Map<String, Any>>> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return firestoreSource.getFoodsFromCloud(uid, date)
    }

    suspend fun deleteFoodFromCloud(food: FoodEntity) {
        val uid = getCurrentUserId() ?: return
        firestoreSource.deleteFoodFromCloud(uid, food.date, food.name, food.calories)
    }


    suspend fun login(email: String, pass: String): Result<Boolean> {
        return firebaseAuthSource.login(email, pass)
    }

    suspend fun register(email: String, pass: String, name: String): Result<Boolean> {
        val authResult = firebaseAuthSource.register(email, pass)

        if (authResult.isSuccess) {
            val uid = firebaseAuthSource.getCurrentUserId()
            if (uid != null) {
                val newUser = UserData(
                    id = uid,
                    email = email,
                    name = name,
                    points = 0
                )
                return firestoreSource.saveUser(newUser)
            }
        }
        return Result.failure(Exception("Falha ao criar utilizador"))
    }

    suspend fun logout() {
        mealDao.deleteAllMeals()
        userPreferences.clear()
        firebaseAuthSource.logout()
    }

    suspend fun sendPasswordReset(email: String): Result<Boolean> {
        return firebaseAuthSource.sendPasswordReset(email)
    }

    suspend fun saveUserData(user: UserData): Result<Boolean> {
        return firestoreSource.saveUser(user)
    }

    suspend fun updatePassword(newPass: String): Result<Boolean> {
        return firebaseAuthSource.updatePassword(newPass)
    }

    fun getWeightHistoryFlow(): Flow<List<Double>> {
        val uid = getCurrentUserId() ?: return kotlinx.coroutines.flow.emptyFlow()
        return firestoreSource.observeWeightHistory(uid)
    }

    fun getWeeklyCaloriesFlow(): Flow<List<Float>> {
        val uid = getCurrentUserId() ?: return kotlinx.coroutines.flow.emptyFlow()
        return firestoreSource.observeWeeklyCalories(uid)
    }
}