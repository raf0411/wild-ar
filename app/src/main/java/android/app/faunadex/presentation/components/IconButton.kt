package android.app.faunadex.presentation.components

import android.R.attr.contentDescription
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenPale
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Tune,
    iconTint: Color = DarkForest,
    backgroundColor: Color = PrimaryGreen,
    borderColor: Color = PrimaryGreen,
    borderWidth: Dp = 3.dp,
    cornerRadius: Dp = 12.dp,
    size: Dp = 56.dp,
    iconSize: Dp = 28.dp,
    contentDescription: String? = "Filter"
) {
    Surface(
        modifier = modifier
            .size(size)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(size)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonPreview() {
    FaunaDexTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            IconButton(
                onClick = {}
            )
        }
    }
}