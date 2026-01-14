package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.repository.AnimalRepository
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AnimalRepository {

    private val animalsCollection = firestore.collection("animals")

    override suspend fun getAnimalById(animalId: String): Result<Animal> {
        return try {
            val document = animalsCollection.document(animalId).get().await()

            Log.d("AnimalRepositoryImpl", "Document exists: ${document.exists()}")
            Log.d("AnimalRepositoryImpl", "Document data: ${document.data}")

            if (document.exists()) {
                val animal = Animal(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    scientificName = document.getString("scientific_name") ?: "",
                    category = document.getString("category") ?: "",
                    habitat = document.getString("habitat") ?: "",
                    description = document.getString("description") ?: "",
                    conservationStatus = document.getString("conservation_status") ?: "",
                    imageUrl = document.getString("image_url"),
                    funFact = document.getString("fun_fact") ?: "",
                    diet = document.getString("diet") ?: "",
                    lifespan = document.getString("lifespan") ?: "",
                    weight = document.getString("weight") ?: "",
                    length = document.getString("length") ?: ""
                )

                Log.d("AnimalRepositoryImpl", "Animal object created - name: '${animal.name}'")
                Result.success(animal)
            } else {
                Result.failure(Exception("Animal not found"))
            }
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting animal", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllAnimals(): Result<List<Animal>> {
        return try {
            val snapshot = animalsCollection.get().await()
            val animals = snapshot.documents.mapNotNull { document ->
                try {
                    Animal(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        scientificName = document.getString("scientific_name") ?: "",
                        category = document.getString("category") ?: "",
                        habitat = document.getString("habitat") ?: "",
                        description = document.getString("description") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        funFact = document.getString("fun_fact") ?: "",
                        diet = document.getString("diet") ?: "",
                        lifespan = document.getString("lifespan") ?: "",
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("AnimalRepositoryImpl", "Error parsing animal document", e)
                    null
                }
            }
            Result.success(animals)
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting all animals", e)
            Result.failure(e)
        }
    }

    override suspend fun getAnimalsByCategory(category: String): Result<List<Animal>> {
        return try {
            val snapshot = animalsCollection
                .whereEqualTo("category", category)
                .get()
                .await()

            val animals = snapshot.documents.mapNotNull { document ->
                try {
                    Animal(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        scientificName = document.getString("scientific_name") ?: "",
                        category = document.getString("category") ?: "",
                        habitat = document.getString("habitat") ?: "",
                        description = document.getString("description") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        funFact = document.getString("fun_fact") ?: "",
                        diet = document.getString("diet") ?: "",
                        lifespan = document.getString("lifespan") ?: "",
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("AnimalRepositoryImpl", "Error parsing animal document", e)
                    null
                }
            }
            Result.success(animals)
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting animals by category", e)
            Result.failure(e)
        }
    }
}

