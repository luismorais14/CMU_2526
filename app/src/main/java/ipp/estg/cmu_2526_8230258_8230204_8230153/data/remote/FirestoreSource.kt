package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote

import com.google.firebase.firestore.*
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.entity.FoodEntity
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun addPoints(userId: String, points: Int) : Result<Boolean> {
        return try {
            db.collection("users").document(userId)
                .update("points", FieldValue.increment(points.toLong()))
                .await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun getLeaderboardRealTime(): Flow<List<UserData>> = callbackFlow {
        val registration = db.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val users = snapshot.toObjects(UserData::class.java)
                    trySend(users)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun saveUser(user: UserData): Result<Boolean> {
        return try {
            db.collection("users").document(user.id).set(user).await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getUserData(userId: String): Result<UserData> {
        return try {
            val documentSnapshot = db.collection("users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.toObject(UserData::class.java)
                if (userData != null) {
                    Result.success(userData)
                } else {
                    Result.failure(Exception("User data is null"))
                }
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    suspend fun updateLastCaloriesUpdate(userId: String, date: String): Result<Boolean> {
        return try {
            db.collection("users").document(userId)
                .update("lastCaloriesUpdate", date)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getWeightHistory(uid: String): Result<List<Double>> {
        return try {
            val snapshot = db.collection("users").document(uid)
                .collection("weight_history")
                .orderBy("date", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .await()

            val weights = snapshot.documents.mapNotNull { it.getDouble("weight") }
            Result.success(weights)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklyCalories(uid: String): Result<List<Float>> {
        return try {
            val snapshot = db.collection("users").document(uid)
                .collection("diary")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(7)
                .get()
                .await()

            val calories = snapshot.documents.mapNotNull {
                it.getDouble("totalCalories")?.toFloat()
            }.reversed()

            Result.success(calories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveWeightRecord(uid: String, weight: Double, date: String): Result<Boolean> {
        return try {
            val data = mapOf(
                "weight" to weight,
                "date" to date
            )
            db.collection("users").document(uid)
                .collection("weight_records")
                .document(date)
                .set(data, SetOptions.merge())
                .await()

            db.collection("users").document(uid)
                .update("weight", weight)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveDailySummary(uid: String, date: String, totalCalories: Int): Result<Boolean> {
        return try {
            val data = mapOf(
                "totalCalories" to totalCalories,
                "date" to date
            )
            db.collection("users").document(uid)
                .collection("daily_summary")
                .document(date)
                .set(data, SetOptions.merge())
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActivePlan(userId: String, planId: Int) : Result<Boolean> {
        return try {
            db.collection("users").document(userId)
                .update("activePlanId", planId)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getFoodsFromCloud(uid: String, date: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = db.collection("users").document(uid)
                .collection("meals")
                .document(date)
                .collection("items")
                .get()
                .await()

            val foods = snapshot.documents.map { doc ->
                val data = doc.data ?: mutableMapOf()
                data["firestoreId"] = doc.id
                data
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFoodFromCloud(uid: String, date: String, foodName: String, calories: Int) {
        try {
            val query = db.collection("users").document(uid)
                .collection("meals").document(date)
                .collection("items")
                .whereEqualTo("name", foodName)
                .whereEqualTo("calories", calories)
                .get()
                .await()

            for (document in query.documents) {
                document.reference.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveFoodToCloud(userId: String, food: FoodEntity): Result<Boolean> {
        return try {
            val foodMap = hashMapOf(
                "name" to food.name,
                "calories" to food.calories,
                "protein" to food.protein,
                "carbs" to food.carbs,
                "fat" to food.fat,
                "mealType" to food.mealType,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("users").document(userId)
                .collection("meals")
                .document(food.date)
                .collection("items")
                .document(food.id.toString())
                .set(foodMap)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeWeightHistory(uid: String): Flow<List<Double>> = callbackFlow {
        val query = db.collection("users").document(uid)
            .collection("weight_records")
            .orderBy("date", Query.Direction.ASCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val weights = snapshot.documents.mapNotNull { doc ->
                    doc.getDouble("weight")
                }
                trySend(weights)
            }
        }
        awaitClose { listener.remove() }
    }

    fun observeWeeklyCalories(uid: String): Flow<List<Float>> = callbackFlow {
        val query = db.collection("users").document(uid)
            .collection("daily_summary")
            .orderBy("date", Query.Direction.ASCENDING)
            .limitToLast(7)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val calories = snapshot.documents.mapNotNull { doc ->
                    doc.getDouble("totalCalories")?.toFloat()
                }
                trySend(calories)
            }
        }
        awaitClose { listener.remove() }
    }
}