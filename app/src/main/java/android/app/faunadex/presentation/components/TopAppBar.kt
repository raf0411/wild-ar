package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.util.*

data class TopAppBarUserData(
    val username: String,
    val profilePictureUrl: String? = null,
    val educationLevel: String,
    val currentLevel: Int,
    val currentXp: Int,
    val xpForNextLevel: Int
)

@Composable
fun TopAppBar(
    userData: TopAppBarUserData,
    modifier: Modifier = Modifier,
    showProfilePicture: Boolean = true,
    showEducationBadge: Boolean = true,
    showLevelAndProgress: Boolean = true
) {
    val greeting = getGreeting()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .padding(top = statusBarPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                if (showProfilePicture) {
                    ProfilePicture(
                        profilePictureUrl = userData.profilePictureUrl,
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = "$greeting!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = MediumGreenSage,
                        fontFamily = JerseyFont
                    )

                    Text(
                        text = userData.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelYellow,
                        fontFamily = JerseyFont
                    )

                    if (showEducationBadge) {
                        Spacer(Modifier.height(8.dp))

                        EducationLevelBadgeCompact(
                            educationLevel = userData.educationLevel
                        )
                    }
                }
            }

            if (showLevelAndProgress) {
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HexagonLevel(
                        level = userData.currentLevel
                    )

                    XpProgress(
                        currentXp = userData.currentXp,
                        maxXp = userData.xpForNextLevel,
                        progressColor = PrimaryGreenLight,
                        backgroundColor = DarkGreenShade
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePicture(
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MediumGreenSage)
            .border(2.dp, White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUrl != null && profilePictureUrl.isNotBlank()) {
            val imageData = if (profilePictureUrl.startsWith("data:image")) {
                val base64Data = profilePictureUrl.substringAfter("base64,")
                android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            } else {
                profilePictureUrl
            }

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageData)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 28.sp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 28.sp
                        )
                    }
                }
            )
        } else {
            Text(
                text = "👤",
                fontSize = 28.sp
            )
        }
    }
}

@Composable
private fun EducationLevelBadgeCompact(
    educationLevel: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (educationLevel) {
        "SD" -> ErrorRed
        "SMP" -> PrimaryBlue
        "SMA" -> BlueOcean
        else -> BlueOcean
    }

    Row(
        modifier = modifier
            .background(badgeColor, CircleShape)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Education Level:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
        Text(
            text = educationLevel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
    }
}

@Composable
private fun HexagonLevel(
    level: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .offset(x = 3.dp, y = 4.dp)
                .background(Black.copy(alpha = 0.6f), HexagonShape())
        )

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(PrimaryGreenLight, HexagonShape())
                .border(6.dp, DarkGreenTeal, HexagonShape()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreenMoss,
                fontFamily = JerseyFont
            )
        }
    }
}


/**
 * XP progress indicator
 */
@Composable
private fun XpProgress(
    currentXp: Int,
    maxXp: Int,
    progressColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maxXp > 0) currentXp.toFloat() / maxXp.toFloat() else 0f

    var animate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animate = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 300
        ),
        label = "xpProgressAnimation"
    )

    Column(
        modifier = modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = PastelYellow.copy(alpha = 0.8f),
                fontFamily = JerseyFont
            )
            Text(
                text = "$currentXp/$maxXp",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PastelYellow.copy(alpha = 0.7f),
                fontFamily = JerseyFont
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(progressColor)
            )
        }
    }
}


private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good night"
    }
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    FaunaDexTheme {
        TopAppBar(
            userData = TopAppBarUserData(
                username = "raf_0411",
                profilePictureUrl = null,
                educationLevel = "SMA",
                currentLevel = 5,
                currentXp = 450,
                xpForNextLevel = 1000
            )
        )
    }
}