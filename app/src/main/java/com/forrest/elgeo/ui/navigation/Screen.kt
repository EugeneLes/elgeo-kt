package com.forrest.elgeo.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Map : Screen("map", "Map")
    data object History : Screen("history", "History")
    data object Settings : Screen("settings", "Settings")
    data object TripDetail : Screen("trip_detail/{tripId}", "Trip Detail") {
        fun createRoute(tripId: Long) = "trip_detail/$tripId"
    }
}
