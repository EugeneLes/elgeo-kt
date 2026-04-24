package com.forrest.elgeo.ui.screen.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.forrest.elgeo.domain.model.AltitudeTrend
import com.forrest.elgeo.domain.model.SpeedUnit
import com.forrest.elgeo.ui.theme.DashboardAmber
import com.forrest.elgeo.ui.theme.DashboardGreen
import com.forrest.elgeo.ui.theme.DashboardRed
import com.forrest.elgeo.utils.GpsUtils

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val gps by viewModel.gpsData.collectAsState()
    val trip by viewModel.tripState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    val displaySpeed = GpsUtils.convertSpeed(gps.speed, settings.speedUnit)
    val unitLabel = GpsUtils.speedUnitLabel(settings.speedUnit)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeedometerGauge(
            speed = displaySpeed,
            maxSpeed = if (settings.speedUnit == SpeedUnit.KMH) 240.0 else 150.0,
            unit = unitLabel,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                label = "MAX",
                value = GpsUtils.formatSpeed(
                    if (trip.isActive) trip.maxSpeed else 0f,
                    settings.speedUnit
                ),
                unit = unitLabel
            )
            StatCard(
                label = "AVG",
                value = GpsUtils.formatSpeed(
                    if (trip.isActive) trip.avgSpeed else 0f,
                    settings.speedUnit
                ),
                unit = unitLabel
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Altitude",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${GpsUtils.formatAltitude(gps.altitude, settings.altitudeUnit)} ${GpsUtils.altitudeUnitLabel(settings.altitudeUnit)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = when (gps.altitudeTrend) {
                            AltitudeTrend.RISING -> Icons.AutoMirrored.Filled.TrendingUp
                            AltitudeTrend.FALLING -> Icons.AutoMirrored.Filled.TrendingDown
                            AltitudeTrend.STABLE -> Icons.AutoMirrored.Filled.TrendingFlat
                        },
                        contentDescription = gps.altitudeTrend.name,
                        tint = when (gps.altitudeTrend) {
                            AltitudeTrend.RISING -> DashboardGreen
                            AltitudeTrend.FALLING -> DashboardRed
                            AltitudeTrend.STABLE -> DashboardAmber
                        },
                        modifier = Modifier.size(32.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Coordinates",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${GpsUtils.formatCoordinate(gps.latitude)}, ${GpsUtils.formatCoordinate(gps.longitude)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable {
                                val clip = ClipData.newPlainText(
                                    "coordinates",
                                    "${gps.latitude}, ${gps.longitude}"
                                )
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Coordinates copied", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Accuracy",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "\u00B1${gps.accuracy.toInt()} m",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                gps.accuracy < 10 -> DashboardGreen
                                gps.accuracy < 30 -> DashboardAmber
                                else -> DashboardRed
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (trip.isActive) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (trip.isPaused) "Trip Paused" else "Trip Recording",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (trip.isPaused) DashboardAmber else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                GpsUtils.formatDistance(trip.distance),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Distance",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                GpsUtils.formatDuration(trip.duration),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Duration",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${trip.pointCount}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Points",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TripControls(
            isActive = trip.isActive,
            isPaused = trip.isPaused,
            onStart = viewModel::startTrip,
            onPause = viewModel::pauseTrip,
            onResume = viewModel::resumeTrip,
            onStop = viewModel::stopTrip
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    unit: String
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TripControls(
    isActive: Boolean,
    isPaused: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isActive) {
            FilledIconButton(
                onClick = onStart,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DashboardGreen
                )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Start Trip",
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            FilledIconButton(
                onClick = if (isPaused) onResume else onPause,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DashboardAmber
                )
            ) {
                Icon(
                    if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            FilledIconButton(
                onClick = onStop,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DashboardRed
                )
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = "Stop Trip",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
