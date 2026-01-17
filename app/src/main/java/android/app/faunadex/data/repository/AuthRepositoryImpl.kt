package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import android.app.faunadex.domain.repository.UserRepository
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        username: String,
        educationLevel: String
    ): AuthResult<User> {
        return try {
            // Step 1: Create user in Firebase Authentication
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Step 2: Create User object with full profile data
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    username = username,
                    educationLevel = educationLevel,
                    currentTitle = "Petualang Pemula",
                    totalXp = 0
                )

                // Step 3: Save profile to Firestore (PENTING!)
                val firestoreResult = userRepository.createUserProfile(user)

                if (firestoreResult.isSuccess) {
                    AuthResult.Success(user)
                } else {
                    // Jika gagal simpan ke Firestore, hapus user dari Auth (rollback)
                    firebaseUser.delete().await()
                    AuthResult.Error("Failed to create user profile: ${firestoreResult.exceptionOrNull()?.message}")
                }
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
                // Fetch user profile from Firestore
                val profileResult = userRepository.getUserProfile(firebaseUser.uid)

                if (profileResult.isSuccess) {
                    AuthResult.Success(profileResult.getOrThrow())
                } else {
                    // Fallback: return basic user info if profile not found
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: ""
                    )
                    AuthResult.Success(user)
                }
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

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null || user.email == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // Re-authenticate user with current password
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()

            // Update password
            user.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_WRONG_PASSWORD" -> Result.failure(Exception("Current password is incorrect"))
                "ERROR_WEAK_PASSWORD" -> Result.failure(Exception("New password is too weak"))
                else -> Result.failure(Exception(e.message ?: "Failed to change password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to change password"))
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            try {
                val profileResult = runBlocking {
                    userRepository.getUserProfile(it.uid)
                }

                if (profileResult.isSuccess) {
                    val user = profileResult.getOrNull()
                    Log.d("AuthRepositoryImpl", "Fetched user profile - education: '${user?.educationLevel}'")
                    user
                } else {
                    Log.w("AuthRepositoryImpl", "Profile not found, returning basic user")
                    // Fallback to basic user info
                    User(
                        uid = it.uid,
                        email = it.email ?: "",
                        educationLevel = "SMA" // Default fallback
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Error fetching user profile", e)
                // Fallback to basic user info
                User(
                    uid = it.uid,
                    email = it.email ?: "",
                    educationLevel = "SMA" // Default fallback
                )
            }
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

