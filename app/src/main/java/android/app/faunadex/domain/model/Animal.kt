package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName

data class Animal(
    val id: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("scientific_name")
    val scientificName: String = "",

    @PropertyName("category")
    val category: String = "",

    @PropertyName("habitat")
    val habitat: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("conservation_status")
    val conservationStatus: String = "",

    @PropertyName("image_url")
    val imageUrl: String? = null,

    @PropertyName("fun_fact")
    val funFact: String = "",

    @PropertyName("diet")
    val diet: String = "",

    @PropertyName("lifespan")
    val lifespan: String = "",

    @PropertyName("weight")
    val weight: String = "",

    @PropertyName("length")
    val length: String = ""
)

