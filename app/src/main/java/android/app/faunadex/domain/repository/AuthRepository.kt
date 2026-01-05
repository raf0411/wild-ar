package android.app.faunadex.domain.repository

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String): AuthResult<User>
    suspend fun signIn(email: String, password: String): AuthResult<User>
    suspend fun signOut()
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Flow<Boolean>
}

