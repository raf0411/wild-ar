package android.app.faunadex.presentation.ar

import android.Manifest
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ArScreen(
    onNavigateBack: () -> Unit = {},
    animalId: String? = null,
    viewModel: ArViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()
    
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
            viewModel.startScanning()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        animalId?.let { viewModel.loadAnimalForAr(it) }

        if (cameraPermissionState.status.isGranted) {
            viewModel.onPermissionGranted()
            viewModel.startScanning()
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted &&
        (uiState is ArUiState.Ready || uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced)) {
        ArCameraContent(
            uiState = uiState,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onPlaneDetected = viewModel::onPlaneDetected,
            onAnimalPlaced = { animal ->
                viewModel.placeAnimal(animal, 0f, 0f, 0f)
            },
            onClearAnimals = viewModel::clearPlacedAnimals
        )
    } else {
        ArScreenContent(
            uiState = uiState,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onRequestPermission = {
                cameraPermissionState.launchPermissionRequest()
            },
            onClearAnimals = viewModel::clearPlacedAnimals,
            showRationale = cameraPermissionState.status.shouldShowRationale
        )
    }
}

private const val DUMMY_MODEL_URL = "https://ampugrpczxyluircynug.supabase.co/storage/v1/object/public/wildar-3d-models/models/dummy/model_dummy.glb"

@Composable
fun ArCameraContent(
    uiState: ArUiState,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit,
    onPlaneDetected: (Int) -> Unit,
    onAnimalPlaced: (android.app.faunadex.domain.model.Animal) -> Unit,
    onClearAnimals: () -> Unit
) {
    var planeCount by remember { mutableIntStateOf(0) }
    var isModelPlaced by remember { mutableStateOf(false) }
    var isModelLoading by remember { mutableStateOf(false) }
    var arSceneViewRef by remember { mutableStateOf<ArSceneView?>(null) }
    var modelNodeRef by remember { mutableStateOf<ArModelNode?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                ArSceneView(context).apply {
                    arSceneViewRef = this

                    planeRenderer.isEnabled = true
                    planeRenderer.isVisible = true

                    configureSession { _, config ->
                        config.planeFindingMode = com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                        config.lightEstimationMode = com.google.ar.core.Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    }

                    var frameCounter = 0
                    onFrame = { _ ->
                        frameCounter++
                        val currentSession = arSession
                        if (currentSession == null && frameCounter % 60 == 0) {
                            android.util.Log.d("AR_DEBUG", "arSession is null in onFrame (frame $frameCounter)")
                        }

                        currentSession?.let { session ->
                            val allPlanes = session.getAllTrackables(com.google.ar.core.Plane::class.java)
                            val trackingPlanes = allPlanes.count { plane ->
                                plane.trackingState == com.google.ar.core.TrackingState.TRACKING
                            }

                            if (trackingPlanes != planeCount) {
                                android.util.Log.d("AR_DEBUG", "Plane count changed: $planeCount -> $trackingPlanes (total planes: ${allPlanes.size})")
                                planeCount = trackingPlanes
                                onPlaneDetected(trackingPlanes)
                            }
                        }
                    }

                    onTapAr = { hitResult, _ ->
                        android.util.Log.d("AR_DEBUG", "Tap detected! isModelPlaced=$isModelPlaced, isModelLoading=$isModelLoading, planeCount=$planeCount")

                        if (!isModelPlaced && !isModelLoading) {
                            // Allow placement even with planeCount=0 if we have a valid hitResult
                            isModelLoading = true
                            android.util.Log.d("AR_DEBUG", "Starting model load...")

                            val modelUrl = DUMMY_MODEL_URL
                            android.util.Log.d("AR_DEBUG", "Model URL: $modelUrl")

                            try {
                                val newModelNode = ArModelNode(
                                    engine = engine,
                                    modelGlbFileLocation = modelUrl,
                                    autoAnimate = true,
                                    scaleToUnits = 1.5f,
                                    centerOrigin = io.github.sceneview.math.Position(0f, 0f, 0f),
                                    onLoaded = {
                                        android.util.Log.d("AR_DEBUG", "Model loaded successfully!")
                                        isModelLoading = false
                                        isModelPlaced = true
                                        sessionState.selectedAnimal?.let { animal ->
                                            onAnimalPlaced(animal)
                                        }
                                    },
                                    onError = { e ->
                                        android.util.Log.e("AR_DEBUG", "Model load error: ${e.message}", e)
                                        isModelLoading = false
                                    }
                                )

                                android.util.Log.d("AR_DEBUG", "Creating anchor...")
                                newModelNode.anchor = hitResult.createAnchor()
                                android.util.Log.d("AR_DEBUG", "Adding child node...")
                                addChild(newModelNode)
                                modelNodeRef = newModelNode
                                android.util.Log.d("AR_DEBUG", "Model node added to scene")
                            } catch (e: Exception) {
                                android.util.Log.e("AR_DEBUG", "Exception during model creation: ${e.message}", e)
                                isModelLoading = false
                            }
                        } else {
                            android.util.Log.d("AR_DEBUG", "Tap ignored - conditions not met")
                        }
                    }
                }
            }
        )

        if (isModelLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryGreenLime, strokeWidth = 4.dp)
                    Text(
                        text = "Loading 3D Model...",
                        color = PastelYellow,
                        fontSize = 18.sp,
                        fontFamily = JerseyFont,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        ArCameraOverlay(
            planeCount = planeCount,
            isModelPlaced = isModelPlaced,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onClearAnimals = {
                modelNodeRef?.let { node ->
                    arSceneViewRef?.removeChild(node)
                    node.destroy()
                }
                modelNodeRef = null
                isModelPlaced = false
                onClearAnimals()
            }
        )
    }
}

@Composable
fun BoxScope.ArCameraOverlay(
    planeCount: Int,
    isModelPlaced: Boolean,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit,
    onClearAnimals: () -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(isModelPlaced) {
        if (isModelPlaced) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(3000L)
            showSuccessMessage = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = statusBarPadding.calculateTopPadding())
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = PrimaryGreenLight
            )
        }

        Surface(
            color = if (planeCount > 0) PrimaryGreen.copy(alpha = 0.9f) else Color.Gray.copy(alpha = 0.7f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (planeCount > 0) Icons.Default.CheckCircle else Icons.Default.CenterFocusWeak,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (planeCount > 0) "$planeCount Surface${if (planeCount > 1) "s" else ""}" else "Scanning...",
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (!isModelPlaced) {
        if (planeCount == 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 70.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Move camera to detect a plane surface",
                            color = Color.Red,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sessionState.selectedAnimal?.let { animal ->
                            Text(
                                text = "Ready to place: ${animal.name}",
                                color = PrimaryGreenLime,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tap to place the 3D model",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Surface detected! Tap anywhere to place",
                            color = White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            ArReticle()
        }
    }

    if (isModelPlaced) {
        val animal = sessionState.selectedAnimal
        val animalName = animal?.name ?: "3D Model"
        val scientificName = animal?.scientificName ?: "Demo Model"

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusBarPadding.calculateTopPadding() + 70.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(visible = showSuccessMessage) {
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Animal Placed!",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(64.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = animalName,
                        color = PastelYellow,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JerseyFont,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = scientificName,
                        color = MediumGreenSage,
                        fontSize = 16.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontFamily = JerseyFont,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(bottom = navigationBarPadding.calculateBottomPadding())
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = isModelPlaced) {
            IconButton(
                onClick = onClearAnimals,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Red.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Clear Model",
                    tint = PastelYellow,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Surface(
            color = PrimaryGreen.copy(alpha = 0.9f),
            shape = RoundedCornerShape(64.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = PastelYellow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isModelPlaced) "Model Active" else "Tap to Place",
                    color = PastelYellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont
                )
            }
        }
    }
}

@Composable
fun ArScreenContent(
    uiState: ArUiState,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onClearAnimals: () -> Unit = {},
    showRationale: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1B2A),
                            Color(0xFF1B263B),
                            Color(0xFF0D1B2A)
                        )
                    )
                )
        )

        if (uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced) {
            ArScanningGrid()
        }

        ArTopBar(
            onNavigateBack = onNavigateBack,
            planesDetected = sessionState.detectedPlanes
        )

        when (uiState) {
            is ArUiState.Initializing -> {
                ArLoadingState()
            }
            is ArUiState.CameraPermissionRequired -> {
                ArPermissionRequired(
                    onRequestPermission = onRequestPermission,
                    showRationale = showRationale
                )
            }
            is ArUiState.Ready, is ArUiState.Scanning -> {
                ArScanningInstructions(
                    planesDetected = if (uiState is ArUiState.Scanning) uiState.planesDetected else 0
                )
            }
            is ArUiState.AnimalPlaced -> {
                ArAnimalPlacedInfo(animal = uiState.animal)
            }
            is ArUiState.Error -> {
                ArErrorState(message = uiState.message)
            }
        }

        if (uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced) {
            ArBottomControls(
                placedAnimalsCount = sessionState.placedAnimals.size,
                onClearAnimals = onClearAnimals,
                onCapture = { /* TODO: Implement capture */ }
            )
        }

        if (uiState is ArUiState.Scanning) {
            ArReticle()
        }
    }
}

@Composable
fun BoxScope.ArTopBar(
    onNavigateBack: () -> Unit,
    planesDetected: Int
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = statusBarPadding.calculateTopPadding())
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = PrimaryGreenLight
            )
        }

        if (planesDetected > 0) {
            Surface(
                color = PrimaryGreen.copy(alpha = 0.9f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$planesDetected Surface${if (planesDetected > 1) "s" else ""} Found",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.ArLoadingState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = PrimaryGreenLime,
            strokeWidth = 4.dp
        )
        Text(
            text = "Initializing AR...",
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BoxScope.ArPermissionRequired(
    onRequestPermission: () -> Unit,
    showRationale: Boolean
) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = PastelYellow,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = if (showRationale) 
                "Camera permission is required\nto use AR features" 
            else 
                "Grant Camera Permission",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Grant Permission",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BoxScope.ArScanningInstructions(planesDetected: Int) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "scan")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Icon(
            imageVector = Icons.Default.CenterFocusWeak,
            contentDescription = null,
            tint = PrimaryGreenLime,
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
        )

        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (planesDetected == 0) 
                        "Point camera at a flat surface"
                    else
                        "Tap to place an animal",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (planesDetected == 0)
                        "Move your device slowly to detect surfaces"
                    else
                        "Surface detected! Ready to place 3D models",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BoxScope.ArAnimalPlacedInfo(animal: android.app.faunadex.domain.model.Animal) {
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 80.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = PrimaryGreen.copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Animal Placed!",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = animal.name,
                        color = White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.ArErrorState(message: String) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "AR Error",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            color = White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BoxScope.ArBottomControls(
    placedAnimalsCount: Int,
    onClearAnimals: () -> Unit,
    onCapture: () -> Unit
) {
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(bottom = navigationBarPadding.calculateBottomPadding())
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = placedAnimalsCount > 0) {
            IconButton(
                onClick = onClearAnimals,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Red.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Animals",
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(White, CircleShape)
                .border(4.dp, PrimaryGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onCapture,
                modifier = Modifier.size(62.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = DarkGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Surface(
            color = PrimaryGreen.copy(alpha = 0.9f),
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$placedAnimalsCount",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BoxScope.ArReticle() {

    Canvas(
        modifier = Modifier
            .align(Alignment.Center)
            .size(80.dp)
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = PrimaryGreenLime.copy(alpha = 0.5f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - radius * 0.3f, center.y),
            end = Offset(center.x + radius * 0.3f, center.y),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x, center.y - radius * 0.3f),
            end = Offset(center.x, center.y + radius * 0.3f),
            strokeWidth = 2.dp.toPx()
        )

        val bracketLength = radius * 0.4f
        val bracketDistance = radius * 0.8f
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y - bracketDistance),
            end = Offset(center.x - bracketDistance + bracketLength, center.y - bracketDistance),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y - bracketDistance),
            end = Offset(center.x - bracketDistance, center.y - bracketDistance + bracketLength),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Composable
fun ArScanningGrid() {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacing = 100.dp.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)

        var x = 0f
        while (x < size.width) {
            drawLine(
                color = PrimaryGreenLime.copy(alpha = alpha),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = pathEffect
            )
            x += gridSpacing
        }

        var y = 0f
        while (y < size.height) {
            drawLine(
                color = PrimaryGreenLime.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = pathEffect
            )
            y += gridSpacing
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArScreenPreview() {
    FaunaDexTheme {
        ArScreenContent(
            uiState = ArUiState.Scanning(planesDetected = 2),
            sessionState = ArSessionState(
                detectedPlanes = 2,
                placedAnimals = listOf()
            )
        )
    }
}