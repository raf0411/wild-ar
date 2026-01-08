package android.app.faunadex.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = DarkGreenShade,
    onPrimaryContainer = PrimaryGreenLime,

    secondary = MediumGreenMint,
    onSecondary = DarkForest,
    secondaryContainer = DarkGreenTeal,
    onSecondaryContainer = AccentGreenLeaf,

    tertiary = BlueOcean,
    onTertiary = White,
    tertiaryContainer = BlueDark,
    onTertiaryContainer = BlueLight,

    error = ErrorRedBright,
    onError = White,
    errorContainer = ErrorRedDark,
    onErrorContainer = ErrorRedLight,

    background = AlmostBlack,
    onBackground = OffWhite,

    surface = DarkForest,
    onSurface = OffWhite,
    surfaceVariant = DarkGreenMoss,
    onSurfaceVariant = MediumGreenSage,

    outline = DarkGreenSage,
    outlineVariant = DarkGreenTeal
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = PrimaryGreenPale,
    onPrimaryContainer = DarkGreenDeep,

    secondary = MediumGreen,
    onSecondary = White,
    secondaryContainer = PrimaryGreenLight,
    onSecondaryContainer = DarkGreen,

    tertiary = PrimaryBlue,
    onTertiary = White,
    tertiaryContainer = BlueLight,
    onTertiaryContainer = BlueDark,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRedDark,

    background = DarkForest,
    onBackground = OffWhite,

    surface = White,
    onSurface = DarkForest,
    surfaceVariant = PastelYellow,
    onSurfaceVariant = DarkGreenMoss,

    outline = MediumGreenSage,
    outlineVariant = PrimaryGreenPale
)

@Composable
fun FaunaDexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}