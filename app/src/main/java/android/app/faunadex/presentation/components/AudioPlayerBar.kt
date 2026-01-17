package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.utils.AudioPlaybackState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AudioPlayerBar(
    playbackState: AudioPlaybackState,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (playbackState == AudioPlaybackState.IDLE) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DarkForest.copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        when (playbackState) {
            is AudioPlaybackState.ERROR -> {
                AudioErrorContent(
                    message = playbackState.message,
                    onDismiss = onStopClick
                )
            }
            AudioPlaybackState.LOADING -> {
                AudioLoadingContent()
            }
            else -> {
                AudioPlayerContent(
                    isPlaying = playbackState == AudioPlaybackState.PLAYING,
                    currentPosition = currentPosition,
                    duration = duration,
                    onPlayPauseClick = onPlayPauseClick,
                    onStopClick = onStopClick
                )
            }
        }
    }
}

@Composable
private fun AudioPlayerContent(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play/Pause Button
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PastelYellow)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = DarkForest,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Progress and Time
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    fontFamily = PoppinsFont,
                    fontSize = 12.sp,
                    color = MediumGreenSage
                )
                Text(
                    text = formatTime(duration),
                    fontFamily = PoppinsFont,
                    fontSize = 12.sp,
                    color = MediumGreenSage
                )
            }

            Spacer(Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PastelYellow,
                trackColor = MediumGreenSage.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(4.dp))

            // Waveform animation when playing
            if (isPlaying) {
                AudioWaveformAnimation()
            }
        }

        Spacer(Modifier.width(12.dp))

        // Stop Button
        IconButton(
            onClick = onStopClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Stop",
                tint = MediumGreenSage,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AudioLoadingContent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = PastelYellow,
            strokeWidth = 3.dp
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = "Loading audio...",
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            color = MediumGreenSage
        )
    }
}

@Composable
private fun AudioErrorContent(
    message: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Audio Error",
                fontFamily = PoppinsFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B6B)
            )
            Text(
                text = message,
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage
            )
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Dismiss",
                tint = MediumGreenSage
            )
        }
    }
}

@Composable
private fun AudioWaveformAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(20) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        delayMillis = index * 30,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave$index"
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(16.dp)
                    .scale(scaleY = scale, scaleX = 1f)
                    .background(
                        color = PrimaryGreenLight.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
