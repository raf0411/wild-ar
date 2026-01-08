package android.app.faunadex.presentation.profile

import android.app.faunadex.domain.model.User
import android.app.faunadex.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkForest
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                LoadingContent()
            }
            is ProfileUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    onUpdateEducationLevel = { level ->
                        viewModel.updateEducationLevel(level)
                    }
                )
            }
            is ProfileUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::retry
                )
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
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = PastelYellow,
            strokeWidth = 6.dp
        )
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
            text = "Error",
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
                text = "Retry",
                fontFamily = PoppinsFont,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onUpdateEducationLevel: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title Header
        Text(
            text = "Profile",
            fontFamily = JerseyFont,
            fontSize = 48.sp,
            color = PastelYellow,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGreenSage
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                ProfileField(label = "Username", value = user.username.ifEmpty { "Not Set" })
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(label = "Email", value = user.email)
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    label = "Education Level",
                    value = user.educationLevel.ifEmpty { "Not Set - Please update below" }
                )

                // Show update buttons if education level is empty
                if (user.educationLevel.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Select Your Education Level:",
                        fontFamily = CodeNextFont,
                        fontSize = 14.sp,
                        color = PastelYellow,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { onUpdateEducationLevel("SD") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreenLight
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text("SD", fontFamily = CodeNextFont)
                        }

                        Button(
                            onClick = { onUpdateEducationLevel("SMP") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreenLight
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text("SMP", fontFamily = CodeNextFont)
                        }

                        Button(
                            onClick = { onUpdateEducationLevel("SMA") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreenLight
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text("SMA", fontFamily = CodeNextFont)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(label = "Current Title", value = user.currentTitle)
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(label = "Total XP", value = user.totalXp.toString())
                Spacer(modifier = Modifier.height(16.dp))

                user.joinedAt?.let { date ->
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    ProfileField(label = "Joined", value = dateFormat.format(date))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryGreenLight
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Level Progress",
                    fontFamily = JerseyFont,
                    fontSize = 28.sp,
                    color = White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simple XP progress bar
                LinearProgressIndicator(
                    progress = { (user.totalXp % 100) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    color = PastelYellow,
                    trackColor = DarkForest,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${user.totalXp % 100} / 100 XP",
                    fontFamily = PoppinsFont,
                    fontSize = 16.sp,
                    color = White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            color = PastelYellow,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontFamily = PoppinsFont,
            fontSize = 18.sp,
            color = White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FaunaDexTheme {
        ProfileContent(
            user = User(
                uid = "preview123",
                email = "rafi@test.com",
                username = "RaffiGamer",
                educationLevel = "SMA",
                currentTitle = "Petualang Pemula",
                totalXp = 450,
                joinedAt = Date()
            )
        )
    }
}

