package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}

