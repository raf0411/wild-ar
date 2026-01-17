package android.app.faunadex.presentation.components

import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PrimaryGreenAlpha60
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaunaTopBarWithBack(
    title: String,
    onNavigateBack: () -> Unit,
    showBadge: Boolean = false,
    level: EducationLevel = EducationLevel("Null", Red),
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.padding(start = 24.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = JerseyFont,
                    fontSize = 28.sp,
                    color = PrimaryGreenLight,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = PrimaryGreenAlpha60,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = PrimaryGreenLight,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        actions = {
            if (showBadge) {
                Box(
                    modifier = Modifier.padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EducationLevelBadge(level = level)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkGreen
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FaunaTopBarWithBackPreview() {
    FaunaDexTheme {
        FaunaTopBarWithBack(
            title = "Title",
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FaunaTopBarWithBadgePreview() {
    FaunaDexTheme {
        FaunaTopBarWithBack(
            title = "Title",
            onNavigateBack = {},
            showBadge = true,
            level = EducationLevel("SMA", BlueOcean)
        )
    }
}