package android.app.faunadex.presentation.auth.register

import android.app.faunadex.R
import android.app.faunadex.presentation.components.AuthButton
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.EducationLevelSelector
import android.app.faunadex.presentation.components.LayeredHeader
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.ui.theme.AlmostBlack
import android.app.faunadex.ui.theme.CodeNextFont
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenSage
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignUpSuccessful) {
        if (uiState.isSignUpSuccessful) {
            viewModel.resetSignUpState()
            onNavigateToDashboard()
        }
    }

    RegisterContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onEducationLevelChange = viewModel::onEducationLevelChange,
        onSignUpClick = viewModel::signUp,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
internal fun RegisterContent(
    uiState: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onEducationLevelChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkForest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                LayeredHeader(
                    text = stringResource(R.string.register),
                    fontSize = 28,
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
                        contentDescription = stringResource(R.string.wildar_logo),
                        alignment = Alignment.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = stringResource(R.string.email),
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
                        label = stringResource(R.string.email),
                        keyboardType = KeyboardType.Email,
                        enabled = !uiState.isLoading,
                        isError = uiState.errorMessage != null || uiState.errorMessageResId != null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.username),
                        fontFamily = JerseyFont,
                        fontSize = 28.sp,
                        color = PastelYellow,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = stringResource(R.string.username),
                        keyboardType = KeyboardType.Text,
                        enabled = !uiState.isLoading,
                        isError = uiState.errorMessage != null || uiState.errorMessageResId != null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.password),
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
                        label = stringResource(R.string.password),
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        enabled = !uiState.isLoading,
                        isError = uiState.errorMessage != null || uiState.errorMessageResId != null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.confirm_password),
                        fontFamily = JerseyFont,
                        fontSize = 28.sp,
                        color = PastelYellow,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = stringResource(R.string.confirm_password),
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        enabled = !uiState.isLoading,
                        isError = uiState.errorMessage != null || uiState.errorMessageResId != null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.your_education_level),
                        fontFamily = JerseyFont,
                        fontSize = 28.sp,
                        color = PastelYellow,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    EducationLevelSelector(
                        selectedLevel = uiState.educationLevel,
                        onLevelSelected = onEducationLevelChange,
                        enabled = !uiState.isLoading
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
                    } else if (uiState.errorMessageResId != null) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = stringResource(uiState.errorMessageResId),
                            fontFamily = JerseyFont,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AuthButton(
                        text = stringResource(R.string.submit),
                        onClick = onSignUpClick,
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
                        onClick = onNavigateToLogin,
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.already_have_account),
                            color = PastelYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

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
                LoadingSpinner()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FaunaDexTheme {
        RegisterContent(
            uiState = RegisterUiState(),
            onEmailChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onEducationLevelChange = {},
            onSignUpClick = {},
            onNavigateToLogin = {}
        )
    }
}
