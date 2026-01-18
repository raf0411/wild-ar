package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.repository.UserRepository
import javax.inject.Inject

class ToggleFavoriteAnimalUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, animalId: String, isFavorite: Boolean): Result<Unit> {
        return if (isFavorite) {
            userRepository.removeFavoriteAnimal(uid, animalId)
        } else {
            userRepository.addFavoriteAnimal(uid, animalId)
        }
    }
}
