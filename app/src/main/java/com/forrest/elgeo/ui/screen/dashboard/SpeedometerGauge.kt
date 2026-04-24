package com.forrest.elgeo.ui.screen.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forrest.elgeo.ui.theme.GaugeGreen
import com.forrest.elgeo.ui.theme.GaugeOrange
import com.forrest.elgeo.ui.theme.GaugeRed
import com.forrest.elgeo.ui.theme.GaugeYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGauge(
    speed: Double,
    maxSpeed: Double = 240.0,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed.toFloat(),
        animationSpec = tween(durationMillis = 300),
        label = "speed"
    )

    val sweepAngle = 240f
    val startAngle = 150f
    val fraction = (animatedSpeed / maxSpeed.toFloat()).coerceIn(0f, 1f)
    val needleAngle = startAngle + fraction * sweepAngle

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val padding = 24.dp.toPx()
            val arcSize = minOf(size.width, size.height) - padding * 2
            val topLeft = Offset(
                (size.width - arcSize) / 2,
                (size.height - arcSize) / 2
            )
            val arcRect = Size(arcSize, arcSize)
            val center = Offset(size.width / 2, size.height / 2)
            val radius = arcSize / 2

            drawArc(
                color = Color(0xFF333333),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcRect,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            val activeSweep = fraction * sweepAngle
            if (activeSweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(GaugeGreen, GaugeYellow, GaugeOrange, GaugeRed),
                        center = center
                    ),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcRect,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            drawTicks(center, radius, startAngle, sweepAngle, maxSpeed)

            val needleRad = needleAngle * PI.toFloat() / 180f
            val needleLen = radius * 0.7f
            val needleEnd = Offset(
                center.x + needleLen * cos(needleRad),
                center.y + needleLen * sin(needleRad)
            )

            drawLine(
                color = Color.White,
                start = center,
                end = needleEnd,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(color = Color.White, radius = 8.dp.toPx(), center = center)
            drawCircle(color = Color(0xFF333333), radius = 5.dp.toPx(), center = center)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 32.dp)
        ) {
            Text(
                text = "%.0f".format(animatedSpeed.toDouble()),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun DrawScope.drawTicks(
    center: Offset,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    maxSpeed: Double
) {
    val majorStep = when {
        maxSpeed <= 120 -> 20
        maxSpeed <= 200 -> 20
        else -> 40
    }
    val totalTicks = (maxSpeed / majorStep).toInt()

    for (i in 0..totalTicks) {
        val frac = i.toFloat() / totalTicks
        val angle = (startAngle + frac * sweepAngle) * PI.toFloat() / 180f
        val outerRadius = radius - 8.dp.toPx()
        val innerRadius = radius - 24.dp.toPx()

        val outer = Offset(
            center.x + outerRadius * cos(angle),
            center.y + outerRadius * sin(angle)
        )
        val inner = Offset(
            center.x + innerRadius * cos(angle),
            center.y + innerRadius * sin(angle)
        )

        drawLine(
            color = Color(0xFF888888),
            start = inner,
            end = outer,
            strokeWidth = 2.dp.toPx()
        )
    }
}
