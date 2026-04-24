package com.forrest.elgeo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.forrest.elgeo.data.local.entity.LocationPointEntity
import com.forrest.elgeo.data.local.entity.TripEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ExportUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun exportGpx(context: Context, trip: TripEntity, points: List<LocationPointEntity>): File {
        val file = File(context.cacheDir, "elgeo_trip_${trip.id}.gpx")
        file.writeText(buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<gpx version="1.1" creator="ElGeo" xmlns="http://www.topografix.com/GPX/1/1">""")
            appendLine("  <metadata>")
            appendLine("    <name>${escapeXml(trip.name)}</name>")
            appendLine("    <time>${isoFormat.format(Date(trip.startTime))}</time>")
            appendLine("  </metadata>")
            appendLine("  <trk>")
            appendLine("    <name>${escapeXml(trip.name)}</name>")
            appendLine("    <trkseg>")
            for (p in points) {
                appendLine("""      <trkpt lat="${p.latitude}" lon="${p.longitude}">""")
                appendLine("        <ele>${p.altitude}</ele>")
                appendLine("        <time>${isoFormat.format(Date(p.timestamp))}</time>")
                appendLine("        <speed>${p.speed}</speed>")
                appendLine("      </trkpt>")
            }
            appendLine("    </trkseg>")
            appendLine("  </trk>")
            appendLine("</gpx>")
        })
        return file
    }

    fun exportCsv(context: Context, trip: TripEntity, points: List<LocationPointEntity>): File {
        val file = File(context.cacheDir, "elgeo_trip_${trip.id}.csv")
        file.writeText(buildString {
            appendLine("timestamp,latitude,longitude,altitude,speed,accuracy,bearing")
            for (p in points) {
                appendLine("${isoFormat.format(Date(p.timestamp))},${p.latitude},${p.longitude},${p.altitude},${p.speed},${p.accuracy},${p.bearing}")
            }
        })
        return file
    }

    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export trip data"))
    }

    private fun escapeXml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
