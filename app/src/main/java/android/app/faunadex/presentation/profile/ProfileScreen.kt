package android.app.faunadex.presentation.profile

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.app.faunadex.R
import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.ConfirmationDialog
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.LanguageSelectionDialog
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.presentation.components.ProfilePicture
import android.app.faunadex.presentation.components.TopAppBar
import android.app.faunadex.presentation.components.TopAppBarUserData
import android.app.faunadex.ui.theme.*
import android.app.faunadex.utils.LanguageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ProfileScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.loadUserProfile()
    }

    ProfileScreenContent(
        uiState = uiState,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToQuiz = onNavigateToQuiz,
        onRetry = viewModel::retry,
        onLogout = {
            viewModel.logout()
            onNavigateToOnboarding()
        },
        onUploadProfilePicture = { uri ->
            viewModel.uploadProfilePicture(uri)
        },
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToChangePassword = onNavigateToChangePassword
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onRetry: () -> Unit,
    onLogout: () -> Unit = {},
    onUploadProfilePicture: (android.net.Uri) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    currentRoute: String = "profile"
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            when (uiState) {
                is ProfileUiState.Success -> {
                    val user = uiState.user
                    TopAppBar(
                        userData = TopAppBarUserData(
                            username = user.username,
                            profilePictureUrl = user.profilePictureUrl,
                            educationLevel = user.educationLevel,
                            currentLevel = (user.totalXp / 1000) + 1,
                            currentXp = user.totalXp % 1000,
                            xpForNextLevel = 1000
                        )
                    )
                }
                else -> {}
            }
        },
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> onNavigateToDashboard()
                        "quiz" -> onNavigateToQuiz()
                        "profile" -> { /* Already on profile */ }
                    }
                }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    LoadingContent()
                }
                is ProfileUiState.Success -> {
                    ProfileContent(
                        user = uiState.user,
                        onLogout = onLogout,
                        onUploadProfilePicture = onUploadProfilePicture,
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToChangePassword = onNavigateToChangePassword,
                        snackbarHostState = snackbarHostState,
                        scope = scope
                    )
                }
                is ProfileUiState.Error -> {
                    ErrorContent(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingSpinner()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error),
            fontFamily = JerseyFont,
            fontSize = 32.sp,
            color = PastelYellow,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontFamily = PoppinsFont,
            fontSize = 16.sp,
            color = White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreenLight
            )
        ) {
            Text(
                text = stringResource(R.string.retry),
                fontFamily = PoppinsFont,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onLogout: () -> Unit = {},
    onUploadProfilePicture: (android.net.Uri) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var isLanguageLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appContext = context.applicationContext
    var currentLanguage by remember { mutableStateOf(LanguageManager.getLanguage(appContext)) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onUploadProfilePicture(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profile),
            fontFamily = PoppinsFont,
            fontSize = 32.sp,
            color = PastelYellow,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(contentAlignment = Alignment.Center) {
            Log.d("ProfileScreen", "Profile Picture URL: ${user.profilePictureUrl}")

            ProfilePicture(
                imageUrl = user.profilePictureUrl,
                onEditClick = { imagePickerLauncher.launch("image/*") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = user.username.ifEmpty { stringResource(R.string.no_username) },
                fontFamily = JerseyFont,
                fontSize = 32.sp,
                color = PastelYellow
            )

            Text(
                text = user.email,
                fontFamily = JerseyFont,
                fontSize = 16.sp,
                color = MediumGreenSage
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileActionButton(
                icon = Icons.Outlined.Edit,
                text = stringResource(R.string.edit_profile),
                onClick = onNavigateToEditProfile
            )

            ProfileActionButton(
                icon = Icons.Outlined.Lock,
                text = stringResource(R.string.change_password),
                onClick = onNavigateToChangePassword
            )

            ProfileActionButton(
                icon = Icons.Outlined.Language,
                text = stringResource(R.string.language),
                onClick = { showLanguageDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.logout),
                    tint = PastelYellow,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.logout),
                    fontFamily = JerseyFont,
                    fontSize = 28.sp,
                    color = PastelYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    val logoutTitle = stringResource(R.string.logout)
    val logoutMessage = stringResource(R.string.logout_confirmation)
    val logoutConfirmText = stringResource(R.string.logout)
    val cancelText = stringResource(R.string.cancel)
    val loggedOutMessage = stringResource(R.string.logged_out_successfully)

    ConfirmationDialog(
        title = logoutTitle,
        message = logoutMessage,
        confirmText = logoutConfirmText,
        cancelText = cancelText,
        onConfirm = {
            showLogoutDialog = false
            scope.launch {
                launch {
                    snackbarHostState.showSnackbar(
                        message = loggedOutMessage,
                        duration = SnackbarDuration.Short
                    )
                }
                delay(500)
                onLogout()
            }
        },
        onDismiss = { showLogoutDialog = false },
        showDialog = showLogoutDialog
    )

    LanguageSelectionDialog(
        currentLanguage = currentLanguage,
        isLoading = isLanguageLoading,
        onLanguageSelected = { selectedLanguage ->
            Log.d("ProfileScreen", "onLanguageSelected: selected = $selectedLanguage, current = $currentLanguage")
            if (selectedLanguage != currentLanguage) {
                Log.d("ProfileScreen", "Language is different, applying change...")
                isLanguageLoading = true
                showLanguageDialog = false

                val activity = context.findActivity()
                if (activity != null) {
                    LanguageManager.setLanguageAndRestart(activity, selectedLanguage)
                } else {
                    Log.e("ProfileScreen", "Could not find activity!")
                    isLanguageLoading = false
                }
            } else {
                Log.d("ProfileScreen", "Same language selected, closing dialog")
                showLanguageDialog = false
            }
        },
        onDismiss = { showLanguageDialog = false },
        showDialog = showLanguageDialog
    )
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = PrimaryGreen
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = DarkForest,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = text,
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = MediumGreenSage,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.navigate),
                tint = MediumGreenSage,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FaunaDexTheme {
        ProfileScreenContent(
            uiState = ProfileUiState.Success(
                user = User(
                    uid = "preview123",
                    email = "rafi@test.com",
                    username = "raf_0411",
                    educationLevel = "SMA",
                    currentTitle = "Petualang",
                    totalXp = 450,
                    joinedAt = Date()
                )
            ),
            onNavigateToDashboard = {},
            onNavigateToQuiz = {},
            onRetry = {},
            onLogout = {},
            onUploadProfilePicture = {},
            onNavigateToEditProfile = {},
            onNavigateToChangePassword = {}
        )
    }
}

