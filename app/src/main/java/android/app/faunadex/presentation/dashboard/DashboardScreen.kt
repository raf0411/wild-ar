package android.app.faunadex.presentation.dashboard

import android.app.faunadex.R
import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.FaunaCard
import android.app.faunadex.presentation.components.FilterBottomSheet
import android.app.faunadex.presentation.components.FilterOption
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.presentation.components.LoadingSpinner
import android.app.faunadex.presentation.components.TopAppBar
import android.app.faunadex.presentation.components.TopAppBarUserData
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToQuiz: () -> Unit = {},
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
        onNavigateToQuiz = onNavigateToQuiz,
        onNavigateToAnimalDetail = onNavigateToAnimalDetail,
        viewModel = viewModel
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToQuiz: () -> Unit = {},
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

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val filterMyFavorites = stringResource(R.string.filter_my_favorites)
    val filterMammals = stringResource(R.string.filter_mammals)
    val filterBirds = stringResource(R.string.filter_birds)
    val filterReptiles = stringResource(R.string.filter_reptiles)
    val filterAmphibians = stringResource(R.string.filter_amphibians)
    val filterFish = stringResource(R.string.filter_fish)
    val filterEndangered = stringResource(R.string.filter_endangered)
    val filterEndemic = stringResource(R.string.filter_endemic)

    var appliedFilterOptions by remember {
        mutableStateOf(
            listOf(
                FilterOption("favorites", filterMyFavorites, false),
                FilterOption("mammal", filterMammals, false),
                FilterOption("bird", filterBirds, false),
                FilterOption("reptile", filterReptiles, false),
                FilterOption("amphibian", filterAmphibians, false),
                FilterOption("fish", filterFish, false),
                FilterOption("endangered", filterEndangered, false),
                FilterOption("endemic", filterEndemic, false)
            )
        )
    }

    var tempFilterOptions by remember { mutableStateOf(appliedFilterOptions) }

    Scaffold(
        topBar = {
            val user = uiState.user
            if (user != null) {
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
                        "quiz" -> onNavigateToQuiz()
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
                    label = stringResource(R.string.search_your_fauna),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(28.dp).padding(start = 6.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = DarkGreenShade
                        )
                    }
                )

                IconButton(
                    onClick = {
                        tempFilterOptions = appliedFilterOptions
                        showFilterSheet = true
                    }
                )
            }

            Spacer(Modifier.height(32.dp))


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

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel?.loadAnimals()
                    // Reset loading state after a short delay
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(1000)
                        isRefreshing = false
                    }
                },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
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
                                    text = stringResource(R.string.no_more_fauna),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            val filterTitle = stringResource(R.string.filter_fauna)
            val filterDescription = stringResource(R.string.filter_description)
            val filterAppliedMessage = stringResource(R.string.filter_applied)

            FilterBottomSheet(
                title = filterTitle,
                description = filterDescription,
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
                    tempFilterOptions = appliedFilterOptions
                    showFilterSheet = false
                },
                onSave = {
                    appliedFilterOptions = tempFilterOptions
                    showFilterSheet = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = filterAppliedMessage
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

