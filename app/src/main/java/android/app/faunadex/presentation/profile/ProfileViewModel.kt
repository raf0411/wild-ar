package android.app.faunadex.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.repository.AuthRepository
import android.app.faunadex.domain.repository.UserRepository
import android.app.faunadex.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val currentUser = authRepository.getCurrentUser()
            Log.d("ProfileViewModel", "Current user from Auth: uid=${currentUser?.uid}, email=${currentUser?.email}")

            if (currentUser != null) {
                val result = getUserProfileUseCase(currentUser.uid)

                result.onSuccess { user ->
                    Log.d("ProfileViewModel", "User profile fetched: username=${user.username}, educationLevel=${user.educationLevel}, xp=${user.totalXp}")
                    _uiState.value = ProfileUiState.Success(user)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to fetch profile", exception)
                    _uiState.value = ProfileUiState.Error(
                        exception.message ?: "Failed to load profile"
                    )
                }
            } else {
                Log.e("ProfileViewModel", "No current user found")
                _uiState.value = ProfileUiState.Error("User not logged in")
            }
        }
    }

    fun updateEducationLevel(educationLevel: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                _uiState.value = ProfileUiState.Loading

                val updatedUser = currentState.user.copy(educationLevel = educationLevel)
                val result = userRepository.updateUserProfile(updatedUser)

                result.onSuccess {
                    Log.d("ProfileViewModel", "Education level updated to: $educationLevel")
                    _uiState.value = ProfileUiState.Success(updatedUser)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to update education level", exception)
                    _uiState.value = ProfileUiState.Error(
                        "Failed to update: ${exception.message}"
                    )
                }
            }
        }
    }

    fun retry() {
        loadUserProfile()
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

