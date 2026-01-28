package android.app.faunadex.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseDataSeeder {

    /**
     * Parse population string to integer
     * Examples: "~1,000 (1970s estimate)" -> 1000, "<400" -> 400, "~2,400" -> 2400
     */
    private fun parsePopulation(populationStr: String): Int {
        return try {
            val cleanedStr = populationStr
                .replace("~", "")
                .replace("<", "")
                .replace(">", "")
                .replace(",", "")
                .replace(".", "")
                .split("(")[0]
                .trim()

            cleanedStr.toIntOrNull() ?: 0
        } catch (e: Exception) {
            Log.e("FirebaseDataSeeder", "Error parsing population: $populationStr", e)
            0
        }
    }

    suspend fun seedAnimals(firestore: FirebaseFirestore) {
        val animalsCollection = firestore.collection("animals")

        val sampleAnimals = listOf(
            // 1. Dugong
            mapOf(
                "name_en" to "Dugong",
                "name_id" to "Dugong (Duyung)",
                "category_en" to "Mammal",
                "category_id" to "Mamalia",
                "description_en" to "A gentle marine mammal often called the \"sea cow\" that grazes on underwater seagrass beds.",
                "description_id" to "Mamalia laut yang lembut yang sering disebut \"sapi laut\" yang merumput di padang lamun bawah air.",
                "long_description_en" to "Dugongs are the only strictly herbivorous marine mammals. They are related to elephants and can stay underwater for up to 6 minutes before surfacing to breathe. In Indonesia, they are protected and often found in seagrass-rich waters like Bintan and Alor.",
                "long_description_id" to "Dugong adalah satu-satunya mamalia laut yang sepenuhnya herbivora. Mereka berkerabat dengan gajah dan dapat bertahan di dalam air hingga 6 menit sebelum muncul ke permukaan untuk bernapas. Di Indonesia, mereka dilindungi dan sering ditemukan di perairan kaya lamun seperti Bintan dan Alor.",
                "fun_fact_en" to "They are believed to be the inspiration for ancient \"mermaid\" myths observed by sailors.",
                "fun_fact_id" to "Mereka diyakini sebagai inspirasi bagi mitos \"putri duyung\" kuno yang diamati oleh para pelaut.",
                "audio_description_url_en" to "",
                "audio_description_url_id" to "",
                "audio_fun_fact_url_en" to "",
                "audio_fun_fact_url_id" to "",
                "activity_period_en" to "Diurnal / Nocturnal (Tidal dependent)",
                "activity_period_id" to "Diurnal / Nokturnal (Tergantung pasang surut)",
                "diet_en" to "Herbivore (Seagrass)",
                "diet_id" to "Herbivora (Lamun)",
                "habitat_en" to "Coastal waters, seagrass meadows",
                "habitat_id" to "Perairan pesisir, padang lamun",
                "lifespan_en" to "70 years",
                "lifespan_id" to "70 tahun",
                "city_en" to "Bintan / Alor (Specific sightings)",
                "city_id" to "Bintan / Alor (Lokasi penampakan)",
                "country_en" to "Indonesia",
                "country_id" to "Indonesia",
                "endemic_status_en" to "Native to Indo-Pacific",
                "endemic_status_id" to "Asli Indo-Pasifik",
                "population_trend_en" to "Decreasing",
                "population_trend_id" to "Menurun",
                "protection_type_en" to "Protected by Law No. 5 of 1990",
                "protection_type_id" to "Dilindungi UU No. 5 Tahun 1990",
                "rarity_level_en" to "Vulnerable",
                "rarity_level_id" to "Rentan",
                "size_category_en" to "Large",
                "size_category_id" to "Besar",
                "special_title_en" to "The Sea Cow",
                "special_title_id" to "Sapi Laut",
                "scientific_name" to "Dugong dugon",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "domain" to "Eukaryota",
                "class" to "Mammalia",
                "order" to "Sirenia",
                "family" to "Dugongidae",
                "genus" to "Dugong",
                "species" to "D. dugon",
                "conservation_status" to "VU",
                "ar_model_url" to null,
                "image_url" to null,
                "is_protected" to true,
                "latitude" to -1.1500,
                "longitude" to 104.5300,
                "length" to "240-300 cm",
                "weight" to "230-400 kg",
                "population_past" to parsePopulation("Unknown"),
                "population_present" to parsePopulation("~30,000")
            ) to "dugong",

            // 2. Hawksbill Turtle
            mapOf(
                "name_en" to "Hawksbill Turtle",
                "name_id" to "Penyu Sisik",
                "category_en" to "Reptile",
                "category_id" to "Reptil",
                "description_en" to "A critically endangered sea turtle named for its narrow, pointed beak.",
                "description_id" to "Penyu laut yang sangat terancam punah, dinamai berdasarkan paruhnya yang sempit dan runcing.",
                "long_description_en" to "The Hawksbill is famous for its beautiful shell (tortoiseshell), which sadly made it a target for poaching. They play a vital role in coral reef health by eating sponges that would otherwise outcompete corals.",
                "long_description_id" to "Penyu Sisik terkenal dengan cangkangnya yang indah, yang sayangnya menjadikannya target perburuan liar. Mereka memainkan peran vital dalam kesehatan terumbu karang dengan memakan spons yang jika tidak dimakan akan mengalahkan karang.",
                "fun_fact_en" to "Their diet of sponges includes toxic silica (glass) needles, which they can digest without harm.",
                "fun_fact_id" to "Makanan mereka yang berupa spons mengandung jarum silika (kaca) beracun, yang dapat mereka cerna tanpa bahaya.",
                "audio_description_url_en" to "",
                "audio_description_url_id" to "",
                "audio_fun_fact_url_en" to "",
                "audio_fun_fact_url_id" to "",
                "activity_period_en" to "Diurnal / Nocturnal",
                "activity_period_id" to "Diurnal / Nokturnal",
                "diet_en" to "Omnivore (Sponges dominant)",
                "diet_id" to "Omnivora (Dominan spons)",
                "habitat_en" to "Coral reefs, lagoons",
                "habitat_id" to "Terumbu karang, laguna",
                "lifespan_en" to "30-50 years",
                "lifespan_id" to "30-50 tahun",
                "city_en" to "N/A (Found in Thousand Islands, Bunaken, Raja Ampat)",
                "city_id" to "Tidak ada (Ditemukan di Kepulauan Seribu, Bunaken, Raja Ampat)",
                "country_en" to "Indonesia",
                "country_id" to "Indonesia",
                "endemic_status_en" to "Native to Tropical Oceans",
                "endemic_status_id" to "Asli Samudra Tropis",
                "population_trend_en" to "Decreasing",
                "population_trend_id" to "Menurun",
                "protection_type_en" to "Protected by Law No. 5 of 1990",
                "protection_type_id" to "Dilindungi UU No. 5 Tahun 1990",
                "rarity_level_en" to "Critically Endangered",
                "rarity_level_id" to "Kritis",
                "size_category_en" to "Medium / Large",
                "size_category_id" to "Sedang / Besar",
                "special_title_en" to "Guardian of the Reef",
                "special_title_id" to "Penjaga Terumbu Karang",
                "scientific_name" to "Eretmochelys imbricata",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "domain" to "Eukaryota",
                "class" to "Reptilia",
                "order" to "Testudines",
                "family" to "Cheloniidae",
                "genus" to "Eretmochelys",
                "species" to "E. imbricata",
                "conservation_status" to "CR",
                "ar_model_url" to null,
                "image_url" to null,
                "is_protected" to true,
                "latitude" to -5.6000,
                "longitude" to 106.5500,
                "length" to "60-90 cm",
                "weight" to "45-70 kg",
                "population_past" to parsePopulation("Abundant"),
                "population_present" to parsePopulation("~20,000")
            ) to "hawksbill_turtle",

            // 3. Napoleon Wrasse
            mapOf(
                "name_en" to "Napoleon Wrasse (Humphead Wrasse)",
                "name_id" to "Ikan Napoleon (Ikan Mameng)",
                "category_en" to "Fish",
                "category_id" to "Ikan",
                "description_en" to "A gigantic, colorful reef fish characterized by a prominent hump on its forehead and thick lips.",
                "description_id" to "Ikan karang raksasa berwarna-warni yang ditandai dengan benjolan menonjol di dahinya dan bibir yang tebal.",
                "long_description_en" to "This is the largest member of the wrasse family. They are slow-growing and can live for decades. Their intricate blue-green patterns are like fingerprints, unique to each individual. They are one of the few predators of the toxic Crown-of-Thorns starfish.",
                "long_description_id" to "Ini adalah anggota terbesar dari keluarga wrasse. Mereka tumbuh lambat dan bisa hidup selama beberapa dekade. Pola biru-hijau mereka yang rumit seperti sidik jari, unik untuk setiap individu. Mereka adalah satu dari sedikit predator bintang laut Mahkota Duri yang beracun.",
                "fun_fact_en" to "They are protogynous hermaphrodites; some are born female and can change sex to male around age 9.",
                "fun_fact_id" to "Mereka adalah hermafrodit protogini; beberapa lahir sebagai betina dan dapat berubah jenis kelamin menjadi jantan sekitar usia 9 tahun.",
                "audio_description_url_en" to "",
                "audio_description_url_id" to "",
                "audio_fun_fact_url_en" to "",
                "audio_fun_fact_url_id" to "",
                "activity_period_en" to "Diurnal",
                "activity_period_id" to "Diurnal",
                "diet_en" to "Carnivore (Mollusks, crustaceans, starfish)",
                "diet_id" to "Karnivora (Moluska, krustasea, bintang laut)",
                "habitat_en" to "Coral reef slopes",
                "habitat_id" to "Lereng terumbu karang",
                "lifespan_en" to "30-50 years",
                "lifespan_id" to "30-50 tahun",
                "city_en" to "Wakatobi / Raja Ampat",
                "city_id" to "Wakatobi / Raja Ampat",
                "country_en" to "Indonesia",
                "country_id" to "Indonesia",
                "endemic_status_en" to "Indo-Pacific Native",
                "endemic_status_id" to "Asli Indo-Pasifik",
                "population_trend_en" to "Decreasing",
                "population_trend_id" to "Menurun",
                "protection_type_en" to "Protected by Minister of Marine Affairs Decree",
                "protection_type_id" to "Dilindungi Keputusan Menteri Kelautan",
                "rarity_level_en" to "Endangered",
                "rarity_level_id" to "Genting",
                "size_category_en" to "Large",
                "size_category_id" to "Besar",
                "special_title_en" to "King of the Reef",
                "special_title_id" to "Raja Terumbu Karang",
                "scientific_name" to "Cheilinus undulatus",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "domain" to "Eukaryota",
                "class" to "Actinopterygii",
                "order" to "Labriformes",
                "family" to "Labridae",
                "genus" to "Cheilinus",
                "species" to "C. undulatus",
                "conservation_status" to "EN",
                "ar_model_url" to null,
                "image_url" to null,
                "is_protected" to true,
                "latitude" to -5.3500,
                "longitude" to 123.5800,
                "length" to "150-230 cm",
                "weight" to "180-190 kg",
                "population_past" to parsePopulation("Common"),
                "population_present" to parsePopulation("Endangered")
            ) to "napoleon_wrasse",

            // 4. Oceanic Manta Ray
            mapOf(
                "name_en" to "Oceanic Manta Ray",
                "name_id" to "Pari Manta Oseanik",
                "category_en" to "Fish (Ray)",
                "category_id" to "Ikan (Pari)",
                "description_en" to "The largest species of ray in the world, known for its distinct black and white markings and \"horns\".",
                "description_id" to "Spesies pari terbesar di dunia, dikenal dengan tanda hitam putih yang khas dan \"tanduknya\".",
                "long_description_en" to "Unlike the Reef Manta, the Oceanic Manta is pelagic, traveling vast distances across open oceans. They are filter feeders, swimming with their mouths open to catch plankton. They are highly intelligent and curious towards divers.",
                "long_description_id" to "Berbeda dengan Manta Karang, Manta Oseanik bersifat pelagis, menempuh jarak yang sangat jauh melintasi lautan lepas. Mereka adalah penyaring makanan, berenang dengan mulut terbuka untuk menangkap plankton. Mereka sangat cerdas dan ingin tahu terhadap penyelam.",
                "fun_fact_en" to "They have the largest brain-to-body mass ratio of any cold-blooded fish.",
                "fun_fact_id" to "Mereka memiliki rasio massa otak-ke-tubuh terbesar dari semua ikan berdarah dingin.",
                "audio_description_url_en" to "",
                "audio_description_url_id" to "",
                "audio_fun_fact_url_en" to "",
                "audio_fun_fact_url_id" to "",
                "activity_period_en" to "Diurnal / Nocturnal",
                "activity_period_id" to "Diurnal / Nokturnal",
                "diet_en" to "Filter Feeder (Zooplankton)",
                "diet_id" to "Penyaring Makanan (Zooplankton)",
                "habitat_en" to "Open ocean, seamounts, cleaning stations",
                "habitat_id" to "Laut lepas, gunung laut, stasiun pembersihan",
                "lifespan_en" to "40-50 years",
                "lifespan_id" to "40-50 tahun",
                "city_en" to "N/A (Migratory - Seen in Komodo, Raja Ampat, Nusa Penida)",
                "city_id" to "Tidak ada (Migrasi - Terlihat di Komodo, Raja Ampat, Nusa Penida)",
                "country_en" to "Indonesia",
                "country_id" to "Indonesia",
                "endemic_status_en" to "Circumglobal Tropical",
                "endemic_status_id" to "Tropis Sirkumglobal",
                "population_trend_en" to "Decreasing",
                "population_trend_id" to "Menurun",
                "protection_type_en" to "Protected by Law No. 5 of 1990",
                "protection_type_id" to "Dilindungi UU No. 5 Tahun 1990",
                "rarity_level_en" to "Endangered",
                "rarity_level_id" to "Genting",
                "size_category_en" to "Very Large",
                "size_category_id" to "Sangat Besar",
                "special_title_en" to "Gentle Giant",
                "special_title_id" to "Raksasa Lembut",
                "scientific_name" to "Mobula birostris",
                "kingdom" to "Animalia",
                "phylum" to "Chordata",
                "domain" to "Eukaryota",
                "class" to "Chondrichthyes",
                "order" to "Myliobatiformes",
                "family" to "Mobulidae",
                "genus" to "Mobula",
                "species" to "M. birostris",
                "conservation_status" to "EN",
                "ar_model_url" to null,
                "image_url" to null,
                "is_protected" to true,
                "latitude" to -8.5000,
                "longitude" to 119.5000,
                "length" to "450-700 cm (Wingspan)",
                "weight" to "1350-2000 kg",
                "population_past" to parsePopulation("Unknown"),
                "population_present" to parsePopulation("Declining globally")
            ) to "oceanic_manta_ray"
        )

        try {
            var successCount = 0
            sampleAnimals.forEach { (animalData, docId) ->
                animalsCollection.document(docId).set(animalData).await()
                Log.d("FirebaseDataSeeder", "✓ Added: ${animalData["name_en"]}")
                successCount++
            }
            Log.d("FirebaseDataSeeder", "🎉 Successfully added $successCount animals to Firebase!")
        } catch (e: Exception) {
            Log.e("FirebaseDataSeeder", "❌ Error adding animals: ${e.message}", e)
            throw e
        }
    }
}