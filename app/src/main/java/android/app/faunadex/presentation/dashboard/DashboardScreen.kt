package android.app.faunadex.presentation.dashboard

import android.app.faunadex.domain.model.User
import android.app.faunadex.presentation.components.CustomTextField
import android.app.faunadex.presentation.components.FaunaBottomBar
import android.app.faunadex.presentation.components.FaunaCard
import android.app.faunadex.presentation.components.FaunaTopBar
import android.app.faunadex.presentation.components.FilterBottomSheet
import android.app.faunadex.presentation.components.FilterOption
import android.app.faunadex.presentation.components.IconButton
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
import androidx.compose.material3.CircularProgressIndicator
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
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToProfile: () -> Unit,
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
    var filterOptions by remember {
        mutableStateOf(
            listOf(
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
                    onClick = { showFilterSheet = true }
                )
            }

            Spacer(Modifier.height(32.dp))

            val allFaunaList = remember {
                listOf(
                    Triple("Sumatran Tiger", "Panthera tigris sumatrae", 0) to listOf("mammal", "endangered"),
                    Triple("Komodo Dragon", "Varanus komodoensis", 1) to listOf("reptile", "endangered", "endemic"),
                    Triple("Javan Rhinoceros", "Rhinoceros sondaicus", 2) to listOf("mammal", "endangered", "endemic"),
                    Triple("Orangutan", "Pongo pygmaeus", 3) to listOf("mammal", "endangered", "endemic"),
                    Triple("Bali Starling", "Leucopsar rothschildi", 4) to listOf("bird", "endangered", "endemic"),
                    Triple("Proboscis Monkey", "Nasalis larvatus", 5) to listOf("mammal", "endangered", "endemic"),
                    Triple("Anoa", "Bubalus depressicornis", 6) to listOf("mammal", "endangered", "endemic"),
                    Triple("Cenderawasih", "Paradisaea apoda", 7) to listOf("bird", "endemic"),
                    Triple("Maleo Bird", "Macrocephalon maleo", 8) to listOf("bird", "endangered", "endemic"),
                    Triple("Tarsius", "Tarsius tarsier", 9) to listOf("mammal", "endemic"),
                    Triple("Banteng", "Bos javanicus", 10) to listOf("mammal", "endangered", "endemic"),
                    Triple("Sun Bear", "Helarctos malayanus", 11) to listOf("mammal", "endangered"),
                    Triple("Clouded Leopard", "Neofelis nebulosa", 12) to listOf("mammal", "endangered"),
                    Triple("Slow Loris", "Nycticebus coucang", 13) to listOf("mammal", "endangered", "endemic"),
                    Triple("Malayan Tapir", "Tapirus indicus", 14) to listOf("mammal", "endangered"),
                    Triple("Sunda Pangolin", "Manis javanica", 15) to listOf("mammal", "endangered", "endemic"),
                    Triple("Javan Hawk-Eagle", "Nisaetus bartelsi", 16) to listOf("bird", "endangered", "endemic"),
                    Triple("Black Macaque", "Macaca nigra", 17) to listOf("mammal", "endangered", "endemic"),
                    Triple("Babirusa", "Babyrousa babyrussa", 18) to listOf("mammal", "endangered", "endemic"),
                    Triple("Javan Gibbon", "Hylobates moloch", 19) to listOf("mammal", "endangered", "endemic"),
                    Triple("Asian Elephant", "Elephas maximus", 20) to listOf("mammal", "endangered"),
                    Triple("Green Turtle", "Chelonia mydas", 21) to listOf("reptile", "endangered"),
                    Triple("Whale Shark", "Rhincodon typus", 22) to listOf("fish", "endangered"),
                    Triple("Manta Ray", "Mobula birostris", 23) to listOf("fish", "endangered"),
                    Triple("Dugong", "Dugong dugon", 24) to listOf("mammal", "endangered"),
                    Triple("Saltwater Crocodile", "Crocodylus porosus", 25) to listOf("reptile"),
                    Triple("False Gharial", "Tomistoma schlegelii", 26) to listOf("reptile", "endangered", "endemic"),
                    Triple("Rafflesia", "Rafflesia arnoldii", 27) to listOf("endemic"),
                    Triple("Javan Warty Pig", "Sus verrucosus", 28) to listOf("mammal", "endemic"),
                    Triple("Sumatran Rhino", "Dicerorhinus sumatrensis", 29) to listOf("mammal", "endangered", "endemic")
                )
            }

            val selectedFilters = remember(filterOptions) {
                filterOptions.filter { it.isSelected }.map { it.id }
            }

            val filteredFaunaList = remember(searchQuery, selectedFilters) {
                var result = allFaunaList

                if (selectedFilters.isNotEmpty()) {
                    result = result.filter { (_, tags) ->
                        selectedFilters.any { filter -> tags.contains(filter) }
                    }
                }

                if (searchQuery.isNotBlank()) {
                    result = result.filter { (fauna, _) ->
                        val (name, latinName, _) = fauna
                        name.contains(searchQuery, ignoreCase = true) ||
                                latinName.contains(searchQuery, ignoreCase = true)
                    }
                }

                result.map { it.first }
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
                    val (name, latinName, id) = displayedFaunaList[index]


                    FaunaCard(
                        faunaName = name,
                        latinName = latinName,
                        imageUrl = null,
                        isFavorite = id % 3 == 0,
                        onFavoriteClick = { /* TODO: Handle favorite toggle */ },
                        onCardClick = { /* TODO: Navigate to fauna detail */ }
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
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = PrimaryGreen
                            )
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
                filterOptions = filterOptions,
                onFilterToggle = { filterId ->
                    filterOptions = filterOptions.map { option ->
                        if (option.id == filterId) {
                            option.copy(isSelected = !option.isSelected)
                        } else {
                            option
                        }
                    }
                },
                onDismiss = { showFilterSheet = false },
                onSave = {
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
            onNavigateToProfile = {}
        )
    }
}

