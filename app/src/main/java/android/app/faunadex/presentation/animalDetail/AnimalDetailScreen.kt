package android.app.faunadex.presentation.animalDetail

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.White
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnimalDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AnimalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FaunaTopBarWithBack(
                title = "Animal Detail",
                onNavigateBack = onNavigateBack
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Animal Detail Page: ${animal.name}", color = PastelYellow)
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
                    onNavigateBack = {}
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
                )
            )
        }
    }
}