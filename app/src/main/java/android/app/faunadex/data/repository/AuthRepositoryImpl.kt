package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: ""
                )
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to create user")
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: ""
                )
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to sign in")
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(
                uid = it.uid,
                email = it.email ?: ""
            )
        }
    }

    override fun isUserLoggedIn(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }
}

