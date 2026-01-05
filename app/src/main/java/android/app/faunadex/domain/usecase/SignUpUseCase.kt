package android.app.faunadex.domain.usecase

import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<User> {
        // Add validation logic here
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Invalid email format")
        }
        return authRepository.signUp(email, password)
    }
}

