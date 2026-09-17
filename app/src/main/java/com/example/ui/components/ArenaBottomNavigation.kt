package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

data class NavItem(
    val id: String,
    val labelKey: (com.example.ui.localization.AppStrings) -> String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun ArenaBottomNavigation(
    currentDestination: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    val items = listOf(
        NavItem("home", { it.navHome }, Icons.Filled.Home, Icons.Outlined.Home, "nav_home_button"),
        NavItem("challenges", { it.navChallenges }, Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports, "nav_challenges_button"),
        NavItem("arena", { it.navArena }, Icons.Filled.Bolt, Icons.Outlined.Bolt, "nav_arena_button"),
        NavItem("leaderboard", { it.navLeaderboard }, Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, "nav_leaderboard_button"),
        NavItem("rewards", { it.navRewards }, Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard, "nav_rewards_button"),
        NavItem("profile", { it.navProfile }, Icons.Filled.Person, Icons.Outlined.Person, "nav_profile_button")
    )

    NavigationBar(
        containerColor = palette.surface,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 8.dp,
        modifier = modifier.height(68.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentDestination == item.id
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.id) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.labelKey(strings),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.labelKey(strings),
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = palette.onPrimaryContainer,
                    selectedTextColor = palette.primary,
                    indicatorColor = palette.primaryContainer,
                    unselectedIconColor = palette.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = palette.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
