package android.app.faunadex.presentation.ar

import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.White
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ArScreen() {
    ArScreenContent()
}

@Composable
fun ArScreenContent() {
    Text("AR Screen", color = White)
}

@Preview(showBackground = true)
@Composable
fun ArScreenPreview() {
    FaunaDexTheme {
        ArScreenContent()
    }
}