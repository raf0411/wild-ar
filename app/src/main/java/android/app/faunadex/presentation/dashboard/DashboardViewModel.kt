package android.app.faunadex.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.usecase.ToggleFavoriteAnimalUseCase
import android.app.faunadex.domain.usecase.GetFavoriteAnimalIdsUseCase
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.repository.AnimalRepository
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val animalRepository: AnimalRepository,
    private val toggleFavoriteAnimalUseCase: ToggleFavoriteAnimalUseCase,
    private val getFavoriteAnimalIdsUseCase: GetFavoriteAnimalIdsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadAnimals()
        loadFavorites()
    }

    private fun loadUser() {
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user != null) {
                val result = getFavoriteAnimalIdsUseCase(user.uid)
                result.onSuccess { favoriteIds ->
                    _uiState.value = _uiState.value.copy(favoriteAnimalIds = favoriteIds.toSet())
                    Log.d("DashboardViewModel", "Loaded ${favoriteIds.size} favorites")
                }
            }
        }
    }

    fun loadAnimals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            kotlinx.coroutines.delay(1500)

            val result = animalRepository.getAllAnimals()

            result.onSuccess { animalList ->
                _uiState.value = _uiState.value.copy(
                    animals = animalList,
                    isLoading = false
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavorite(animalId: String) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user == null) {
                Log.e("DashboardViewModel", "Cannot toggle favorite: user not logged in")
                return@launch
            }

            val currentFavorites = _uiState.value.favoriteAnimalIds
            val isFavorite = currentFavorites.contains(animalId)

            // Optimistically update UI
            val newFavorites = if (isFavorite) {
                currentFavorites - animalId
            } else {
                currentFavorites + animalId
            }
            _uiState.value = _uiState.value.copy(favoriteAnimalIds = newFavorites)

            val result = toggleFavoriteAnimalUseCase(user.uid, animalId, isFavorite)
            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(favoriteAnimalIds = currentFavorites)
                Log.e("DashboardViewModel", "Failed to toggle favorite", exception)
            }
        }
    }

}

data class DashboardUiState(
    val user: User? = null,
    val animals: List<Animal> = emptyList(),
    val favoriteAnimalIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSignedOut: Boolean = false
)

