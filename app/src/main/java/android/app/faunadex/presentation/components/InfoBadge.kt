package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Generic info badge for displaying animal information
 * Used for various categories like endemic status, activity period, etc.
 */
@Composable
fun InfoBadge(
    text: String,
    icon: ImageVector? = null,
    backgroundColor: Color = PrimaryGreenLight,
    textColor: Color = White,
    borderColor: Color? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (borderColor != null) {
                    Modifier.border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }

        Text(
            text = text.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontFamily = JerseyFont
        )
    }
}

/**
 * Badge for endemic/native status
 */
@Composable
fun EndemicStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status.lowercase()) {
        "endemic" -> "Endemic to Indonesia" to Color(0xFF2E7D32)
        "native" -> "Native" to Color(0xFF1976D2)
        "introduced" -> "Introduced Species" to Color(0xFF757575)
        else -> status to PrimaryGreenLight
    }

    InfoBadge(
        text = text,
        backgroundColor = color,
        modifier = modifier
    )
}

/**
 * Badge for population trend
 */
@Composable
fun PopulationTrendBadge(
    trend: String,
    modifier: Modifier = Modifier
) {
    val (icon, text, color) = when (trend.lowercase()) {
        "increasing" -> Triple("↑", "Increasing", Color(0xFF2E7D32))
        "stable" -> Triple("→", "Stable", Color(0xFFFFC107))
        "decreasing" -> Triple("↓", "Decreasing", Color(0xFFD32F2F))
        else -> Triple("", trend, MediumGreenSage)
    }

    Row(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                fontFamily = JerseyFont
            )
            Spacer(Modifier.width(4.dp))
        }

        Text(
            text = text.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            fontFamily = JerseyFont
        )
    }
}

/**
 * Badge for activity period (nocturnal, diurnal, crepuscular)
 */
@Composable
fun ActivityPeriodBadge(
    period: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (period.lowercase()) {
        "nocturnal" -> "🌙 Nocturnal" to Color(0xFF1A237E)
        "diurnal" -> "☀️ Diurnal" to Color(0xFFF57C00)
        "crepuscular" -> "🌅 Crepuscular" to Color(0xFF6A1B9A)
        "cathemeral" -> "⏰ Cathemeral" to Color(0xFF00695C)
        else -> period to MediumGreenSage
    }

    Row(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            fontFamily = JerseyFont
        )
    }
}

/**
 * Badge for protected status
 */
@Composable
fun ProtectedStatusBadge(
    isProtected: Boolean,
    protectionType: String = "Protected by Law",
    modifier: Modifier = Modifier
) {
    if (!isProtected) return

    InfoBadge(
        text = protectionType,
        backgroundColor = Color(0xFF1565C0),
        modifier = modifier
    )
}

/**
 * Badge for size category
 */
@Composable
fun SizeCategoryBadge(
    sizeCategory: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (sizeCategory.lowercase()) {
        "small" -> "Small" to Color(0xFF558B2F)
        "medium" -> "Medium" to Color(0xFF1976D2)
        "large" -> "Large" to Color(0xFFE65100)
        "giant" -> "Giant" to Color(0xFF4A148C)
        else -> sizeCategory to PrimaryGreenLight
    }

    InfoBadge(
        text = text,
        backgroundColor = color,
        modifier = modifier
    )
}

/**
 * Badge for rarity level (simplified for SD education level)
 */
@Composable
fun RarityBadge(
    rarityLevel: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (rarityLevel.lowercase()) {
        "very rare" -> "Very Rare" to Color(0xFF880E4F)
        "rare" -> "Rare" to Color(0xFFD32F2F)
        "uncommon" -> "Uncommon" to Color(0xFFF57C00)
        "common" -> "Common" to Color(0xFF388E3C)
        else -> rarityLevel to MediumGreenSage
    }

    InfoBadge(
        text = text,
        backgroundColor = color,
        modifier = modifier
    )
}
