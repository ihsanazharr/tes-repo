package com.example.opendatajabar

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.opendatajabar.ui.navigation.NavigationItem
import com.example.opendatajabar.ui.navigation.Screen
import com.example.opendatajabar.ui.screen.dataEntry.DataEntryScreen
import com.example.opendatajabar.ui.screen.dataList.DataListScreen
import com.example.opendatajabar.ui.screen.dataList.EditScreen
import com.example.opendatajabar.ui.screen.home.HomeScreen
import com.example.opendatajabar.ui.screen.profile.ProfileScreen
import com.example.opendatajabar.ui.screen.profile.EditProfileScreen
import com.example.opendatajabar.ui.theme.OpenDataJabarTheme
import com.example.opendatajabar.viewmodel.DataViewModel
import com.example.opendatajabar.viewmodel.ProfileViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DataOpenJabarApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: DataViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.DetailReward.route) {
                BottomBar(navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
                ) with slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
                )
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    if (targetScreen == Screen.Home.route) {
                        HomeScreen(navController = navController, viewModel = viewModel)
                    }
                }
                composable(Screen.DataEntry.route) {
                    if (targetScreen == Screen.DataEntry.route) DataEntryScreen(viewModel = viewModel)
                }
                composable(Screen.Edit.route,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    if (targetScreen == Screen.Edit.route) {
                        EditScreen(navController = navController, viewModel = viewModel, dataId = id)
                    }
                }
                composable(Screen.DataList.route) {
                    if (targetScreen == Screen.DataList.route) {
                        DataListScreen(navController = navController, viewModel = viewModel)
                    }
                }
                composable(Screen.Profile.route) {
                    if (targetScreen == Screen.Profile.route) {
                        ProfileScreen(navController = navController, viewModel = profileViewModel)
                    }
                }
                composable("edit_profile") {
                    if (targetScreen == "edit_profile") {
                        EditProfileScreen(navController = navController, viewModel = profileViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem("Home", Icons.Default.Home, Screen.Home),
            NavigationItem("Entry Data", Icons.Default.Add, Screen.DataEntry),
//            NavigationItem("List", Icons.Default.List, Screen.DataList),
            NavigationItem("Profile", Icons.Default.AccountCircle, Screen.Profile),
        )

        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { if (isSelected) Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OpenDataJabarPreview() {
    OpenDataJabarTheme {
        DataOpenJabarApp(viewModel = viewModel())
    }
}