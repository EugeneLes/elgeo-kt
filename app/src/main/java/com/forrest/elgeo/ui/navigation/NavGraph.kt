package com.forrest.elgeo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.forrest.elgeo.ui.screen.dashboard.DashboardScreen
import com.forrest.elgeo.ui.screen.history.HistoryScreen
import com.forrest.elgeo.ui.screen.history.TripDetailScreen
import com.forrest.elgeo.ui.screen.map.MapScreen
import com.forrest.elgeo.ui.screen.settings.SettingsScreen

@Composable
fun ElGeoNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onTripClick = { tripId ->
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId") ?: return@composable
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
