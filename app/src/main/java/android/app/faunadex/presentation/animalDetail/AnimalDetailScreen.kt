package android.app.faunadex.presentation.animalDetail

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.ErrorRed
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryBlue
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.app.faunadex.R
import android.app.faunadex.ui.theme.MediumGreenSage
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AnimalDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AnimalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userEducationLevel = viewModel.currentUserEducationLevel

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = "Animal Detail",
                onNavigateBack = onNavigateBack,
                showBadge = true,
                level = getEducationLevelWithColor(userEducationLevel)
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        when (uiState) {
            is AnimalDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = PastelYellow,
                        strokeWidth = 6.dp
                    )
                }
            }
            is AnimalDetailUiState.Success -> {
                AnimalDetailContent(
                    animal = (uiState as AnimalDetailUiState.Success).animal,
                    userEducationLevel = userEducationLevel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is AnimalDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as AnimalDetailUiState.Error).message,
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
fun AnimalDetailContent(
    animal: Animal,
    userEducationLevel: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = animal.imageUrl ?: R.drawable.animal_dummy,
            contentDescription = animal.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.animal_dummy),
            error = painterResource(R.drawable.animal_dummy)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 240.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = DarkForest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = animal.name,
                    fontFamily = PoppinsFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelYellow
                )

                Spacer(Modifier.height(4.dp))

                if (shouldShowContent(userEducationLevel, minLevel = EducationLevelRequirement.SMP)) {
                    Text(
                        text = animal.scientificName,
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = MediumGreenSage
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SD", ErrorRed)
                )
            },
            containerColor = DarkForest
        ) { paddingValues ->
            AnimalDetailContent(
                modifier = Modifier.padding(paddingValues),
                animal = Animal(
                    id = "1",
                    name = "Komodo Dragon",
                    scientificName = "Varanus komodoensis",
                    category = "Reptile",
                    habitat = "Grasslands and forests",
                    description = "The largest living lizard species",
                    conservationStatus = "Vulnerable"
                ),
                userEducationLevel = "SD" // Preview with SD level to test visibility
            )
        }
    }
}

private fun getEducationLevelWithColor(level: String): EducationLevel {
    return when (level) {
        "SD" -> EducationLevel("SD", ErrorRed)
        "SMP" -> EducationLevel("SMP", PrimaryBlue)
        "SMA" -> EducationLevel("SMA", BlueOcean)
        else -> EducationLevel("SMA", BlueOcean) // Default to SMA
    }
}

/**
 * Education level requirement enum for content visibility
 * SD = 1, SMP = 2, SMA = 3
 */
enum class EducationLevelRequirement(val level: Int) {
    SD(1),
    SMP(2),
    SMA(3)
}

/**
 * Helper function to determine if content should be shown based on user's education level
 * @param userLevel Current user's education level (SD, SMP, or SMA)
 * @param minLevel Minimum education level required to view the content
 * @return true if user's level meets or exceeds the minimum requirement
 */
private fun shouldShowContent(
    userLevel: String,
    minLevel: EducationLevelRequirement
): Boolean {
    val userLevelValue = when (userLevel) {
        "SD" -> EducationLevelRequirement.SD.level
        "SMP" -> EducationLevelRequirement.SMP.level
        "SMA" -> EducationLevelRequirement.SMA.level
        else -> EducationLevelRequirement.SMA.level
    }
    return userLevelValue >= minLevel.level
}
