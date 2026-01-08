package android.app.faunadex.presentation.auth.login

import android.R.attr.enabled
import android.R.attr.fontFamily
import android.R.attr.fontWeight
import android.app.faunadex.R
import android.app.faunadex.presentation.components.AuthButton
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.LayeredHeader
import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.CodeNextFont
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenSage
import android.app.faunadex.ui.theme.DarkNeutral
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignInSuccessful) {
        if (uiState.isSignInSuccessful) {
            viewModel.resetSignInState()
            onNavigateToDashboard()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClick = viewModel::signIn,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
internal fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkForest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LayeredHeader(
                    text = "Login",
                    fontSize = 40,
                    textColor = DarkGreenSage,
                    fontWeight = FontWeight.Normal
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 48.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "WildAR Logo",
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Email",
                    fontFamily = JerseyFont,
                    fontSize = 28.sp,
                    color = PastelYellow,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Password",
                    fontFamily = JerseyFont,
                    fontSize = 28.sp,
                    color = PastelYellow,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = uiState.errorMessage,
                        fontFamily = JerseyFont,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AuthButton(
                    text = "Submit",
                    onClick = onSignInClick,
                    baseColor = PrimaryGreenLight,
                    shineColor = PrimaryGreenLime,
                    shadeColor = PrimaryGreenLime,
                    textColor = White,
                    strokeWidth = 14f,
                    shineHeight = 32.dp,
                    height = 80.dp,
                    fontSize = 34.sp,
                    fontFamily = CodeNextFont,
                    cornerRadius = 28.dp,
                    shadowElevation = 12.dp,
                    fontWeight = FontWeight.Bold,
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onNavigateToRegister,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Don't have an account? Register",
                        color = PastelYellow
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AlmostBlack.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = PastelYellow,
                    strokeWidth = 6.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FaunaDexTheme {
        LoginContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onSignInClick = {},
            onNavigateToRegister = {}
        )
    }
}