package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenTeal
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = minOf(width, height) / 2f

            for (i in 0..5) {
                val angle = Math.toRadians(60.0 * i - 30.0).toFloat()
                val x = centerX + radius * kotlin.math.cos(angle)
                val y = centerY + radius * kotlin.math.sin(angle)

                if (i == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
            close()
        }
        return Outline.Generic(path)
    }
}

class RibbonStringShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val cutDepth = height * 0.35f

            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(width, height)
            lineTo(width * 0.5f, height - cutDepth)
            lineTo(0f, height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun RibbonBadge(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconColor: Color = DarkForest,
    hexagonBackgroundColor: Color = PrimaryGreenLight,
    ribbonBackgroundColor: Color = PrimaryGreenLight,
    textColor: Color = PrimaryGreen
) {
    Column(
        modifier = modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.size(width = 120.dp, height = 140.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(55.dp)
                    .size(width = 55.dp, height = 85.dp)
                    .offset(y = 60.dp)
                    .clip(RibbonStringShape())
                    .background(ribbonBackgroundColor)
            )

            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(HexagonShape())
                    .background(hexagonBackgroundColor)
                    .border(
                        width = 6.dp,
                        color = DarkGreenTeal,
                        shape = HexagonShape()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = iconColor,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = text,
            fontFamily = JerseyFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier
                .padding(horizontal = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A4D2E)
@Composable
fun RibbonBadgePreview() {
    FaunaDexTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkForest)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ribbon Badge Examples",
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = PastelYellow
            )

            RibbonBadge(
                icon = Icons.Default.Pets,
                text = "Carnivore"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A4D2E)
@Composable
fun RibbonBadgeRowPreview() {
    FaunaDexTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkForest)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Multiple Ribbon Badges",
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = PastelYellow
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RibbonBadge(
                    icon = Icons.Default.Pets,
                    text = "Carnivore"
                )

                RibbonBadge(
                    icon = Icons.Default.Pets,
                    text = "Herbivore"
                )

                RibbonBadge(
                    icon = Icons.Default.Pets,
                    text = "Omnivore"
                )
            }
        }
    }
}

