package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.repository.UserRepository
import javax.inject.Inject

class GetFavoriteAnimalIdsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String): Result<List<String>> {
        return userRepository.getFavoriteAnimalIds(uid)
    }
}
