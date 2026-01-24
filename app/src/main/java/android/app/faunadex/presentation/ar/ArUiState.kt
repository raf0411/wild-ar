package android.app.faunadex.presentation.ar

import android.app.faunadex.domain.model.Animal

sealed class ArUiState {
    object Initializing : ArUiState()
    object CameraPermissionRequired : ArUiState()
    object Ready : ArUiState()
    data class Scanning(val planesDetected: Int = 0) : ArUiState()
    data class AnimalPlaced(
        val animal: Animal,
        val position: String = "Surface"
    ) : ArUiState()
    data class Error(val message: String) : ArUiState()
}

data class ArSessionState(
    val isArSupported: Boolean = true,
    val isSessionInitialized: Boolean = false,
    val detectedPlanes: Int = 0,
    val placedAnimals: List<PlacedAnimal> = emptyList(),
    val selectedAnimal: Animal? = null,
    val errorMessage: String? = null
)

data class PlacedAnimal(
    val animal: Animal,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val positionZ: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
