package android.app.faunadex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import android.app.faunadex.presentation.navigation.NavGraph
import android.app.faunadex.presentation.navigation.Screen
import android.app.faunadex.ui.theme.FaunaDexTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaunaDexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FaunaDexApp(
                        modifier = Modifier.padding(innerPadding),
                        isUserLoggedIn = firebaseAuth.currentUser != null
                    )
                }
            }
        }
    }
}

@Composable
fun FaunaDexApp(
    modifier: Modifier = Modifier,
    isUserLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val startDestination = if (isUserLoggedIn) {
        Screen.Dashboard.route
    } else {
        Screen.Onboarding.route
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
