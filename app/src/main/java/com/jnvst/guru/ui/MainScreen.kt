package com.jnvst.guru.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jnvst.guru.R
import com.jnvst.guru.ui.navigation.JnvstNavGraph
import com.jnvst.guru.ui.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            // Top-level destinations that show the bottom bar
            val topLevelRoutes = listOf(
                Screen.Home.route,
                Screen.Practice.route,
                Screen.Tests.route,
                Screen.Progress.route,
                Screen.Profile.route
            )
            
            val showBottomBar = currentDestination?.route in topLevelRoutes

            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    data class NavItem(val label: String, val icon: ImageVector, val route: String)
                    val items = listOf(
                        NavItem(stringResource(R.string.nav_home), Icons.Default.Home, Screen.Home.route),
                        NavItem(stringResource(R.string.nav_practice), Icons.Default.EditNote, Screen.Practice.route),
                        NavItem(stringResource(R.string.nav_tests), Icons.AutoMirrored.Filled.Assignment, Screen.Tests.route),
                        NavItem(stringResource(R.string.nav_progress), Icons.Default.BarChart, Screen.Progress.route),
                        NavItem(stringResource(R.string.nav_profile), Icons.Default.PersonOutline, Screen.Profile.route)
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        JnvstNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
