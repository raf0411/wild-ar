package android.app.faunadex.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.usecase.SignOutUseCase
import android.app.faunadex.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = _uiState.value.copy(isSignedOut = true)
        }
    }
}

data class DashboardUiState(
    val user: User? = null,
    val isSignedOut: Boolean = false
)

