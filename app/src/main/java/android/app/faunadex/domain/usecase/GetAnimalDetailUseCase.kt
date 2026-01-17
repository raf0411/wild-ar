package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.repository.AnimalRepository
import javax.inject.Inject

class GetAnimalDetailUseCase @Inject constructor(
    private val animalRepository: AnimalRepository
) {
    suspend operator fun invoke(animalId: String): Result<Animal> {
        if (animalId.isBlank()) {
            return Result.failure(Exception("Animal ID cannot be empty"))
        }
        return animalRepository.getAnimalById(animalId)
    }
}

