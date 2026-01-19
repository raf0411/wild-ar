package android.app.faunadex.presentation.components

import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenDark
import android.app.faunadex.ui.theme.PrimaryGreenLight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun FaunaBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Outlined.Home,
            route = "dashboard"
        ),
        BottomNavItem(
            label = "Quiz",
            icon = Icons.Outlined.Quiz,
            route = "quiz"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Outlined.AccountCircle,
            route = "profile"
        )
    )

    Surface(
        modifier = modifier,
        color = DarkGreen
    ) {
        NavigationBar(
            modifier = Modifier.padding(vertical = 4.dp),
            containerColor = DarkGreen,
            contentColor = Color.White,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontFamily = JerseyFont,
                            fontSize = 12.sp
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreenLight,
                        selectedTextColor = PrimaryGreenLight,
                        unselectedIconColor = MediumGreenDark,
                        unselectedTextColor = MediumGreenDark,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FaunaBottomBarPreview() {
    FaunaDexTheme {
        FaunaBottomBar(
            currentRoute = "home",
            onNavigate = {}
        )
    }
}