package android.app.faunadex.presentation.quiz

import android.app.faunadex.R
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.TopAppBar
import android.app.faunadex.presentation.components.TopAppBarUserData
import android.app.faunadex.utils.QuizLanguageHelper
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage

@Composable
fun QuizScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToQuizDetail: (String) -> Unit = {},
    currentRoute: String = "quiz",
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        android.util.Log.d("QuizScreen", "Screen shown, reloading data...")
        viewModel.refresh()
    }

    QuizScreenContent(
        uiState = uiState,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToQuizDetail = onNavigateToQuizDetail,
        currentRoute = currentRoute
    )
}

@Composable
fun QuizScreenContent(
    uiState: QuizUiState,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToQuizDetail: (String) -> Unit = {},
    currentRoute: String = "quiz"
) {
    Scaffold(
        topBar = {
            val user = uiState.user
            if (user != null) {
                TopAppBar(
                    userData = TopAppBarUserData(
                        username = user.username,
                        profilePictureUrl = user.profilePictureUrl,
                        educationLevel = user.educationLevel,
                        userType = user.userType,
                        currentLevel = (user.totalXp / 1000) + 1,
                        currentXp = user.totalXp % 1000,
                        xpForNextLevel = 1000
                    )
                )
            }
        },
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> onNavigateToDashboard()
                        "profile" -> onNavigateToProfile()
                        "quiz" -> { /* Already on quiz */ }
                    }
                }
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        QuizContent(
            modifier = Modifier.padding(paddingValues),
            availableQuizzes = uiState.availableQuizzes,
            completedQuizzes = uiState.completedQuizzes,
            isLoading = uiState.isLoading,
            onNavigateToQuizDetail = onNavigateToQuizDetail
        )
    }
}

@Composable
private fun QuizContent(
    modifier: Modifier = Modifier,
    availableQuizzes: List<android.app.faunadex.domain.model.Quiz>,
    completedQuizzes: List<android.app.faunadex.domain.model.Quiz>,
    isLoading: Boolean,
    onNavigateToQuizDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryGreen)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        SectionTitle(title = stringResource(R.string.quiz_available))

        Spacer(modifier = Modifier.height(12.dp))

        if (availableQuizzes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_available_quizzes),
                color = MediumGreenSage,
                fontSize = 18.sp,
                fontFamily = JerseyFont,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            availableQuizzes.forEach { quiz ->
                QuizCardItem(
                    title = QuizLanguageHelper.getQuizTitle(quiz, context),
                    subtitle = QuizLanguageHelper.getQuizShortDescription(quiz, context),
                    totalQuestions = quiz.totalQuestions,
                    imageUrl = quiz.imageUrl,
                    isCompleted = false,
                    onCardClick = { onNavigateToQuizDetail(quiz.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (completedQuizzes.isNotEmpty()) {
            SectionTitle(title = stringResource(R.string.quiz_completed))

            Spacer(modifier = Modifier.height(12.dp))

            completedQuizzes.forEach { quiz ->
                QuizCardItem(
                    title = QuizLanguageHelper.getQuizTitle(quiz, context),
                    subtitle = QuizLanguageHelper.getQuizShortDescription(quiz, context),
                    totalQuestions = quiz.totalQuestions,
                    imageUrl = quiz.imageUrl,
                    isCompleted = true,
                    onCardClick = {}
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        color = PastelYellow,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun QuizCardItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    totalQuestions: Int,
    imageUrl: String,
    isCompleted: Boolean = false,
    onCardClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MediumGreenSage.copy(alpha = 0.2f)
            else
                MediumGreenSage.copy(alpha = 0.1f)
        ),
        onClick = if (!isCompleted) onCardClick else { {} }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MediumGreenSage.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Construction,
                                contentDescription = null,
                                tint = PastelYellow,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryGreen.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = PastelYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = if (isCompleted)
                        PastelYellow.copy(alpha = 0.6f)
                    else
                        PastelYellow,
                    fontSize = if (title.length > 30) 16.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont,
                    maxLines = 2,
                    lineHeight = if (title.length > 30) 18.sp else 22.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = if (isCompleted)
                        PrimaryGreen.copy(alpha = 0.5f)
                    else
                        PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    fontFamily = JerseyFont
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.questions_format, totalQuestions),
                    color = if (isCompleted)
                        MediumGreenSage.copy(alpha = 0.4f)
                    else
                        MediumGreenSage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = JerseyFont
                )
            }

            if (!isCompleted) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open quiz",
                    tint = PastelYellow,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    FaunaDexTheme {
        QuizScreenContent(
            uiState = QuizUiState(
                user = android.app.faunadex.domain.model.User(
                    uid = "1",
                    username = "raf_0411",
                    email = "raf@example.com",
                    educationLevel = "SMA",
                    profilePictureUrl = null,
                    totalXp = 5450
                )
            ),
            onNavigateToDashboard = {},
            onNavigateToProfile = {}
        )
    }
}
