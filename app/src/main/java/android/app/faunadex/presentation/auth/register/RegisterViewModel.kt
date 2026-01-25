package android.app.faunadex.presentation.auth.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.usecase.SignUpUseCase
import android.app.faunadex.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessageResId = null, errorMessage = null)
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, errorMessageResId = null, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessageResId = null, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessageResId = null, errorMessage = null)
    }

    fun onEducationLevelChange(educationLevel: String) {
        _uiState.value = _uiState.value.copy(educationLevel = educationLevel, errorMessageResId = null, errorMessage = null)
    }

    fun signUp() {
        val state = _uiState.value

        if (state.email.isBlank() || state.username.isBlank() ||
            state.password.isBlank() || state.educationLevel.isBlank()) {
            _uiState.value = state.copy(errorMessageResId = R.string.error_all_fields_required)
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(errorMessageResId = R.string.error_passwords_do_not_match)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null, errorMessage = null)

            when (val result = signUpUseCase(
                state.email,
                state.password,
                state.username,
                state.educationLevel
            )) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignUpSuccessful = true
                    )
                }
                is AuthResult.Error -> {
                    val errorResId = when (result.message) {
                        "All fields are required" -> R.string.error_all_fields_required
                        "Password must be at least 6 characters" -> R.string.error_password_too_short
                        "Invalid email format" -> R.string.error_invalid_email
                        else -> null
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessageResId = errorResId,
                        errorMessage = if (errorResId == null) result.message else null
                    )
                }
                is AuthResult.Loading -> {
                }
            }
        }
    }

    fun resetSignUpState() {
        _uiState.value = _uiState.value.copy(isSignUpSuccessful = false)
    }
}

data class RegisterUiState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val educationLevel: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null,
    val errorMessage: String? = null,
    val isSignUpSuccessful: Boolean = false
)

