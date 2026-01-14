package android.app.faunadex.presentation.animalDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.usecase.GetAnimalDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    private val getAnimalDetailUseCase: GetAnimalDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimalDetailUiState>(AnimalDetailUiState.Loading)
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    private val animalId: String? = savedStateHandle.get<String>("animalId")

    init {
        loadAnimalDetail()
    }

    private fun loadAnimalDetail() {
        viewModelScope.launch {
            _uiState.value = AnimalDetailUiState.Loading

            if (animalId.isNullOrBlank()) {
                Log.e("AnimalDetailViewModel", "Animal ID is null or blank")
                _uiState.value = AnimalDetailUiState.Error("Animal ID not found")
                return@launch
            }

            Log.d("AnimalDetailViewModel", "Loading animal with ID: $animalId")

            val result = getAnimalDetailUseCase(animalId)
            result.onSuccess { animal ->
                Log.d("AnimalDetailViewModel", "Animal loaded: ${animal.name}")
                _uiState.value = AnimalDetailUiState.Success(animal)
            }.onFailure { exception ->
                Log.e("AnimalDetailViewModel", "Failed to load animal", exception)
                _uiState.value = AnimalDetailUiState.Error(
                    exception.message ?: "Failed to load animal details"
                )
            }
        }
    }

    fun retry() {
        loadAnimalDetail()
    }
}

