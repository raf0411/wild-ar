package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkForest
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Reusable dialog component for displaying information with an icon, content, and close button
 * Used primarily for showing fun facts about animals
 *
 * @param title The title text displayed at the top
 * @param content The main content text displayed in the middle
 * @param icon The icon displayed at the top (defaults to HelpCenter icon)
 * @param onDismiss Callback when dialog is dismissed
 * @param showDialog Boolean to control dialog visibility
 */
@Composable
fun FunFactDialog(
    title: String,
    content: String,
    icon: ImageVector = Icons.AutoMirrored.Outlined.HelpCenter,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            AnimatedVisibility(
                visible = showDialog,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 300)
                ) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 200)
                ) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(durationMillis = 200)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .widthIn(min = 280.dp, max = 500.dp)
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkForest
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(80.dp),
                                tint = PrimaryGreenLight
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = title,
                                fontFamily = PoppinsFont,
                                fontSize = 24.sp,
                                color = PastelYellow,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = content,
                                    fontFamily = JerseyFont,
                                    fontSize = 18.sp,
                                    color = MediumGreenSage,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
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
    }
}

/**
 * Preview for FunFactDialog
 */
@Preview(showBackground = true)
@Composable
fun FunFactDialogPreview() {
    FaunaDexTheme {
        FunFactDialog(
            title = "Fun Fact",
            content = "Komodo dragons are the largest living lizards in the world. They can grow up to 3 meters in length and weigh up to 70 kilograms.\n" +
                    "These amazing creatures have been around for millions of years and are found only in Indonesia!",
            onDismiss = {},
            showDialog = true
        )
    }
}
