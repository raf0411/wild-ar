package android.app.faunadex.presentation.components

import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EducationLevelBadge(
    level: EducationLevel
) {
    Box(
        modifier = Modifier
            .background(
                color = level.badgeColor,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = level.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow,
            fontFamily = JerseyFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EducationLevelBadgePreview() {
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        EducationLevelBadge(
            level = EducationLevel(
                name = "SMA",
                badgeColor = BlueOcean
            )
        )
    }
}