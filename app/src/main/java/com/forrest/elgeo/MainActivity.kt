package com.forrest.elgeo

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forrest.elgeo.data.repository.SettingsRepository
import com.forrest.elgeo.domain.model.AppSettings
import com.forrest.elgeo.services.LocationService
import com.forrest.elgeo.ui.navigation.ElGeoNavGraph
import com.forrest.elgeo.ui.navigation.Screen
import com.forrest.elgeo.ui.theme.ElGeoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (fineGranted) {
            startLocationService()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private val backgroundPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Background location result — service is already running */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissions()

        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = AppSettings())

            if (settings.keepScreenOn) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            ElGeoTheme(darkTheme = settings.darkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomTabs = listOf(
                    Screen.Dashboard,
                    Screen.Map,
                    Screen.History,
                    Screen.Settings
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in bottomTabs.map { it.route }) {
                            NavigationBar {
                                bottomTabs.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = when (screen) {
                                                    Screen.Dashboard -> Icons.Default.Speed
                                                    Screen.Map -> Icons.Default.Map
                                                    Screen.History -> Icons.Default.History
                                                    Screen.Settings -> Icons.Default.Settings
                                                    else -> Icons.Default.Speed
                                                },
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = { Text(screen.title) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Dashboard.route) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    ElGeoNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        locationPermissionRequest.launch(permissions.toTypedArray())
    }

    private fun startLocationService() {
        lifecycleScope.launch {
            val settings = settingsRepository.settings.first()
            val intent = Intent(this@MainActivity, LocationService::class.java).apply {
                putExtra(LocationService.EXTRA_INTERVAL, settings.gpsUpdateIntervalMs)
            }
            startForegroundService(intent)
        }
    }
}
