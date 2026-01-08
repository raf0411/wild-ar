package android.app.faunadex.ui.component

import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.CodeNextFont
import android.app.faunadex.ui.theme.DarkNeutral
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF6200EE),
    shineColor: Color? = null,
    shadeColor: Color? = null,
    textColor: Color = Color.White,
    fontSize: TextUnit = 18.sp,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight = FontWeight.Bold,
    height: Dp = 56.dp,
    cornerRadius: Dp = 12.dp,
    strokeWidth: Float = 3f,
    shineHeight: Dp = 20.dp,
    shineOffsetFromTop: Dp = 16.dp,
    shadowElevation: Dp = 8.dp,
    enabled: Boolean = true
) {
    val actualShineColor = shineColor ?: baseColor.copy(alpha = 0.4f)
    val actualShadeColor = shadeColor ?: Color.White.copy(alpha = 0.6f)
    val buttonColor = if (enabled) baseColor else baseColor.copy(alpha = 0.5f)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) shadowElevation else shadowElevation / 2,
                shape = RoundedCornerShape(cornerRadius),
                clip = false
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(buttonColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(shineHeight)
                    .offset(y = -height / 2 + shineOffsetFromTop + shineHeight / 2)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                actualShadeColor,
                                actualShadeColor,
                                actualShineColor,
                            )
                        )
                    )
            )

            Text(
                text = text,
                style = TextStyle(
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    fontWeight = fontWeight,
                    color = AlmostBlack.copy(alpha = if (enabled) 1f else 0.6f),
                    drawStyle = Stroke(
                        width = strokeWidth,
                        join = StrokeJoin.Round
                    )
                )
            )

            Text(
                text = text,
                color = textColor.copy(alpha = if (enabled) 1f else 0.6f),
                fontSize = fontSize,
                fontFamily = fontFamily,
                fontWeight = fontWeight
            )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun AuthButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AuthButton(
            text = "Login",
            onClick = { },
            baseColor = AlmostBlack,
            shadeColor = DarkNeutral,
            shineColor = DarkNeutral,
            textColor = White,
            strokeWidth = 24f,
            shineHeight = 56.dp,
            height = 133.333.dp,
            fontSize = 51.sp,
            fontFamily = CodeNextFont,
            cornerRadius = 42.667.dp,
            shadowElevation = 12.dp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Register",
            onClick = { },
            baseColor = PrimaryGreenLight,
            shineColor = PrimaryGreenLime,
            shadeColor = PrimaryGreenLime,
            textColor = White,
            strokeWidth = 24f,
            shineHeight = 56.dp,
            height = 133.333.dp,
            cornerRadius = 42.667.dp,
            shadowElevation = 12.dp,
            fontFamily = CodeNextFont,
            fontSize = 51.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

