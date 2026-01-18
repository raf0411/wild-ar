package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.UserRepository
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun createUserProfile(user: User): Result<Unit> {
        return try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "username" to user.username,
                "education_level" to user.educationLevel,
                "current_title" to user.currentTitle,
                "total_xp" to user.totalXp,
                "joined_at" to FieldValue.serverTimestamp()
            )
            usersCollection.document(user.uid).set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val document = usersCollection.document(uid).get().await()

            Log.d("UserRepositoryImpl", "Document exists: ${document.exists()}")
            Log.d("UserRepositoryImpl", "Document data: ${document.data}")

            if (document.exists()) {
                // Explicitly map snake_case fields to User object
                val educationLevel = document.getString("education_level") ?: ""
                val username = document.getString("username") ?: ""
                val profilePictureUrl = document.getString("profile_picture_url")
                val currentTitle = document.getString("current_title") ?: "Petualang Pemula"
                val totalXp = document.getLong("total_xp")?.toInt() ?: 0
                val favoriteAnimalIds = document.get("favorite_animal_ids") as? List<String> ?: emptyList()

                Log.d("UserRepositoryImpl", "Read from Firestore - education_level: '$educationLevel', username: '$username'")

                val user = User(
                    uid = document.getString("uid") ?: "",
                    email = document.getString("email") ?: "",
                    username = username,
                    profilePictureUrl = profilePictureUrl,
                    educationLevel = educationLevel,
                    currentTitle = currentTitle,
                    totalXp = totalXp,
                    favoriteAnimalIds = favoriteAnimalIds,
                    joinedAt = document.getTimestamp("joined_at")?.toDate()
                )

                Log.d("UserRepositoryImpl", "User object created - educationLevel: '${user.educationLevel}'")
                Result.success(user)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error getting user profile", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            // Explicitly map to snake_case field names
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "username" to user.username,
                "profile_picture_url" to (user.profilePictureUrl ?: ""),
                "education_level" to user.educationLevel,
                "current_title" to user.currentTitle,
                "total_xp" to user.totalXp
            )
            // Don't update joined_at on updates
            if (user.joinedAt != null) {
                userMap["joined_at"] = user.joinedAt
            }
            usersCollection.document(user.uid).set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePicture(uid: String, imageUri: Uri, context: Context): Result<String> {
        return try {
            // Read image from URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Resize image to reduce size (max 512x512)
            val resizedBitmap = resizeBitmap(bitmap, 512, 512)

            // Convert to Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)

            // Update user document with Base64 image
            usersCollection.document(uid).update("profile_picture_url", base64Image).await()

            Log.d("UserRepositoryImpl", "Profile picture uploaded successfully as Base64")
            Result.success(base64Image)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error uploading profile picture", e)
            Result.failure(e)
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }

    override suspend fun addFavoriteAnimal(uid: String, animalId: String): Result<Unit> {
        return try {
            usersCollection.document(uid)
                .update("favorite_animal_ids", FieldValue.arrayUnion(animalId))
                .await()
            Log.d("UserRepositoryImpl", "Added animal $animalId to favorites for user $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error adding favorite animal", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFavoriteAnimal(uid: String, animalId: String): Result<Unit> {
        return try {
            usersCollection.document(uid)
                .update("favorite_animal_ids", FieldValue.arrayRemove(animalId))
                .await()
            Log.d("UserRepositoryImpl", "Removed animal $animalId from favorites for user $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error removing favorite animal", e)
            Result.failure(e)
        }
    }

    override suspend fun getFavoriteAnimalIds(uid: String): Result<List<String>> {
        return try {
            val document = usersCollection.document(uid).get().await()
            val favoriteIds = document.get("favorite_animal_ids") as? List<String> ?: emptyList()
            Log.d("UserRepositoryImpl", "Retrieved ${favoriteIds.size} favorite animals for user $uid")
            Result.success(favoriteIds)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error getting favorite animals", e)
            Result.failure(e)
        }
    }
}

