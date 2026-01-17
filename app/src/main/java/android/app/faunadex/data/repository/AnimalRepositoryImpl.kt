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
                    longDescription = document.getString("long_description") ?: "",
                    conservationStatus = document.getString("conservation_status") ?: "",
                    imageUrl = document.getString("image_url"),
                    funFact = document.getString("fun_fact") ?: "",
                    diet = document.getString("diet") ?: "",
                    lifespan = document.getString("lifespan") ?: "",
                    weight = document.getString("weight") ?: "",
                    length = document.getString("length") ?: "",
                    specialTitle = document.getString("special_title") ?: "",
                    endemicStatus = document.getString("endemic_status") ?: "",
                    populationTrend = document.getString("population_trend") ?: "",
                    activityPeriod = document.getString("activity_period") ?: "",
                    isProtected = document.getBoolean("is_protected") ?: false,
                    protectionType = document.getString("protection_type") ?: "Protected by Law",
                    sizeCategory = document.getString("size_category") ?: "",
                    rarityLevel = document.getString("rarity_level") ?: "",
                    populationPast = (document.getLong("population_past") ?: 0).toInt(),
                    populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                    latitude = document.getDouble("latitude") ?: 0.0,
                    longitude = document.getDouble("longitude") ?: 0.0,
                    country = document.getString("country") ?: "",
                    city = document.getString("city") ?: "",
                    domain = document.getString("domain") ?: "",
                    kingdom = document.getString("kingdom") ?: "",
                    phylum = document.getString("phylum") ?: "",
                    taxonomyClass = document.getString("class") ?: "",
                    order = document.getString("order") ?: "",
                    family = document.getString("family") ?: "",
                    genus = document.getString("genus") ?: "",
                    species = document.getString("species") ?: "",
                    arModelUrl = document.getString("ar_model_url")
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
                        longDescription = document.getString("long_description") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        funFact = document.getString("fun_fact") ?: "",
                        diet = document.getString("diet") ?: "",
                        lifespan = document.getString("lifespan") ?: "",
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: "",
                        specialTitle = document.getString("special_title") ?: "",
                        endemicStatus = document.getString("endemic_status") ?: "",
                        populationTrend = document.getString("population_trend") ?: "",
                        activityPeriod = document.getString("activity_period") ?: "",
                        isProtected = document.getBoolean("is_protected") ?: false,
                        protectionType = document.getString("protection_type") ?: "Protected by Law",
                        sizeCategory = document.getString("size_category") ?: "",
                        rarityLevel = document.getString("rarity_level") ?: "",
                        populationPast = (document.getLong("population_past") ?: 0).toInt(),
                        populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        country = document.getString("country") ?: "",
                        city = document.getString("city") ?: "",
                        domain = document.getString("domain") ?: "",
                        kingdom = document.getString("kingdom") ?: "",
                        phylum = document.getString("phylum") ?: "",
                        taxonomyClass = document.getString("class") ?: "",
                        order = document.getString("order") ?: "",
                        family = document.getString("family") ?: "",
                        genus = document.getString("genus") ?: "",
                        species = document.getString("species") ?: "",
                        arModelUrl = document.getString("ar_model_url")
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
                        longDescription = document.getString("long_description") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        funFact = document.getString("fun_fact") ?: "",
                        diet = document.getString("diet") ?: "",
                        lifespan = document.getString("lifespan") ?: "",
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: "",
                        specialTitle = document.getString("special_title") ?: "",
                        endemicStatus = document.getString("endemic_status") ?: "",
                        populationTrend = document.getString("population_trend") ?: "",
                        activityPeriod = document.getString("activity_period") ?: "",
                        isProtected = document.getBoolean("is_protected") ?: false,
                        protectionType = document.getString("protection_type") ?: "Protected by Law",
                        sizeCategory = document.getString("size_category") ?: "",
                        rarityLevel = document.getString("rarity_level") ?: "",
                        populationPast = (document.getLong("population_past") ?: 0).toInt(),
                        populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        country = document.getString("country") ?: "",
                        city = document.getString("city") ?: "",
                        domain = document.getString("domain") ?: "",
                        kingdom = document.getString("kingdom") ?: "",
                        phylum = document.getString("phylum") ?: "",
                        taxonomyClass = document.getString("class") ?: "",
                        order = document.getString("order") ?: "",
                        family = document.getString("family") ?: "",
                        genus = document.getString("genus") ?: "",
                        species = document.getString("species") ?: "",
                        arModelUrl = document.getString("ar_model_url")
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

