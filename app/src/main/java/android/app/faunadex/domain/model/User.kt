package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",

    @PropertyName("profile_picture_url")
    val profilePictureUrl: String? = null,

    @PropertyName("education_level")
    val educationLevel: String = "",

    @PropertyName("current_title")
    val currentTitle: String = "Petualang Pemula",

    @PropertyName("total_xp")
    val totalXp: Int = 0,

    @PropertyName("favorite_animal_ids")
    val favoriteAnimalIds: List<String> = emptyList(),

    @ServerTimestamp
    @PropertyName("joined_at")
    val joinedAt: Date? = null
)

