package android.app.faunadex.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Helper class to add sample animal data to Firestore
 *
 * USAGE:
 * - Call SampleAnimalData.addSampleAnimals() once from your app (e.g., from a debug button)
 * - After data is added, you can remove this file or comment out the function
 *
 * This is for TESTING purposes only!
 */
object SampleAnimalData {

    suspend fun addSampleAnimals(firestore: FirebaseFirestore) {
        val animalsCollection = firestore.collection("animals")

        val sampleAnimals = listOf(
            mapOf(
                "name" to "Sumatran Tiger",
                "scientific_name" to "Panthera tigris sumatrae",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Tropical rainforests",
                "description" to "The Sumatran tiger is a population of Panthera tigris sondaica on the Indonesian island of Sumatra. It is the only surviving tiger population in the Sunda Islands. This population was determined to be a distinct subspecies in 2017. The Sumatran tiger is one of the smallest tiger subspecies. It is characterized by heavy black stripes on its orange coat.",
                "fun_fact" to "Sumatran tigers are the smallest surviving tiger subspecies and have webbed paws that make them excellent swimmers!",
                "diet" to "Carnivore - Wild pigs, deer, fish",
                "lifespan" to "15-20 years",
                "weight" to "100-140 kg",
                "length" to "2.2-2.55 m",
                "special_title" to "King of Sumatran Jungle",
                "endemic_status" to "Endemic to Sumatra",
                "population_trend" to "Decreasing",
                "activity_period" to "Nocturnal",
                "is_protected" to true,
                "protection_type" to "Protected by Law",
                "size_category" to "Large",
                "rarity_level" to "Very Rare",
                "population_past" to 1000,
                "population_present" to 400,
                "latitude" to 0.5897,
                "longitude" to 101.3431,
                "country" to "Indonesia",
                "city" to "Riau Province, Sumatra",
                "domain" to "Eukarya",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "class" to "Mammalia",
                "order" to "Carnivora",
                "family" to "Felidae",
                "genus" to "Panthera",
                "species" to "P. tigris sumatrae",
                "image_url" to null,
                "ar_model_url" to null,
                "tags" to listOf("mammal", "endangered", "endemic")
            ),
            mapOf(
                "name" to "Komodo Dragon",
                "scientific_name" to "Varanus komodoensis",
                "category" to "reptile",
                "conservation_status" to "EN",
                "habitat" to "Dry grasslands and forests",
                "description" to "The Komodo dragon, also known as the Komodo monitor, is a large reptile of the monitor lizard family Varanidae that is endemic to the Indonesian islands of Komodo, Rinca, Flores, and Gili Motang. It is the largest living species of lizard, growing to a maximum length of 3 meters in rare cases and weighing up to approximately 70 kilograms.",
                "fun_fact" to "Komodo dragons have venomous saliva that can kill large prey! They can also run at speeds up to 20 km/h.",
                "diet" to "Carnivore - Deer, pigs, water buffalo",
                "lifespan" to "30 years",
                "weight" to "70-90 kg",
                "length" to "2.5-3 m",
                "special_title" to "Ancient Dragon of Indonesia",
                "endemic_status" to "Endemic to Indonesia",
                "population_trend" to "Stable",
                "activity_period" to "Diurnal",
                "is_protected" to true,
                "protection_type" to "Protected by Law",
                "size_category" to "Large",
                "rarity_level" to "Rare",
                "population_past" to 5000,
                "population_present" to 3000,
                "latitude" to -8.5569,
                "longitude" to 119.4445,
                "country" to "Indonesia",
                "city" to "Komodo National Park, East Nusa Tenggara",
                "domain" to "Eukarya",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "class" to "Reptilia",
                "order" to "Squamata",
                "family" to "Varanidae",
                "genus" to "Varanus",
                "species" to "V. komodoensis",
                "image_url" to null,
                "ar_model_url" to null,
                "tags" to listOf("reptile", "endangered", "endemic")
            ),
            mapOf(
                "name" to "Javan Rhinoceros",
                "scientific_name" to "Rhinoceros sondaicus",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Lowland tropical rainforests",
                "description" to "The Javan rhinoceros, Javan rhino, Sunda rhinoceros or lesser one-horned rhinoceros is a very rare member of the family Rhinocerotidae and one of five extant rhinoceroses. It belongs to the same genus as the Indian rhinoceros, and has similar mosaic, armour-like skin.",
                "fun_fact" to "The Javan rhino is one of the rarest large mammals on Earth, with only about 76 individuals remaining!",
                "diet" to "Herbivore - Shoots, twigs, fallen fruits",
                "lifespan" to "30-45 years",
                "weight" to "900-2300 kg",
                "length" to "2-4 m",
                "special_title" to "Last of Their Kind",
                "endemic_status" to "Endemic to Java",
                "population_trend" to "Stable",
                "activity_period" to "Crepuscular",
                "is_protected" to true,
                "protection_type" to "Protected by Law",
                "size_category" to "Very Large",
                "rarity_level" to "Critically Rare",
                "population_past" to 3000,
                "population_present" to 76,
                "latitude" to -6.7597,
                "longitude" to 105.4245,
                "country" to "Indonesia",
                "city" to "Ujung Kulon National Park, Java",
                "domain" to "Eukarya",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "class" to "Mammalia",
                "order" to "Perissodactyla",
                "family" to "Rhinocerotidae",
                "genus" to "Rhinoceros",
                "species" to "R. sondaicus",
                "image_url" to null,
                "ar_model_url" to null,
                "tags" to listOf("mammal", "endangered", "endemic")
            ),
            mapOf(
                "name" to "Bornean Orangutan",
                "scientific_name" to "Pongo pygmaeus",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Tropical and subtropical moist broadleaf forests",
                "description" to "The Bornean orangutan is a species of orangutan endemic to the island of Borneo. Together with the Sumatran orangutan and Tapanuli orangutan, it belongs to the only genus of great apes native to Asia. It is the largest of the three species and the third-largest primate after gorillas and chimpanzees.",
                "fun_fact" to "Orangutans share 97% of their DNA with humans! They are among the most intelligent primates.",
                "diet" to "Omnivore - Fruits, leaves, insects",
                "lifespan" to "35-45 years",
                "weight" to "50-90 kg",
                "length" to "1.2-1.4 m",
                "special_title" to "Person of the Forest",
                "endemic_status" to "Endemic to Borneo",
                "population_trend" to "Decreasing",
                "activity_period" to "Diurnal",
                "is_protected" to true,
                "protection_type" to "Protected by Law",
                "size_category" to "Large",
                "rarity_level" to "Very Rare",
                "population_past" to 100000,
                "population_present" to 55000,
                "latitude" to 0.0,
                "longitude" to 114.0,
                "country" to "Indonesia/Malaysia",
                "city" to "Borneo Island",
                "domain" to "Eukarya",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "class" to "Mammalia",
                "order" to "Primates",
                "family" to "Hominidae",
                "genus" to "Pongo",
                "species" to "P. pygmaeus",
                "image_url" to null,
                "ar_model_url" to null,
                "tags" to listOf("mammal", "endangered", "endemic")
            ),
            mapOf(
                "name" to "Bali Starling",
                "scientific_name" to "Leucopsar rothschildi",
                "category" to "bird",
                "conservation_status" to "CR",
                "habitat" to "Dry forests and scrublands",
                "description" to "The Bali myna, also known as Rothschild's mynah, Bali starling, or Bali mynah, locally known as jalak Bali, is a medium-sized stocky myna, almost wholly white with a long, drooping crest, and black tips on the wings and tail. The bird has blue bare skin around the eyes, greyish legs and a yellow bill.",
                "fun_fact" to "The Bali Starling is so rare that it only exists naturally in a small region of Bali!",
                "diet" to "Omnivore - Seeds, fruits, insects",
                "lifespan" to "15-20 years",
                "weight" to "0.08-0.1 kg",
                "length" to "25 cm",
                "special_title" to "Symbol of Bali",
                "endemic_status" to "Endemic to Bali",
                "population_trend" to "Increasing",
                "activity_period" to "Diurnal",
                "is_protected" to true,
                "protection_type" to "Protected by Law",
                "size_category" to "Small",
                "rarity_level" to "Critically Rare",
                "population_past" to 1000,
                "population_present" to 150,
                "latitude" to -8.1484,
                "longitude" to 114.3677,
                "country" to "Indonesia",
                "city" to "Bali Barat National Park, Bali",
                "domain" to "Eukarya",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "class" to "Aves",
                "order" to "Passeriformes",
                "family" to "Sturnidae",
                "genus" to "Leucopsar",
                "species" to "L. rothschildi",
                "image_url" to null,
                "ar_model_url" to null,
                "tags" to listOf("bird", "endangered", "endemic")
            )
        )

        try {
            sampleAnimals.forEach { animalData ->
                // Use the animal name as document ID for easier reference
                val docId = (animalData["name"] as String)
                    .lowercase()
                    .replace(" ", "_")

                animalsCollection.document(docId).set(animalData).await()
                println("Added animal: ${animalData["name"]}")
            }
            println("Successfully added ${sampleAnimals.size} sample animals!")
        } catch (e: Exception) {
            println("Error adding sample animals: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Returns the document IDs of the sample animals
     * Use this to navigate to animal details
     */
    fun getSampleAnimalIds(): List<String> {
        return listOf(
            "sumatran_tiger",
            "komodo_dragon",
            "javan_rhinoceros",
            "bornean_orangutan",
            "bali_starling"
        )
    }
}
