package android.app.faunadex.domain.repository

import android.app.faunadex.domain.model.User
import android.content.Context
import android.net.Uri

interface UserRepository {
    suspend fun createUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun uploadProfilePicture(uid: String, imageUri: Uri, context: Context): Result<String>
    suspend fun addFavoriteAnimal(uid: String, animalId: String): Result<Unit>
    suspend fun removeFavoriteAnimal(uid: String, animalId: String): Result<Unit>
    suspend fun getFavoriteAnimalIds(uid: String): Result<List<String>>
}

