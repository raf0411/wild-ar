package android.app.faunadex.presentation.profile

import android.app.faunadex.presentation.components.ConfirmationDialog
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = "Change Password",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = PrimaryGreen,
                    contentColor = PastelYellow
                )
            }
        },
        containerColor = DarkForest
    ) { paddingValues ->
        ChangePasswordContent(
            onChangePassword = { currentPassword, newPassword, onSuccess, onError ->
                viewModel.changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    onSuccess = {
                        onSuccess()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Password changed successfully!",
                                duration = SnackbarDuration.Short
                            )
                            kotlinx.coroutines.delay(1000)
                        }
                    },
                    onError = onError
                )
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ChangePasswordContent(
    onChangePassword: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Old Password",
            fontFamily = JerseyFont,
            fontSize = 24.sp,
            color = PastelYellow,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = currentPassword,
            onValueChange = {
                currentPassword = it
                errorMessage = ""
            },
            label = "Old Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "New Password",
            fontFamily = JerseyFont,
            fontSize = 24.sp,
            color = PastelYellow,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                errorMessage = ""
            },
            label = "New Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Confirm New Password",
            fontFamily = JerseyFont,
            fontSize = 24.sp,
            color = PastelYellow,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            label = "Confirm New Password",
            isPassword = true
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                fontFamily = PoppinsFont,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Change Password Button
        Button(
            onClick = {
                when {
                    currentPassword.isBlank() -> {
                        errorMessage = "Please enter your current password"
                    }
                    newPassword.isBlank() -> {
                        errorMessage = "Please enter a new password"
                    }
                    newPassword.length < 6 -> {
                        errorMessage = "Password must be at least 6 characters"
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "Passwords do not match"
                    }
                    else -> {
                        // Show confirmation dialog
                        showConfirmDialog = true
                    }
                }
            },
            modifier = Modifier
                .width(120.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = PastelYellow,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = "Save",
                    fontFamily = JerseyFont,
                    fontSize = 24.sp,
                    color = PastelYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Password Requirements:",
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• At least 6 characters",
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage
            )
            Text(
                text = "• Contains letters and numbers recommended",
                fontFamily = PoppinsFont,
                fontSize = 12.sp,
                color = MediumGreenSage
            )
        }
    }

    // Confirmation Dialog
    ConfirmationDialog(
        title = "Change Password",
        message = "Are you sure you want to change your password?",
        confirmText = "Confirm",
        cancelText = "Cancel",
        onConfirm = {
            isLoading = true
            onChangePassword(
                currentPassword,
                newPassword,
                { isLoading = false },
                { error ->
                    isLoading = false
                    errorMessage = error
                }
            )
        },
        onDismiss = { showConfirmDialog = false },
        showDialog = showConfirmDialog
    )
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Change Password",
                    onNavigateBack = {}
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            ChangePasswordContent(
                onChangePassword = { _, _, onSuccess, _ -> onSuccess() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

