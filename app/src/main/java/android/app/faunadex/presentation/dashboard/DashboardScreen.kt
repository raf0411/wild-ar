package android.app.faunadex.presentation.dashboard

import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.FaunaCard
import android.app.faunadex.presentation.components.FaunaTopBar
import android.app.faunadex.presentation.components.FilterBottomSheet
import android.app.faunadex.presentation.components.FilterOption
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreenShade
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnimalDetail: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            onNavigateToLogin()
        }
    }

    DashboardScreenContent(
        uiState = uiState,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAnimalDetail = onNavigateToAnimalDetail,
        viewModel = viewModel
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnimalDetail: (String) -> Unit,
    viewModel: DashboardViewModel? = null,
    currentRoute: String = "dashboard"
) {
    var searchQuery by remember { mutableStateOf("") }
    var loadedItemsCount by remember { mutableIntStateOf(10) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var lastLoadedCount by remember { mutableIntStateOf(10) }
    val listState = rememberLazyGridState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showFilterSheet by remember { mutableStateOf(false) }

    // Applied filters (used for actual filtering)
    var appliedFilterOptions by remember {
        mutableStateOf(
            listOf(
                FilterOption("favorites", "My Favorites", false),
                FilterOption("mammal", "Mammals", false),
                FilterOption("bird", "Birds", false),
                FilterOption("reptile", "Reptiles", false),
                FilterOption("amphibian", "Amphibians", false),
                FilterOption("fish", "Fish", false),
                FilterOption("endangered", "Endangered Species", false),
                FilterOption("endemic", "Endemic to Indonesia", false)
            )
        )
    }

    // Temporary filters (used in the bottom sheet before saving)
    var tempFilterOptions by remember { mutableStateOf(appliedFilterOptions) }

    Scaffold(
        topBar = {
            FaunaTopBar(backgroundColor = PrimaryGreen)
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
        bottomBar = {
            FaunaBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "quiz" -> { /* TODO: Navigate to quiz */ }
                        "dashboard" -> { /* Already on dashboard */ }
                    }
                }
            )
        },
        containerColor = DarkForest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    label = "Search your Fauna...",
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(28.dp).padding(start = 6.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DarkGreenShade
                        )
                    }
                )

                IconButton(
                    onClick = {
                        // Reset temp filters to current applied filters when opening
                        tempFilterOptions = appliedFilterOptions
                        showFilterSheet = true
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            // DEBUG: Seed Sample Data Button (Remove after seeding)
            if (uiState.animals.isEmpty() && !uiState.isLoading) {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel?.seedSampleData()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = PastelYellow
                    )
                ) {
                    Text("🌱 Add Sample Animals to Firebase")
                }
                Spacer(Modifier.height(16.dp))
            }

            val animals = uiState.animals

            if (uiState.isLoading && animals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingSpinner()
                }
                return@Column
            }

            if (uiState.error != null && animals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        color = PastelYellow
                    )
                }
                return@Column
            }

            val selectedFilters = remember(appliedFilterOptions) {
                appliedFilterOptions.filter { it.isSelected }.map { it.id }
            }

            val filteredFaunaList = remember(animals, searchQuery, appliedFilterOptions, uiState.favoriteAnimalIds) {
                var result = animals

                if (selectedFilters.isNotEmpty()) {
                    result = result.filter { animal ->
                        selectedFilters.any { filter ->
                            when (filter) {
                                "favorites" ->
                                    uiState.favoriteAnimalIds.contains(animal.id)
                                "mammal", "bird", "reptile", "amphibian", "fish" ->
                                    animal.category.equals(filter, ignoreCase = true)
                                "endangered" ->
                                    animal.conservationStatus in listOf("CR", "EN", "VU")
                                "endemic" ->
                                    animal.endemicStatus.contains("Endemic", ignoreCase = true)
                                else -> false
                            }
                        }
                    }
                }

                if (searchQuery.isNotBlank()) {
                    result = result.filter { animal ->
                        animal.name.contains(searchQuery, ignoreCase = true) ||
                            animal.scientificName.contains(searchQuery, ignoreCase = true)
                    }
                }

                result
            }

            LaunchedEffect(searchQuery, selectedFilters) {
                loadedItemsCount = 10
                lastLoadedCount = 10
                isLoadingMore = false
            }

            val displayedFaunaList = remember(filteredFaunaList, loadedItemsCount) {
                filteredFaunaList.take(loadedItemsCount)
            }

            val hasMoreItems = displayedFaunaList.size < filteredFaunaList.size

            LaunchedEffect(listState, filteredFaunaList.size) {
                snapshotFlow {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                    lastVisible?.index
                }
                    .collect { lastVisibleIndex ->
                        if (lastVisibleIndex != null &&
                            lastVisibleIndex >= loadedItemsCount - 3 &&
                            loadedItemsCount < filteredFaunaList.size &&
                            !isLoadingMore &&
                            loadedItemsCount == lastLoadedCount) {
                            isLoadingMore = true
                            kotlinx.coroutines.delay(500)
                            val newCount = minOf(loadedItemsCount + 10, filteredFaunaList.size)
                            loadedItemsCount = newCount
                            lastLoadedCount = newCount
                            isLoadingMore = false
                        }
                    }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayedFaunaList.size) { index ->
                    val animal = displayedFaunaList[index]

                    FaunaCard(
                        faunaName = animal.name,
                        latinName = animal.scientificName,
                        imageUrl = animal.imageUrl,
                        isFavorite = uiState.favoriteAnimalIds.contains(animal.id),
                        onFavoriteClick = {
                            viewModel?.toggleFavorite(animal.id)
                        },
                        onCardClick = {
                            onNavigateToAnimalDetail(animal.id)
                        }
                    )
                }

                if (isLoadingMore && hasMoreItems) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingSpinner(size = 32.dp, strokeWidth = 3.dp)
                        }
                    }
                }

                if (!hasMoreItems && displayedFaunaList.isNotEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No more fauna to load",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                title = "Filter Fauna",
                description = "Select the filter categories you want to select to see on your home screen. You can update this anytime.",
                filterOptions = tempFilterOptions,
                onFilterToggle = { filterId ->
                    tempFilterOptions = tempFilterOptions.map { option ->
                        if (option.id == filterId) {
                            option.copy(isSelected = !option.isSelected)
                        } else {
                            option
                        }
                    }
                },
                onDismiss = {
                    // Reset temp filters when dismissing without saving
                    tempFilterOptions = appliedFilterOptions
                    showFilterSheet = false
                },
                onSave = {
                    // Apply the temp filters to the actual filters
                    appliedFilterOptions = tempFilterOptions
                    showFilterSheet = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Filter has been applied"
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreenContent(
            uiState = DashboardUiState(
                user = User(
                    uid = "abc123xyz456",
                    email = "test@example.com",
                    username = "TestUser"
                ),
                isSignedOut = false
            ),
            onNavigateToProfile = {},
            onNavigateToAnimalDetail = {}
        )
    }
}

