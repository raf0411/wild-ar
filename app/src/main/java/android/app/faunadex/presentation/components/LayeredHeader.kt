package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenSage
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LayeredHeader(
    text: String,
    modifier: Modifier = Modifier,
    topLayerColor: Color = DarkGreenSage,
    middleLayerColor: Color = AlmostBlack,
    bottomLayerColor: Color = PrimaryGreenLight,
    circleBackgroundColor: Color = PastelYellow,
    circleBorderColor: Color = DarkForest,
    textColor: Color = DarkGreenSage,
    topLayerHeight: Dp = 50.dp,
    middleLayerHeight: Dp = 25.dp,
    bottomLayerHeight: Dp = 40.dp,
    bottomCornerRadius: Dp = 32.dp,
    circleSize: Dp = 140.dp,
    circleBorderWidth: Dp = 6.dp,
    fontSize: Int = 24,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topLayerHeight)
                    .background(topLayerColor)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(middleLayerHeight)
                    .background(middleLayerColor)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomLayerHeight)
                    .background(
                        color = bottomLayerColor,
                        shape = RoundedCornerShape(
                            bottomStart = bottomCornerRadius,
                            bottomEnd = bottomCornerRadius
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(circleSize)
                .offset(y = 20.dp)
                .border(
                    width = circleBorderWidth,
                    color = circleBorderColor,
                    shape = CircleShape
                )
                .background(
                    color = circleBackgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Shining white gradient effect at the top (like AuthButton)
            Box(
                modifier = Modifier
                    .size(circleSize * 0.6f, circleSize * 0.6f)
                    .offset(y = -(circleSize * 0.15f))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                White.copy(alpha = 0.6f),
                                White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Text(
                text = text,
                fontFamily = JerseyFont,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

