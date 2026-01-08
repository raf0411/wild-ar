package android.app.faunadex.presentation.auth.onboarding

import android.app.faunadex.R
import android.app.faunadex.ui.component.AuthButton
import android.app.faunadex.ui.component.LayeredHeader
import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.CodeNextFont
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenSage
import android.app.faunadex.ui.theme.DarkNeutral
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkForest)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LayeredHeader(
                text = "Welcome to",
                fontSize = 30,
                textColor = DarkGreenSage,
                fontWeight = FontWeight.Normal
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "WildAR Logo"
                )

                Spacer(modifier = Modifier.height(64.dp))

                AuthButton(
                    text = "Login",
                    onClick = onLoginClick,
                    baseColor = AlmostBlack,
                    shadeColor = DarkNeutral,
                    shineColor = DarkNeutral,
                    textColor = White,
                    strokeWidth = 24f,
                    shineHeight = 40.dp,
                    height = 100.dp,
                    fontSize = 40.sp,
                    fontFamily = CodeNextFont,
                    cornerRadius = 28.dp,
                    shadowElevation = 12.dp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(48.dp))

                AuthButton(
                    text = "Register",
                    onClick = onRegisterClick,
                    baseColor = PrimaryGreenLight,
                    shineColor = PrimaryGreenLime,
                    shadeColor = PrimaryGreenLime,
                    textColor = White,
                    strokeWidth = 16f,
                    shineHeight = 40.dp,
                    height = 100.dp,
                    fontSize = 40.sp,
                    fontFamily = CodeNextFont,
                    cornerRadius = 28.dp,
                    shadowElevation = 12.dp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun OnboardingScreenPreview() {
    FaunaDexTheme {
        OnboardingScreen()
    }
}
