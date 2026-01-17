package android.app.faunadex.presentation.animalDetail

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.model.EducationLevel
import android.app.faunadex.presentation.components.FaunaTopBarWithBack
import android.app.faunadex.ui.theme.BlueOcean
import android.app.faunadex.ui.theme.DarkForest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.ErrorRed
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PoppinsFont
import android.app.faunadex.ui.theme.PrimaryBlue
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.app.faunadex.R
import android.app.faunadex.presentation.components.ConservationStatusBadge
import android.app.faunadex.presentation.components.EndemicStatusBadge
import android.app.faunadex.presentation.components.PopulationTrendBadge
import android.app.faunadex.presentation.components.ActivityPeriodBadge
import android.app.faunadex.presentation.components.ProtectedStatusBadge
import android.app.faunadex.presentation.components.RarityBadge
import android.app.faunadex.presentation.components.IconButton
import android.app.faunadex.presentation.components.RibbonBadge
import android.app.faunadex.presentation.components.FunFactDialog
import android.app.faunadex.presentation.components.AnimalDescriptionDialog
import android.app.faunadex.presentation.components.AudioPlayerDialog
import android.app.faunadex.utils.AudioPlaybackState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf

@Composable
fun AnimalDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AnimalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userEducationLevel = viewModel.currentUserEducationLevel
    val audioPlaybackState by viewModel.audioPlaybackState.collectAsState()
    val audioCurrentPosition by viewModel.audioCurrentPosition.collectAsState()
    val audioDuration by viewModel.audioDuration.collectAsState()

    Log.d("AnimalDetailScreen", "======================================")
    Log.d("AnimalDetailScreen", "Education Level in Screen: $userEducationLevel")
    Log.d("AnimalDetailScreen", "UI State: ${uiState::class.simpleName}")
    Log.d("AnimalDetailScreen", "======================================")

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
                val animal = (uiState as AnimalDetailUiState.Success).animal
                Log.d("AnimalDetailScreen", "SUCCESS STATE - Animal: ${animal.name}, Education: $userEducationLevel")

                AnimalDetailContent(
                    animal = animal,
                    userEducationLevel = userEducationLevel,
                    audioPlaybackState = audioPlaybackState,
                    audioCurrentPosition = audioCurrentPosition,
                    audioDuration = audioDuration,
                    onAudioClick = { viewModel.playDescriptionAudio(animal.audioDescriptionUrl) },
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onStopAudioClick = { viewModel.stopAudio() },
                    onSeekTo = { position -> viewModel.seekTo(position) },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalDetailContent(
    animal: Animal,
    userEducationLevel: String,
    audioPlaybackState: AudioPlaybackState,
    audioCurrentPosition: Long,
    audioDuration: Long,
    onAudioClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onStopAudioClick: () -> Unit,
    onSeekTo: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AnimalDetailTab.INFO) }
    var showAudioDialog by remember { mutableStateOf(false) }

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

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { /* TODO: Launch AR view */ },
                icon = Icons.Outlined.ViewInAr,
                size = 48.dp,
                cornerRadius = 8.dp
            )
            Text(
                text = "AR",
                fontFamily = JerseyFont,
                fontSize = 24.sp,
                color = PastelYellow
            )
        }

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
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp,
                        top = 16.dp
                    )
            ) {
                if (shouldShowContent(userEducationLevel, minLevel = EducationLevelRequirement.SMP)) {
                    TabIndicators(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        userEducationLevel = userEducationLevel
                    )

                    Spacer(Modifier.height(24.dp))
                }


                when (selectedTab) {
                    AnimalDetailTab.INFO -> {
                        InfoTabContent(
                            animal = animal,
                            userEducationLevel = userEducationLevel,
                            onAudioClick = {
                                showAudioDialog = true
                                onAudioClick()
                            }
                        )
                    }
                    AnimalDetailTab.POPULATION -> {
                        PopulationTabContent(animal = animal)
                    }
                    AnimalDetailTab.HABITAT -> {
                        HabitatTabContent(animal = animal)
                    }
                    AnimalDetailTab.TAXONOMY -> {
                        TaxonomyTabContent(animal = animal)
                    }
                }
            }
        }

        // Show audio player dialog when button is clicked
        if (showAudioDialog) {
            AudioPlayerDialog(
                animalName = animal.name,
                playbackState = audioPlaybackState,
                currentPosition = audioCurrentPosition,
                duration = audioDuration,
                onPlayPauseClick = onPlayPauseClick,
                onSeekTo = onSeekTo,
                onDismiss = {
                    showAudioDialog = false
                    onStopAudioClick()
                }
            )
        }
    }
}

enum class AnimalDetailTab(val title: String) {
    INFO("Info"),
    POPULATION("Population"),
    HABITAT("Habitat"),
    TAXONOMY("Taxonomy")
}

@Composable
fun TabIndicators(
    selectedTab: AnimalDetailTab,
    onTabSelected: (AnimalDetailTab) -> Unit,
    userEducationLevel: String,
    modifier: Modifier = Modifier
) {
    val availableTabs = if (userEducationLevel == "SMA") {
        AnimalDetailTab.entries
    } else {
        AnimalDetailTab.entries.filter { it != AnimalDetailTab.TAXONOMY }
    }

    val hasFourTabs = availableTabs.size == 4

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        availableTabs.forEach { tab ->
            TabItem(
                text = tab.title,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                isCompact = hasFourTabs
            )
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = if (isCompact) 4.dp else 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontFamily = JerseyFont,
            fontSize = if (isCompact) 18.sp else 22.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PastelYellow else MediumGreenSage
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(if (isCompact) 50.dp else 60.dp)
                .height(3.dp)
                .background(
                    color = if (isSelected) PastelYellow else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoTabContent(
    animal: Animal,
    userEducationLevel: String,
    onAudioClick: () -> Unit
) {
    var showFunFactDialog by remember { mutableStateOf(false) }

    val effectiveEducationLevel = userEducationLevel.takeIf { it.isNotBlank() } ?: "SMA"
    Log.d("InfoTabContent", "Original level: '$userEducationLevel', Effective level: '$effectiveEducationLevel'")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = animal.name,
                    fontFamily = PoppinsFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelYellow
                )

                if (shouldShowContent(effectiveEducationLevel, minLevel = EducationLevelRequirement.SMP)) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = animal.scientificName,
                        fontFamily = PoppinsFont,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = MediumGreenSage
                    )
                }

                Spacer(Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ConservationStatusBadge(
                        status = animal.conservationStatus,
                        showFullName = true
                    )

                    when (effectiveEducationLevel) {
                        "SD" -> {
                            if (animal.rarityLevel.isNotEmpty()) {
                                RarityBadge(rarityLevel = animal.rarityLevel)
                            }
                            if (animal.endemicStatus.isNotEmpty()) {
                                EndemicStatusBadge(status = animal.endemicStatus)
                            }
                        }
                        "SMP" -> {
                            if (animal.endemicStatus.isNotEmpty()) {
                                EndemicStatusBadge(status = animal.endemicStatus)
                            }
                            if (animal.populationTrend.isNotEmpty()) {
                                PopulationTrendBadge(trend = animal.populationTrend)
                            }
                            if (animal.isProtected) {
                                ProtectedStatusBadge(
                                    isProtected = animal.isProtected,
                                    protectionType = animal.protectionType
                                )
                            }
                        }
                        "SMA" -> {
                            if (animal.endemicStatus.isNotEmpty()) {
                                EndemicStatusBadge(status = animal.endemicStatus)
                            }
                            if (animal.populationTrend.isNotEmpty()) {
                                PopulationTrendBadge(trend = animal.populationTrend)
                            }
                            if (animal.activityPeriod.isNotEmpty()) {
                                ActivityPeriodBadge(period = animal.activityPeriod)
                            }
                            if (animal.isProtected) {
                                ProtectedStatusBadge(
                                    isProtected = animal.isProtected,
                                    protectionType = animal.protectionType
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onAudioClick,
                icon = Icons.AutoMirrored.Outlined.VolumeUp
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = animal.description,
            fontSize = 18.sp,
            color = MediumGreenSage,
            fontFamily = JerseyFont,
            textAlign = TextAlign.Justify
        )

        Spacer(Modifier.height(8.dp))

        if (effectiveEducationLevel == "SMA") {
            var showDescriptionDialog by remember { mutableStateOf(false) }

            Text(
                text = "Read more...",
                fontSize = 18.sp,
                color = PastelYellow,
                fontFamily = JerseyFont,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .clickable { showDescriptionDialog = true }
            )

            AnimalDescriptionDialog(
                description = animal.longDescription.ifEmpty { animal.description },
                onDismiss = { showDescriptionDialog = false },
                showDialog = showDescriptionDialog
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (effectiveEducationLevel) {
                "SD" -> {
                    Log.d("InfoTabContent", "Showing SD ribbons")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        RibbonBadge(
                            icon = if (animal.diet.contains("Carnivore", ignoreCase = true)) {
                                Icons.Outlined.Pets
                            } else {
                                Icons.Outlined.Eco
                            },
                            text = animal.diet.ifEmpty { "Omnivore" },
                            hexagonBackgroundColor = PrimaryGreenLight,
                            modifier = Modifier.weight(1f)
                        )

                        RibbonBadge(
                            icon = Icons.Outlined.Star,
                            text = animal.specialTitle.ifEmpty { "Unique" },
                            hexagonBackgroundColor = PrimaryGreenLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                "SMP", "SMA" -> {
                    Log.d("InfoTabContent", "Showing Fun Fact button for $effectiveEducationLevel")
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                Log.d("InfoTabContent", "Fun Fact button clicked")
                                showFunFactDialog = true
                            },
                            icon = Icons.AutoMirrored.Outlined.HelpCenter,
                            size = 50.dp,
                            cornerRadius = 8.dp
                        )

                        Text(
                            text = "Fun Fact",
                            fontFamily = JerseyFont,
                            color = MediumGreenSage,
                            fontSize = 20.sp
                        )
                    }
                }
                else -> {
                    Log.d("InfoTabContent", "Unknown education level: $effectiveEducationLevel")
                }
            }
        }
    }

    FunFactDialog(
        title = "Fun Fact",
        content = animal.funFact.ifEmpty { "No fun fact available for this animal yet." },
        onDismiss = { showFunFactDialog = false },
        showDialog = showFunFactDialog
    )
}

@Composable
fun PopulationTabContent(animal: Animal) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Population Status",
            fontFamily = PoppinsFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow
        )

        Spacer(Modifier.height(24.dp))

        PopulationBarChart(
            pastPopulation = animal.populationPast,
            presentPopulation = animal.populationPresent,
            animate = startAnimation,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "IUCN Status",
                fontFamily = PoppinsFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MediumGreenSage
            )

            ConservationStatusBadge(
                status = animal.conservationStatus,
                showFullName = true
            )
        }
    }
}

@Composable
fun PopulationBarChart(
    pastPopulation: Int,
    presentPopulation: Int,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val maxPopulation = maxOf(pastPopulation, presentPopulation)

    val pastPercentage = if (maxPopulation > 0) pastPopulation.toFloat() / maxPopulation else 0f
    val presentPercentage = if (maxPopulation > 0) presentPopulation.toFloat() / maxPopulation else 0f

    val animatedPastPercentage by animateFloatAsState(
        targetValue = if (animate) pastPercentage else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 100
        ),
        label = "pastPercentageAnimation"
    )

    val animatedPresentPercentage by animateFloatAsState(
        targetValue = if (animate) presentPercentage else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = 400
        ),
        label = "presentPercentageAnimation"
    )

    val animatedPastPopulation by remember {
        derivedStateOf {
            (pastPopulation * animatedPastPercentage / pastPercentage.coerceAtLeast(0.001f)).toInt()
        }
    }

    val animatedPresentPopulation by remember {
        derivedStateOf {
            (presentPopulation * animatedPresentPercentage / presentPercentage.coerceAtLeast(0.001f)).toInt()
        }
    }

    val declineRatio = if (pastPopulation > 0) {
        presentPopulation.toFloat() / pastPopulation.toFloat()
    } else {
        1f
    }

    val presentColor = when {
        declineRatio >= 0.8f -> Color(0xFF60C659)
        declineRatio >= 0.5f -> Color(0xFFFFC107)
        declineRatio >= 0.3f -> Color(0xFFFC7F3F)
        else -> Color(0xFFD81E05)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        PopulationBar(
            label = "Past",
            population = animatedPastPopulation,
            percentage = animatedPastPercentage,
            color = Color(0xFF4CAF50)
        )

        PopulationBar(
            label = "Present",
            population = animatedPresentPopulation,
            percentage = animatedPresentPercentage,
            color = presentColor
        )
    }
}

@Composable
fun PopulationBar(
    label: String,
    population: Int,
    percentage: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontFamily = JerseyFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage.coerceIn(0f, 1f))
                    .background(
                        color = color,
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = formatPopulationNumber(population),
                    fontFamily = PoppinsFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }

        Text(
            text = formatPopulationNumber(population),
            fontFamily = PoppinsFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MediumGreenSage
        )
    }
}

private fun formatPopulationNumber(population: Int): String {
    return if (population == 0) {
        "No data"
    } else {
        "%,d".format(population)
    }
}

@Composable
fun HabitatTabContent(animal: Animal) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Habitat",
            fontFamily = PoppinsFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow
        )

        Spacer(Modifier.height(16.dp))

        HabitatMapPlaceholder(
            latitude = animal.latitude,
            longitude = animal.longitude,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Location Details
        HabitatLocationInfo(
            country = animal.country,
            city = animal.city,
            habitat = animal.habitat
        )
    }
}

@Composable
fun HabitatMapPlaceholder(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(250.dp)
            .background(
                color = Color(0xFF2C3E2E),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = MediumGreenSage.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = "Map Location",
                tint = MediumGreenSage,
                modifier = Modifier.size(48.dp)
            )

            if (latitude != 0.0 && longitude != 0.0) {
                Text(
                    text = "Lat: ${String.format(java.util.Locale.US, "%.4f", latitude)}",
                    fontFamily = PoppinsFont,
                    fontSize = 14.sp,
                    color = MediumGreenSage
                )
                Text(
                    text = "Long: ${String.format(java.util.Locale.US, "%.4f", longitude)}",
                    fontFamily = PoppinsFont,
                    fontSize = 14.sp,
                    color = MediumGreenSage
                )
            } else {
                Text(
                    text = "Map Preview",
                    fontFamily = PoppinsFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGreenSage
                )
            }
        }
    }
}

@Composable
fun HabitatLocationInfo(
    country: String,
    city: String,
    habitat: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (country.isNotEmpty() || city.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Location",
                        tint = PastelYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Location",
                        fontFamily = PoppinsFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PastelYellow
                    )
                }

                Text(
                    text = formatLocation(country, city),
                    fontFamily = PoppinsFont,
                    fontSize = 16.sp,
                    color = MediumGreenSage,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }
        }

        if (habitat.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Eco,
                        contentDescription = "Habitat",
                        tint = PastelYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Habitat Type",
                        fontFamily = PoppinsFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PastelYellow
                    )
                }

                Text(
                    text = habitat,
                    fontFamily = PoppinsFont,
                    fontSize = 16.sp,
                    color = MediumGreenSage,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }
        }
    }
}

private fun formatLocation(country: String, city: String): String {
    return when {
        country.isNotEmpty() && city.isNotEmpty() -> "$city, $country"
        country.isNotEmpty() -> country
        city.isNotEmpty() -> city
        else -> "Location not specified"
    }
}

@Composable
fun TaxonomyTabContent(animal: Animal) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Taxonomy Classification",
            fontFamily = PoppinsFont,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PastelYellow
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            TaxonomyIndicatorColumn(
                modifier = Modifier.width(60.dp)
            )

            TaxonomyTable(
                domain = animal.domain,
                kingdom = animal.kingdom,
                phylum = animal.phylum,
                taxonomyClass = animal.taxonomyClass,
                order = animal.order,
                family = animal.family,
                genus = animal.genus,
                species = animal.species,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TaxonomyIndicatorColumn(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "▲",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreenLight
            )
            Text(
                text = "Less\nSpecific",
                fontFamily = PoppinsFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGreenLight,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(320.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            PrimaryGreenLight,
                            Color(0xFF89A257),
                            DarkGreen
                        )
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(Modifier.height(8.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "More\nSpecific",
                fontFamily = PoppinsFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF89A257),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Text(
                text = "▼",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF89A257)
            )
        }
    }
}

@Composable
fun TaxonomyTable(
    domain: String,
    kingdom: String,
    phylum: String,
    taxonomyClass: String,
    order: String,
    family: String,
    genus: String,
    species: String,
    modifier: Modifier = Modifier
) {
    val taxonomyLevels = listOf(
        "Domain" to domain,
        "Kingdom" to kingdom,
        "Phylum" to phylum,
        "Class" to taxonomyClass,
        "Order" to order,
        "Family" to family,
        "Genus" to genus,
        "Species" to species
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        taxonomyLevels.forEachIndexed { index, (label, value) ->
            val gradient = index / 7f
            TaxonomyRow(
                label = label,
                value = value.ifEmpty { "-" },
                gradientPosition = gradient
            )
        }
    }
}

@Composable
fun TaxonomyRow(
    label: String,
    value: String,
    gradientPosition: Float,
    modifier: Modifier = Modifier
) {
    val labelBgColor = Color(
        red = (190 + (23 - 190) * gradientPosition) / 255f,
        green = (220 + (58 - 220) * gradientPosition) / 255f,
        blue = (127 + (37 - 127) * gradientPosition) / 255f
    )

    val valueBgColor = Color(
        red = (65 + (23 - 65) * gradientPosition) / 255f,
        green = (120 + (58 - 120) * gradientPosition) / 255f,
        blue = (69 + (37 - 69) * gradientPosition) / 255f
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(0.4f)
                .background(
                    color = labelBgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                fontFamily = JerseyFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (gradientPosition < 0.6f) DarkGreen else PrimaryGreenLight
            )
        }

        Box(
            modifier = Modifier
                .weight(0.6f)
                .background(
                    color = valueBgColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                fontFamily = JerseyFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryGreenLime
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSDPreview() {
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
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    rarityLevel = "Rare",
                    populationPast = 5000,
                    populationPresent = 3000,
                    latitude = -8.5569,
                    longitude = 119.4869,
                    country = "Indonesia",
                    city = "Komodo National Park",
                    domain = "Eukaryota",
                    kingdom = "Animalia",
                    phylum = "Chordata",
                    taxonomyClass = "Reptilia",
                    order = "Squamata",
                    family = "Varanidae",
                    genus = "Varanus",
                    species = "V. komodoensis"
                ),
                audioPlaybackState = AudioPlaybackState.IDLE,
                audioCurrentPosition = 0L,
                audioDuration = 0L,
                onAudioClick = {},
                onPlayPauseClick = {},
                onStopAudioClick = {},
                userEducationLevel = "SD"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSMPPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SMP", PrimaryBlue)
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
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    populationTrend = "Decreasing",
                    isProtected = true,
                    protectionType = "CITES Listed",
                    funFact = "Komodo dragons can eat up to 80% of their body weight in one meal! They are also excellent swimmers and can dive up to 4.5 meters deep.",
                    populationPast = 5000,
                    populationPresent = 3000,
                    latitude = -8.5569,
                    longitude = 119.4869,
                    country = "Indonesia",
                    city = "Komodo National Park",
                    domain = "Eukaryota",
                    kingdom = "Animalia",
                    phylum = "Chordata",
                    taxonomyClass = "Reptilia",
                    order = "Squamata",
                    family = "Varanidae",
                    genus = "Varanus",
                    species = "V. komodoensis"
                ),
                audioPlaybackState = AudioPlaybackState.IDLE,
                audioCurrentPosition = 0L,
                audioDuration = 0L,
                onAudioClick = {},
                onPlayPauseClick = {},
                onStopAudioClick = {},
                userEducationLevel = "SMP"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalDetailScreenSMAPreview() {
    FaunaDexTheme {
        Scaffold(
            topBar = {
                FaunaTopBarWithBack(
                    title = "Animal Detail",
                    onNavigateBack = {},
                    showBadge = true,
                    level = EducationLevel("SMA", BlueOcean)
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
                    description = "Gajah adalah hewan darat terbesar. Belalainya berguna untuk mengambil makanan dan minum air.",
                    conservationStatus = "Vulnerable",
                    diet = "Carnivore",
                    endemicStatus = "Endemic",
                    populationTrend = "Decreasing",
                    activityPeriod = "Diurnal",
                    isProtected = true,
                    protectionType = "National Park Species",
                    funFact = "Komodo dragons can eat up to 80% of their body weight in one meal! They are also excellent swimmers and can dive up to 4.5 meters deep. Female Komodo dragons can reproduce through parthenogenesis, meaning they can lay fertile eggs without mating with a male.",
                    populationPast = 5000,
                    populationPresent = 3000,
                    latitude = -8.5569,
                    longitude = 119.4869,
                    country = "Indonesia",
                    city = "Komodo National Park",
                    domain = "Eukaryota",
                    kingdom = "Animalia",
                    phylum = "Chordata",
                    taxonomyClass = "Reptilia",
                    order = "Squamata",
                    family = "Varanidae",
                    genus = "Varanus",
                    species = "V. komodoensis"
                ),
                audioPlaybackState = AudioPlaybackState.IDLE,
                audioCurrentPosition = 0L,
                audioDuration = 0L,
                onAudioClick = {},
                onPlayPauseClick = {},
                onStopAudioClick = {},
                userEducationLevel = "SMA"
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

enum class EducationLevelRequirement(val level: Int) {
    SD(1),
    SMP(2),
    SMA(3)
}

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
