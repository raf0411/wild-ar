package android.app.faunadex.presentation.components

import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.DarkGreenMoss
import android.app.faunadex.ui.theme.ErrorRed
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryBlue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EducationLevelSelector(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val levels = listOf(
        EducationLevel("SD", ErrorRed),
        EducationLevel("SMP", PrimaryBlue),
        EducationLevel("SMA", BlueOcean)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            EducationLevelOption(
                level = level,
                isSelected = selectedLevel == level.name,
                onClick = { onLevelSelected(level.name) },
                enabled = enabled
            )
            if (index < levels.size - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun EducationLevelOption(
    level: EducationLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(1.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) PastelYellow else DarkGreenMoss
                )
                .border(
                    width = 3.dp,
                    color = AlmostBlack,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .background(
                    color = level.badgeColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = level.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PastelYellow,
                fontFamily = JerseyFont
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EducationLevelSelectorPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                var selectedLevel by remember { mutableStateOf("SMP") }

                Text(
                    text = "Selected: $selectedLevel",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                EducationLevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { selectedLevel = it }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EducationLevelSelectorEmptyPreview() {
    FaunaDexTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            EducationLevelSelector(
                selectedLevel = "",
                onLevelSelected = {}
            )
        }
    }
}

