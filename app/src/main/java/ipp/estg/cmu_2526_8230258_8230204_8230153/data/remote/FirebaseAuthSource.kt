package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseAuthSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Retorna um fluxo (Flow) que emite atualizações sempre que o estado de autenticação muda.
     * Isto permite que a UI reaja automaticamente a logins e logouts.
     *
     * @return Flow<FirebaseUser?> emite o utilizador atual ou null se não estiver logado.
     */
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun updatePassword(newPassword: String): Result<Boolean> {
        return try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}