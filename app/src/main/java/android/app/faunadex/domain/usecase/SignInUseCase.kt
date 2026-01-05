package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<User> {
        // Add validation logic here
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty")
        }
        return authRepository.signIn(email, password)
    }
}

