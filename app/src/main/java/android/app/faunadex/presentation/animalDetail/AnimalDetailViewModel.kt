package android.app.faunadex.presentation.animalDetail

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.usecase.GetAnimalDetailUseCase
import android.app.faunadex.domain.usecase.GetCurrentUserUseCase
import android.app.faunadex.utils.AudioPlayerManager
import android.app.faunadex.utils.ArCoreSessionManager
import android.app.faunadex.utils.ArCoreStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    private val getAnimalDetailUseCase: GetAnimalDetailUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimalDetailUiState>(AnimalDetailUiState.Loading)
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    private val _arAvailability = MutableStateFlow<ArAvailabilityState>(ArAvailabilityState.Checking)
    val arAvailability: StateFlow<ArAvailabilityState> = _arAvailability.asStateFlow()

    private val animalId: String? = savedStateHandle.get<String>("animalId")

    private val audioPlayerManager = AudioPlayerManager.getInstance(application)
    val audioPlaybackState = audioPlayerManager.playbackState
    val audioCurrentPosition = audioPlayerManager.currentPosition
    val audioDuration = audioPlayerManager.duration

    private val arCoreSessionManager = ArCoreSessionManager(application)

    val currentUserEducationLevel: String
        get() {
            val user = getCurrentUserUseCase()
            val level = user?.educationLevel?.takeIf { it.isNotBlank() } ?: "SMA"
            Log.d("AnimalDetailViewModel", "Current user education level: '$level' (raw: '${user?.educationLevel}')")
            return level
        }

    val currentUserType: String
        get() {
            val user = getCurrentUserUseCase()
            val type = user?.userType ?: "Student"
            Log.d("AnimalDetailViewModel", "Current user type: '$type'")
            return type
        }

    init {
        val user = getCurrentUserUseCase()
        Log.d("AnimalDetailViewModel", "User info - uid: ${user?.uid}, email: ${user?.email}, level: '${user?.educationLevel}'")
        Log.d("AnimalDetailViewModel", "Education level is blank: ${user?.educationLevel.isNullOrBlank()}")
        loadAnimalDetail()
        checkArCoreAvailability()
    }

    private fun checkArCoreAvailability() {
        viewModelScope.launch {
            try {
                _arAvailability.value = ArAvailabilityState.Checking
                val status = arCoreSessionManager.checkArCoreAvailability()
                _arAvailability.value = when (status) {
                    ArCoreStatus.SUPPORTED -> ArAvailabilityState.Available
                    ArCoreStatus.NOT_INSTALLED -> ArAvailabilityState.NotInstalled
                    ArCoreStatus.UNSUPPORTED -> {
                        if (isProbablyEmulator()) {
                            ArAvailabilityState.Error("This emulator is not ARCore-capable. Use a Google Play x86_64 AVD and set Back Camera to VirtualScene.")
                        } else {
                            ArAvailabilityState.Unsupported
                        }
                    }
                    ArCoreStatus.UNKNOWN -> ArAvailabilityState.Error("AR support check timed out. Wait a moment, then tap AR again.")
                    ArCoreStatus.ERROR -> ArAvailabilityState.Error("Error checking AR support. Please try again.")
                }
                Log.d("AnimalDetailViewModel", "ARCore availability: ${_arAvailability.value}")
            } catch (e: Exception) {
                Log.e("AnimalDetailViewModel", "Error checking ARCore availability", e)
                _arAvailability.value = ArAvailabilityState.Error("Error checking AR support: ${e.message}")
            }
        }
    }

    fun refreshArCoreAvailability() {
        checkArCoreAvailability()
    }

    fun checkArBeforeNavigation(): ArAvailabilityState {
        return _arAvailability.value
    }

    // ...existing code...

    private fun loadAnimalDetail() {
        viewModelScope.launch {
            _uiState.value = AnimalDetailUiState.Loading

            kotlinx.coroutines.delay(1500)

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

    fun playDescriptionAudio(audioUrl: String) {
        audioPlayerManager.loadAndPlay(audioUrl)
    }

    fun togglePlayPause() {
        audioPlayerManager.togglePlayPause()
    }

    fun stopAudio() {
        audioPlayerManager.stop()
    }

    fun seekTo(position: Long) {
        audioPlayerManager.seekTo(position)
    }

    fun retry() {
        loadAnimalDetail()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stop()
    }
}

private fun isProbablyEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()
    val brand = Build.BRAND.lowercase()
    val product = Build.PRODUCT.lowercase()

    return fingerprint.startsWith("generic") ||
        fingerprint.contains("emulator") ||
        model.contains("sdk") ||
        manufacturer.contains("genymotion") ||
        brand.startsWith("generic") ||
        product.contains("sdk")
}

sealed class ArAvailabilityState {
    object Checking : ArAvailabilityState()
    object Available : ArAvailabilityState()
    object NotInstalled : ArAvailabilityState()
    object Unsupported : ArAvailabilityState()
    data class Error(val message: String) : ArAvailabilityState()
}
