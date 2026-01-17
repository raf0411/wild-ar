package android.app.faunadex.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Manages audio playback using ExoPlayer
 * Singleton to ensure only one audio plays at a time
 */
class AudioPlayerManager private constructor(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _playbackState = MutableStateFlow(AudioPlaybackState.IDLE as AudioPlaybackState)
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var currentUrl: String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> _playbackState.value = AudioPlaybackState.IDLE
                    Player.STATE_BUFFERING -> _playbackState.value = AudioPlaybackState.LOADING
                    Player.STATE_READY -> {
                        if (player.isPlaying) {
                            _playbackState.value = AudioPlaybackState.PLAYING
                        } else {
                            _playbackState.value = AudioPlaybackState.PAUSED
                        }
                        _duration.value = player.duration
                    }
                    Player.STATE_ENDED -> {
                        _playbackState.value = AudioPlaybackState.IDLE
                        _currentPosition.value = 0L
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    _playbackState.value = AudioPlaybackState.PLAYING
                    startPositionUpdate()
                } else if (player.playbackState == Player.STATE_READY) {
                    _playbackState.value = AudioPlaybackState.PAUSED
                    stopPositionUpdate()
                }
            }
        })
    }

    private fun startPositionUpdate() {
        positionUpdateJob?.cancel()
        positionUpdateJob = coroutineScope.launch {
            while (isActive && player.isPlaying) {
                _currentPosition.value = player.currentPosition
                delay(100) // Update every 100ms
            }
        }
    }

    private fun stopPositionUpdate() {
        positionUpdateJob?.cancel()
        _currentPosition.value = player.currentPosition
    }

    fun loadAndPlay(audioUrl: String) {
        if (audioUrl.isEmpty()) {
            _playbackState.value = AudioPlaybackState.ERROR("Audio not available")
            return
        }

        if (currentUrl == audioUrl && player.playbackState != Player.STATE_IDLE) {
            togglePlayPause()
            return
        }

        currentUrl = audioUrl
        _playbackState.value = AudioPlaybackState.LOADING

        try {
            val mediaItem = MediaItem.fromUri(audioUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            _playbackState.value = AudioPlaybackState.ERROR(e.message ?: "Failed to load audio")
        }
    }

    fun togglePlayPause() {
        if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(0)
            player.play()
        } else if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        stopPositionUpdate()
        player.stop()
        player.clearMediaItems()
        currentUrl = null
        _playbackState.value = AudioPlaybackState.IDLE
        _currentPosition.value = 0L
        _duration.value = 0L
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun release() {
        player.release()
    }

    companion object {
        @Volatile
        private var INSTANCE: AudioPlayerManager? = null

        fun getInstance(context: Context): AudioPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayerManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}

sealed interface AudioPlaybackState {
    data object IDLE : AudioPlaybackState
    data object LOADING : AudioPlaybackState
    data object PLAYING : AudioPlaybackState
    data object PAUSED : AudioPlaybackState
    data class ERROR(val message: String) : AudioPlaybackState
}
