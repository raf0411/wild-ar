package android.app.faunadex.domain.repository

import android.app.faunadex.domain.model.Animal

interface AnimalRepository {
    suspend fun getAnimalById(animalId: String): Result<Animal>
    suspend fun getAllAnimals(): Result<List<Animal>>
    suspend fun getAnimalsByCategory(category: String): Result<List<Animal>>
}

