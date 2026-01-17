package android.app.faunadex.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.domain.model.User
import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.repository.AnimalRepository
import android.app.faunadex.utils.FirebaseDataSeeder
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadAnimals()
    }

    private fun loadUser() {
        val user = getCurrentUserUseCase()
        _uiState.value = _uiState.value.copy(user = user)
    }

    fun loadAnimals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

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

    /**
     * Seed sample animals to Firebase Firestore
     * Call this function ONCE to populate your database with sample data
     * After seeding, you can remove or comment out the call
     */
    fun seedSampleData() {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "🌱 Starting to seed sample animals...")
                val firestore = FirebaseFirestore.getInstance()
                FirebaseDataSeeder.seedAnimals(firestore)
                Log.d("DashboardViewModel", "✅ Seeding completed! Reloading animals...")

                // Reload animals after seeding
                loadAnimals()
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "❌ Error seeding data: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = "Failed to seed data: ${e.message}"
                )
            }
        }
    }

}

data class DashboardUiState(
    val user: User? = null,
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSignedOut: Boolean = false
)

