
package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.model.UserData
import kotlinx.coroutines.tasks.await

class FirestoreSource {
    private val db = FirebaseFirestore.getInstance()

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
}