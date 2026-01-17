package android.app.faunadex.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utility to seed initial animal data into Firebase Firestore
 *
 * USAGE: Call FirebaseDataSeeder.seedAnimals(firestore) once from your app
 * After data is seeded, you can comment out or remove the call
 */
object FirebaseDataSeeder {

    suspend fun seedAnimals(firestore: FirebaseFirestore) {
        val animalsCollection = firestore.collection("animals")

        val sampleAnimals = listOf(
            // Sumatran Tiger
            hashMapOf(
                "name" to "Sumatran Tiger",
                "scientific_name" to "Panthera tigris sumatrae",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Tropical rainforests",
                "description" to "The Sumatran tiger is the smallest surviving tiger subspecies, endemic to the Indonesian island of Sumatra.",
                "long_description" to "The Sumatran tiger is a population of Panthera tigris sondaica on the Indonesian island of Sumatra. It is the only surviving tiger population in the Sunda Islands, and was determined to be a distinct subspecies in 2017. The Sumatran tiger is one of the smallest tiger subspecies, characterized by heavy black stripes on its orange coat. Males typically weigh between 100-140 kg, while females weigh 75-110 kg. They inhabit tropical rainforests and prefer areas with dense vegetation and access to water sources.\n\nThis magnificent predator faces critical threats from habitat loss due to deforestation, poaching for the illegal wildlife trade, and human-wildlife conflict. Conservation efforts include protected areas like national parks, anti-poaching patrols, and community-based conservation programs. The Sumatran tiger plays a crucial role in maintaining the ecological balance of its rainforest habitat.",
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
                "image_url" to "https://cdn.pixabay.com/photo/2013/07/18/20/24/tiger-164905_1280.jpg",
                "ar_model_url" to null
            ) to "sumatran_tiger",

            // Komodo Dragon
            hashMapOf(
                "name" to "Komodo Dragon",
                "scientific_name" to "Varanus komodoensis",
                "category" to "reptile",
                "conservation_status" to "EN",
                "habitat" to "Dry grasslands and forests",
                "description" to "The Komodo dragon is the largest living species of lizard, growing up to 3 meters and weighing up to 70 kilograms.",
                "long_description" to "The Komodo dragon, also known as the Komodo monitor, is a large reptile of the monitor lizard family Varanidae that is endemic to the Indonesian islands of Komodo, Rinca, Flores, and Gili Motang. It is the largest living species of lizard, growing to a maximum length of 3 meters in rare cases and weighing up to approximately 70 kilograms. These impressive predators are apex carnivores in their habitat.\n\nKomodo dragons have a unique hunting strategy that combines venomous saliva and powerful jaws. They are capable of taking down large prey such as deer, pigs, and water buffalo. Their excellent sense of smell allows them to detect carrion from up to 9 kilometers away. Conservation efforts focus on protecting their island habitats within Komodo National Park, which was established in 1980 to protect these remarkable creatures.",
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
                "image_url" to "https://cdn.pixabay.com/photo/2023/08/23/23/20/komodo-dragon-8209429_1280.jpg",
                "ar_model_url" to null
            ) to "komodo_dragon",

            // Bornean Orangutan
            hashMapOf(
                "name" to "Bornean Orangutan",
                "scientific_name" to "Pongo pygmaeus",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Tropical and subtropical moist broadleaf forests",
                "description" to "The Bornean orangutan is a great ape species endemic to Borneo, known for its intelligence and distinctive reddish-brown fur.",
                "long_description" to "The Bornean orangutan is a species of orangutan endemic to the island of Borneo. Together with the Sumatran orangutan and Tapanuli orangutan, it belongs to the only genus of great apes native to Asia. It is the largest of the three species and the third-largest primate after gorillas and chimpanzees. These highly intelligent primates share 97% of their DNA with humans and demonstrate remarkable problem-solving abilities.\n\nBornean orangutans are primarily arboreal, spending most of their time in trees where they build nests for sleeping. They have a varied diet consisting mainly of fruits, leaves, bark, and insects. Adult males develop distinctive cheek pads called flanges. The species faces severe threats from habitat loss due to palm oil plantations, logging, and illegal pet trade. Conservation efforts include rehabilitation centers, protected forest areas, and community education programs.",
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
                "image_url" to "https://cdn.pixabay.com/photo/2018/07/05/17/13/bornean-3518768_1280.jpg",
                "ar_model_url" to null
            ) to "bornean_orangutan",

            // Javan Rhinoceros
            hashMapOf(
                "name" to "Javan Rhinoceros",
                "scientific_name" to "Rhinoceros sondaicus",
                "category" to "mammal",
                "conservation_status" to "CR",
                "habitat" to "Lowland tropical rainforests",
                "description" to "The Javan rhino is one of the rarest large mammals on Earth, with only about 76 individuals remaining in the wild.",
                "long_description" to "The Javan rhinoceros, also known as the Javan rhino, Sunda rhinoceros, or lesser one-horned rhinoceros, is a very rare member of the family Rhinocerotidae and one of five extant rhinoceroses. It belongs to the same genus as the Indian rhinoceros and has similar mosaic, armour-like skin. The Javan rhino is smaller than the Indian rhino and is the rarest of the five rhino species, with only about 76 individuals surviving in Ujung Kulon National Park on the western tip of Java, Indonesia.\n\nHistorically, the Javan rhino ranged throughout Southeast Asia, but habitat loss, poaching, and competition from invasive species have reduced its population to a single location. These solitary animals are browsers, feeding on shoots, twigs, young foliage, and fallen fruits. Conservation efforts focus on intensive protection of Ujung Kulon National Park, habitat management, and potential establishment of a second population. The species' survival depends on maintaining and expanding suitable habitat while preventing poaching and disease outbreaks.",
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
                "image_url" to "https://img.freepik.com/free-photo/closeup-shot-rhino-grazing-grass-front-it_181624-9097.jpg?t=st=1768616620~exp=1768620220~hmac=4b3117a4f4a8bb1c7341d420fe68b862ef0aea4f290fb320d9b940465c71b531",
                "ar_model_url" to null
            ) to "javan_rhinoceros",

            // Bali Starling
            hashMapOf(
                "name" to "Bali Starling",
                "scientific_name" to "Leucopsar rothschildi",
                "category" to "bird",
                "conservation_status" to "CR",
                "habitat" to "Dry forests and scrublands",
                "description" to "The Bali Starling is a beautiful white bird endemic to Bali, known for its striking blue eye patches and elegant crest.",
                "long_description" to "The Bali myna, also known as Rothschild's mynah, Bali starling, or Bali mynah (locally known as jalak Bali), is a medium-sized stocky myna, almost wholly white with a long, drooping crest, and black tips on the wings and tail. The bird has distinctive blue bare skin around the eyes, greyish legs, and a yellow bill. This elegant bird is endemic to the island of Bali in Indonesia and is considered a symbol of the island's natural heritage.\n\nOnce widespread across Bali, the Bali Starling's population declined dramatically due to illegal trapping for the caged bird trade and habitat loss. By the 1990s, fewer than 15 birds remained in the wild. Intensive conservation efforts, including captive breeding programs, anti-poaching measures, and habitat restoration, have helped increase the population. However, the species remains critically endangered. Bali Barat National Park serves as the primary habitat, where the birds feed on seeds, fruits, and insects. Conservation success depends on continued protection and addressing the illegal bird trade.",
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
                "image_url" to "https://cdn.pixabay.com/photo/2019/10/30/12/47/bali-myna-4589476_1280.jpg",
                "ar_model_url" to null
            ) to "bali_starling"
        )

        try {
            var successCount = 0
            sampleAnimals.forEach { (animalData, docId) ->
                animalsCollection.document(docId).set(animalData).await()
                Log.d("FirebaseDataSeeder", "✓ Added: ${animalData["name"]}")
                successCount++
            }
            Log.d("FirebaseDataSeeder", "🎉 Successfully added $successCount animals to Firebase!")
        } catch (e: Exception) {
            Log.e("FirebaseDataSeeder", "❌ Error adding animals: ${e.message}", e)
            throw e
        }
    }
}
