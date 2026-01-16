package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for displaying long animal descriptions with custom scrollbar
 */
@Composable
fun AnimalDescriptionDialog(
    description: String,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(200)
        )
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = DarkForest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scrollable content with custom scrollbar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        val scrollState = rememberScrollState()

                        Text(
                            text = description,
                            fontFamily = JerseyFont,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Justify,
                            fontWeight = FontWeight.Normal,
                            color = MediumGreenSage,
                            lineHeight = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                                .verticalScroll(scrollState)
                        )
                        if (scrollState.maxValue > 0) {
                            CustomScrollbar(
                                scrollState = scrollState,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .height(400.dp)
                                    .width(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close button at bottom
                    androidx.compose.material3.Button(
                        onClick = onDismiss,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontFamily = JerseyFont,
                            fontSize = 24.sp,
                            color = PastelYellow
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom scrollbar with rounded capsule design
 * Background: DarkGreen, Indicator: PrimaryGreen
 */
@Composable
fun CustomScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
    ) {
        // Scrollbar track (background)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(DarkGreen)
        )

        // Calculate thumb position and size
        val scrollPercentage = if (scrollState.maxValue > 0) {
            scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        } else {
            0f
        }

        val thumbHeightRatio = 0.3f
        val maxScrollOffset = 1f - thumbHeightRatio
        val thumbOffsetRatio = scrollPercentage * maxScrollOffset

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(thumbHeightRatio)
                    .offset(y = (400.dp * thumbOffsetRatio)) // 400.dp is the content height
                    .clip(RoundedCornerShape(50))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PrimaryGreen,
                                PrimaryGreenLight
                            )
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDescriptionDialogPreview() {
    FaunaDexTheme {
        AnimalDescriptionDialog(
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\u2028\u2028\n" +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Curabitur pretium tincidunt lacus. Nulla gravida orci a odio. Nullam varius, turpis et commodo pharetra, est eros bibendum elit, nec luctus magna felis sollicitudin mauris.\n" +
                    "Integer in mauris eu nibh euisrisus aaaaaaaaaaaaaa quam.\u2028\u2028Nullam varius, turpis et commodo pharetra, varius, turpis et ",
            onDismiss = {},
            showDialog = true,
        )
    }
}
