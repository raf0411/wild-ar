package android.app.faunadex.presentation.auth.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.AuthResult
import android.app.faunadex.domain.usecase.SignInUseCase
import android.app.faunadex.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessageResId = null, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessageResId = null, errorMessage = null)
    }

    fun signIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null, errorMessage = null)

            when (val result = signInUseCase(_uiState.value.email, _uiState.value.password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignInSuccessful = true
                    )
                }
                is AuthResult.Error -> {
                    val errorResId = when (result.message) {
                        "Email and password cannot be empty" -> R.string.error_email_password_empty
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

    fun resetSignInState() {
        _uiState.value = _uiState.value.copy(isSignInSuccessful = false)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageResId: Int? = null,
    val errorMessage: String? = null,
    val isSignInSuccessful: Boolean = false
)

