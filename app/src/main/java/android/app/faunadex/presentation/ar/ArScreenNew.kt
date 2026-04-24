package android.app.faunadex.presentation.ar

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.app.faunadex.R
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.faunadex.utils.ModelCache
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.withContext

private const val TAG = "ArScreenNew"

private const val MODEL_SCALE = 1f

private sealed interface ArRuntimeState {
    data object Checking : ArRuntimeState
    data object Ready : ArRuntimeState
    data class Unsupported(
        val message: String,
        val canRetry: Boolean = true
    ) : ArRuntimeState
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ArScreenNew(
    onNavigateBack: () -> Unit = {},
    animalId: String? = null,
    viewModel: ArViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var arRuntimeState by remember { mutableStateOf<ArRuntimeState>(ArRuntimeState.Checking) }
    var runtimeCheckNonce by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
        Log.d(TAG, "ArScreenNew launched with animalId: $animalId")
        animalId?.let {
            Log.d(TAG, "Loading animal for AR: $it")
            viewModel.loadAnimalForAr(it)
        }
    }

    val sessionState by viewModel.sessionState.collectAsState()

    LaunchedEffect(sessionState.selectedAnimal) {
        Log.d(TAG, "Selected animal updated: ${sessionState.selectedAnimal?.name}, URL: ${sessionState.selectedAnimal?.arModelUrl}")
    }

    LaunchedEffect(cameraPermissionState.status.isGranted, runtimeCheckNonce) {
        if (!cameraPermissionState.status.isGranted) {
            arRuntimeState = ArRuntimeState.Checking
            return@LaunchedEffect
        }
        arRuntimeState = ArRuntimeState.Checking
        arRuntimeState = checkArRuntimeState(context)
    }

    if (cameraPermissionState.status.isGranted) {
        when (val state = arRuntimeState) {
            ArRuntimeState.Checking -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = PrimaryGreenLime)
                        Text(
                            text = stringResource(R.string.ar_initializing),
                            color = White,
                            fontSize = 16.sp,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }

            ArRuntimeState.Ready -> {
                ArContent(
                    sessionState = sessionState,
                    onNavigateBack = onNavigateBack
                )
            }

            is ArRuntimeState.Unsupported -> {
                ArRuntimeErrorContent(
                    message = state.message,
                    canRetry = state.canRetry,
                    onRetry = { runtimeCheckNonce++ },
                    onNavigateBack = onNavigateBack
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.camera_permission_fail),
                color = White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun ArRuntimeErrorContent(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.75f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = PastelYellow,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.ar_error),
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    color = White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (canRetry) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = White
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.retry),
                                fontFamily = JerseyFont,
                                fontSize = 18.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreenLime),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreenLime)
                        ) {
                            Text(
                                text = stringResource(R.string.ar_go_back),
                                fontFamily = JerseyFont,
                                fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.ar_go_back),
                            fontFamily = JerseyFont,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

enum class ArState {
    SCANNING,
    READY,
    PLACING,
    PLACED,
    ERROR
}

@Composable
private fun ArContent(
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var arSceneView by remember { mutableStateOf<ARSceneView?>(null) }

    val animal = sessionState.selectedAnimal
    val modelUrl = animal?.arModelUrl
    val isAnimalLoaded = animal != null && !modelUrl.isNullOrBlank()

    LaunchedEffect(isAnimalLoaded) {
        Log.d(TAG, "Animal loaded: $isAnimalLoaded, name: ${animal?.name}")
    }

    var arState by remember { mutableStateOf(ArState.SCANNING) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingProgress by remember { mutableStateOf("") }

    var isCapturing by remember { mutableStateOf(false) }
    var showCaptureSuccess by remember { mutableStateOf(false) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    var childNodes by remember { mutableStateOf<List<AnchorNode>>(emptyList()) }

    var hasPlane by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableStateOf<Frame?>(null) }
    var placementJob by remember { mutableStateOf<Job?>(null) }

    var cachedModelPath by remember { mutableStateOf<String?>(null) }
    var isPreloading by remember { mutableStateOf(false) }

    LaunchedEffect(modelUrl) {
        cachedModelPath = null
    }

    DisposableEffect(Unit) {
        onDispose {
            placementJob?.cancel()
            childNodes.forEach { node ->
                try {
                    node.anchor.detach()
                    node.destroy()
                } catch (_: Exception) {
                }
            }
        }
    }

    LaunchedEffect(modelUrl) {
        if (modelUrl != null && cachedModelPath == null && !isPreloading) {
            isPreloading = true
            Log.d(TAG, "Pre-loading model in background: $modelUrl")
            try {
                val path = getOrDownloadModel(context, modelUrl) { progress ->
                    loadingProgress = progress
                }
                cachedModelPath = path
                loadingProgress = ""
                Log.d(TAG, "Model pre-loaded: $path")
            } catch (e: Exception) {
                Log.e(TAG, "Pre-load failed: ${e.message}")
            }
            isPreloading = false
        }
    }

    if (!isAnimalLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = PrimaryGreenLime)
                Text(
                    text = if (animal == null) stringResource(R.string.ar_loading_animal_data) else stringResource(R.string.ar_no_3d_model_available),
                    color = White,
                    fontSize = 16.sp,
                    fontFamily = JerseyFont,
                    textAlign = TextAlign.Center
                )
                if (animal != null && modelUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(stringResource(R.string.ar_go_back), color = White)
                    }
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            childNodes = childNodes,
            planeRenderer = true,
            onViewUpdated = {
                arSceneView = this
            },
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.focusMode = Config.FocusMode.AUTO
                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    config.depthMode = Config.DepthMode.AUTOMATIC
                }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            },
            onSessionUpdated = { session, frame ->
                try {
                    currentFrame = frame

                    val planes = session.getAllTrackables(Plane::class.java)
                        .filter { it.trackingState == TrackingState.TRACKING }

                    if (planes.isNotEmpty() && !hasPlane) {
                        hasPlane = true
                        if (arState == ArState.SCANNING) {
                            arState = ArState.READY
                            Log.d(TAG, "Plane detected (${planes.size} planes)")
                        }
                    }

                    if (!hasPlane && arState == ArState.SCANNING) {
                        val timestamp = frame.timestamp
                        if (timestamp > 3_000_000_000L) {
                            hasPlane = true
                            arState = ArState.READY
                            Log.d(TAG, "Auto-enabled placement")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Session update error: ${e.message}", e)
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    if (node != null) return@rememberOnGestureListener
                    if (placementJob?.isActive == true) return@rememberOnGestureListener

                    try {
                        if (arState != ArState.READY) {
                            Log.d(TAG, "Tap blocked - state: $arState")
                            return@rememberOnGestureListener
                        }

                        if (isPreloading && cachedModelPath == null) {
                            Log.d(TAG, "Tap blocked - model still downloading")
                            return@rememberOnGestureListener
                        }

                        val frame = currentFrame ?: return@rememberOnGestureListener
                        val modelSourceUrl = modelUrl

                        val anchorResult = createPlacementAnchor(frame, motionEvent)
                        if (anchorResult == null) {
                            Log.e(TAG, "No anchor created")
                            return@rememberOnGestureListener
                        }

                        val placementAnchor = anchorResult.first
                        val anchorSource = anchorResult.second
                        arState = ArState.PLACING
                        errorMessage = null

                        childNodes.forEach { existingNode ->
                            try {
                                existingNode.anchor.detach()
                                existingNode.destroy()
                            } catch (_: Exception) {
                            }
                        }
                        childNodes = emptyList()

                        placementJob?.cancel()
                        placementJob = scope.launch {
                            try {
                                val modelPath = cachedModelPath ?: run {
                                    loadingProgress = context.getString(R.string.ar_loading_model)
                                    getOrDownloadModel(context, modelSourceUrl) { loadingProgress = it }
                                }

                                loadingProgress = context.getString(R.string.ar_loading_model)

                                var modelInstance = modelLoader.loadModelInstance(modelPath)

                                if (modelInstance == null) {
                                    Log.w(TAG, "First load attempt failed, retrying...")
                                    kotlinx.coroutines.delay(120)
                                    modelInstance = modelLoader.loadModelInstance(modelPath)
                                }

                                if (modelInstance != null) {
                                    val modelNode = ModelNode(
                                        modelInstance = modelInstance,
                                        scaleToUnits = MODEL_SCALE
                                    ).apply {
                                        isEditable = true
                                    }

                                    val anchorNode = AnchorNode(
                                        engine = engine,
                                        anchor = placementAnchor
                                    ).apply {
                                        addChildNode(modelNode)
                                    }

                                    childNodes = listOf(anchorNode)
                                    arState = ArState.PLACED
                                    loadingProgress = ""
                                    Log.d(TAG, "Model placed from $anchorSource")
                                } else {
                                    throw Exception("Model failed to load. Tap to retry.")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Placement error: ${e.message}", e)
                                errorMessage = e.message
                                arState = ArState.ERROR
                                try {
                                    placementAnchor.detach()
                                } catch (_: Exception) {
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Tap handling error: ${e.message}", e)
                        errorMessage = e.message ?: context.getString(R.string.error_unknown)
                        arState = ArState.ERROR
                    }
                }
            )
        )

        ArOverlay(
            arState = arState,
            loadingProgress = loadingProgress,
            errorMessage = errorMessage,
            animalName = animal.name,
            scientificName = animal.scientificName,
            isPreloading = isPreloading && cachedModelPath == null,
            isCapturing = isCapturing,
            showCaptureSuccess = showCaptureSuccess,
            onNavigateBack = onNavigateBack,
            onClear = {
                placementJob?.cancel()
                childNodes.forEach { node ->
                    try { node.anchor.detach(); node.destroy() } catch (_: Exception) {}
                }
                childNodes = emptyList()
                arState = if (hasPlane) ArState.READY else ArState.SCANNING
                errorMessage = null
            },
            onRetry = {
                errorMessage = null
                arState = if (hasPlane) ArState.READY else ArState.SCANNING
            },
            onCapture = {
                scope.launch {
                    isCapturing = true
                    try {
                        val sceneView = arSceneView
                        if (sceneView != null) {
                            captureArScreenshot(context, sceneView)
                            showCaptureSuccess = true
                            kotlinx.coroutines.delay(2000L)
                            showCaptureSuccess = false
                        } else {
                            Log.e(TAG, "AR SceneView not available for capture")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Capture failed: ${e.message}")
                    }
                    isCapturing = false
                }
            }
        )
    }
}

@Composable
private fun BoxScope.ArOverlay(
    arState: ArState,
    loadingProgress: String,
    errorMessage: String?,
    animalName: String,
    scientificName: String,
    isPreloading: Boolean,
    isCapturing: Boolean,
    showCaptureSuccess: Boolean,
    onNavigateBack: () -> Unit,
    onClear: () -> Unit,
    onRetry: () -> Unit,
    onCapture: () -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    IconButton(
        onClick = onNavigateBack,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = statusBarPadding.calculateTopPadding() + 8.dp, start = 8.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.ar_back),
            tint = White,
            modifier = Modifier.size(32.dp)
        )
    }

    when (arState) {
        ArState.SCANNING -> {
            StatusCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp),
                text = stringResource(R.string.ar_scanning_for_surfaces),
                subText = stringResource(R.string.ar_point_at_surface_hint)
            )
        }

        ArState.READY -> {
            if (isPreloading) {
                StatusCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navBarPadding.calculateBottomPadding() + 24.dp),
                    text = stringResource(R.string.ar_downloading_model, animalName),
                    subText = if (loadingProgress.isNotBlank()) loadingProgress else stringResource(R.string.ar_please_wait),
                    backgroundColor = Color.DarkGray
                )
            } else {
                StatusCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navBarPadding.calculateBottomPadding() + 24.dp),
                    text = stringResource(R.string.ar_tap_to_place_named, animalName),
                    subText = stringResource(R.string.ar_tap_where_to_place),
                    backgroundColor = PrimaryGreen
                )
            }
        }

        ArState.PLACING -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryGreenLime)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = loadingProgress,
                            color = White,
                            fontSize = 16.sp,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
        }

        ArState.PLACED -> {
            var showSuccessMessage by remember { mutableStateOf(true) }
            var showGestureHint by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000L)
                showSuccessMessage = false
                showGestureHint = true
                kotlinx.coroutines.delay(5000L)
                showGestureHint = false
            }

            AnimatedVisibility(
                visible = showSuccessMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp)
            ) {
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
                            text = stringResource(R.string.ar_animal_placed),
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSuccessMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
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
                            color = Color(0xFF8FBC8F),
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showGestureHint,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarPadding.calculateBottomPadding() + 150.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.ar_interact_with_your_model),
                            color = PastelYellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomOutMap,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_pinch_zoom),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Rotate90DegreesCw,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_two_finger_rotate),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenWith,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_drag_to_move),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showCaptureSuccess,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_photo_saved_to_gallery),
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarPadding.calculateBottomPadding())
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = stringResource(R.string.ar_reset),
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(White, CircleShape)
                        .border(4.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onCapture,
                        enabled = !isCapturing,
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PrimaryGreen,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = stringResource(R.string.ar_capture),
                                tint = DarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        ArState.ERROR -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error),
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: stringResource(R.string.error_unknown),
                            color = White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = White)
                        ) {
                            Text(stringResource(R.string.retry), color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    text: String,
    subText: String? = null,
    backgroundColor: Color = Color.Black.copy(alpha = 0.7f)
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = White,
                fontSize = 16.sp,
                fontFamily = JerseyFont,
                textAlign = TextAlign.Center
            )
            if (subText != null) {
                Text(
                    text = subText,
                    color = White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun getOrDownloadModel(
    context: Context,
    url: String,
    onProgress: (String) -> Unit
): String = withContext(Dispatchers.IO) {
    if (ModelCache.isModelCached(context, url)) {
        onProgress(context.getString(R.string.ar_loading_from_cache))
        ModelCache.getCachedFilePath(context, url)?.let { cachedPath ->
            return@withContext "file://$cachedPath"
        }
    }

    onProgress(context.getString(R.string.ar_downloading))
    val downloadedPath = ModelCache.getCachedModelPath(context, url)
    if (downloadedPath != null) {
        onProgress(context.getString(R.string.ar_download_complete))
        return@withContext "file://$downloadedPath"
    }

    throw Exception("Failed to download model")
}

private fun createPlacementAnchor(
    frame: Frame,
    motionEvent: android.view.MotionEvent
): Pair<com.google.ar.core.Anchor, String>? {
    val hitResults = frame.hitTest(motionEvent.x, motionEvent.y)

    val planeHit = hitResults.firstOrNull { hit ->
        hit.trackable is Plane && hit.trackable.trackingState == TrackingState.TRACKING
    }
    if (planeHit != null) {
        return createAnchorSafely(planeHit)?.let { it to "Plane" }
    }

    val trackedHit = hitResults.firstOrNull { hit ->
        hit.trackable?.trackingState == TrackingState.TRACKING
    }
    if (trackedHit != null) {
        val source = trackedHit.trackable?.javaClass?.simpleName ?: "Trackable"
        return createAnchorSafely(trackedHit)?.let { it to source }
    }

    val instantHit = frame.hitTestInstantPlacement(motionEvent.x, motionEvent.y, 2.0f).firstOrNull()
    if (instantHit != null) {
        return createAnchorSafely(instantHit)?.let { it to "instant_placement" }
    }

    return null
}

private fun createAnchorSafely(hitResult: HitResult): com.google.ar.core.Anchor? {
    return try {
        hitResult.createAnchor()
    } catch (e: Exception) {
        Log.e(TAG, "Anchor creation failed: ${e.message}", e)
        null
    }
}

@Composable
private fun rememberOnGestureListener(
    onSingleTapConfirmed: (android.view.MotionEvent, io.github.sceneview.node.Node?) -> Unit
) = io.github.sceneview.rememberOnGestureListener(
    onSingleTapConfirmed = onSingleTapConfirmed
)

private suspend fun captureArScreenshot(context: Context, arSceneView: ARSceneView) = withContext(Dispatchers.Main) {
    try {
        if (!arSceneView.isAttachedToWindow) {
            throw Exception("AR view is not attached")
        }
        if (arSceneView.width <= 0 || arSceneView.height <= 0) {
            throw Exception("AR view is not laid out yet")
        }

        val bitmap = Bitmap.createBitmap(
            arSceneView.width,
            arSceneView.height,
            Bitmap.Config.ARGB_8888
        )

        val copyResult = suspendCancellableCoroutine { continuation ->
            PixelCopy.request(
                arSceneView,
                bitmap,
                { result: Int ->
                    continuation.resume(result)
                },
                android.os.Handler(android.os.Looper.getMainLooper())
            )
        }

        if (copyResult != PixelCopy.SUCCESS) {
            Log.e(TAG, "PixelCopy failed with code: $copyResult")
            throw Exception("Failed to capture AR scene (error code: $copyResult)")
        }

        withContext(Dispatchers.IO) {
            saveBitmapToGallery(context, bitmap)
        }

        Log.d(TAG, "AR Screenshot captured and saved (${bitmap.width}x${bitmap.height})")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to capture AR screenshot: ${e.message}")
        throw e
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "WildAR!_AR_${System.currentTimeMillis()}.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WildAR!")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        Log.d(TAG, "Image saved to gallery: $uri")
    } ?: throw Exception("Failed to create media entry")
}

private suspend fun checkArRuntimeState(context: Context): ArRuntimeState = withContext(Dispatchers.Main) {
    try {
        if (isKnownUnstableArCoreDevice()) {
            val fingerprint = Build.FINGERPRINT
            Log.e(TAG, "Blocking AR on known unstable device fingerprint: $fingerprint")
            return@withContext ArRuntimeState.Unsupported(
                message = "AR is temporarily unavailable on this device build. Please wait for an ARCore/device update.",
                canRetry = false
            )
        }

        val arCoreApk = ArCoreApk.getInstance()
        var availability = arCoreApk.checkAvailability(context)
        var retryCount = 0

        while (availability.isTransient && retryCount < 8) {
            kotlinx.coroutines.delay(250L)
            availability = arCoreApk.checkAvailability(context)
            retryCount++
        }

        Log.d(
            TAG,
            "ARCore availability=$availability, retries=$retryCount, fingerprint=${Build.FINGERPRINT}, model=${Build.MODEL}"
        )

        when (availability) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> Unit

            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                val emulatorHint = if (isProbablyEmulator()) {
                    " If this is an emulator, ARCore usually needs a Google Play x86_64 AVD with Back Camera set to VirtualScene; ARM64 AVDs often report not capable."
                } else {
                    ""
                }
                return@withContext ArRuntimeState.Unsupported("This device is not compatible with ARCore.$emulatorHint")
            }

            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                val activity = context.findActivity()
                if (activity == null) {
                    return@withContext ArRuntimeState.Unsupported(
                        "Google Play Services for AR is missing or outdated. Open AR again and retry.",
                        canRetry = true
                    )
                }

                return@withContext when (ArCoreApk.getInstance().requestInstall(activity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> ArRuntimeState.Ready
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> ArRuntimeState.Unsupported(
                        "Please complete Google Play Services for AR installation, then tap Retry.",
                        canRetry = true
                    )
                }
            }

            ArCoreApk.Availability.UNKNOWN_CHECKING,
            ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                return@withContext ArRuntimeState.Unsupported("AR support check is still initializing. Please wait a moment and tap Retry.")
            }

            else -> {
                return@withContext ArRuntimeState.Unsupported("AR is currently unavailable on this device")
            }
        }

        ArRuntimeState.Ready
    } catch (_: UnavailableDeviceNotCompatibleException) {
        ArRuntimeState.Unsupported("This device is not compatible with ARCore")
    } catch (_: UnavailableArcoreNotInstalledException) {
        ArRuntimeState.Unsupported("Google Play Services for AR is not installed")
    } catch (_: UnavailableApkTooOldException) {
        ArRuntimeState.Unsupported("Google Play Services for AR needs to be updated")
    } catch (_: UnavailableSdkTooOldException) {
        ArRuntimeState.Unsupported("App AR SDK is outdated for this device")
    } catch (_: UnavailableUserDeclinedInstallationException) {
        ArRuntimeState.Unsupported("AR installation was canceled. Please retry and accept the installation prompt.")
    } catch (_: UnavailableException) {
        ArRuntimeState.Unsupported("AR is currently unavailable on this device")
    } catch (_: SecurityException) {
        ArRuntimeState.Unsupported("Camera permission is required for AR")
    } catch (e: Exception) {
        Log.e(TAG, "AR runtime probe unexpected error: ${e.message}", e)
        ArRuntimeState.Unsupported("Failed to initialize AR runtime")
    }
}

private fun isKnownUnstableArCoreDevice(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()

    // Guard for Samsung A56 Android 16 builds currently crashing in ARCore sensor init.
    if (manufacturer == "samsung" && (fingerprint.contains("/a56x:") || model.contains("a56"))) {
        return true
    }

    return false
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun isProbablyEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()
    val brand = Build.BRAND.lowercase()
    val product = Build.PRODUCT.lowercase()

    return fingerprint.startsWith("generic") ||
        fingerprint.contains("emulator") ||
        model.contains("sdk") ||
        manufacturer.contains("genymotion") ||
        brand.startsWith("generic") ||
        product.contains("sdk")
}

