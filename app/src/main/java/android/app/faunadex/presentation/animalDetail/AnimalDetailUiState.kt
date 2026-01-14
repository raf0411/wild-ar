package android.app.faunadex.presentation.animalDetail

import android.app.faunadex.domain.model.Animal

sealed class AnimalDetailUiState {
    object Loading : AnimalDetailUiState()
    data class Success(val animal: Animal) : AnimalDetailUiState()
    data class Error(val message: String) : AnimalDetailUiState()
}

