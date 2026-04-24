# ElGeo — GPS Dashboard for Android

A real-time GPS dashboard app built with Kotlin and Jetpack Compose. Displays live speed, altitude, coordinates, and offline maps with full trip tracking.

## Features

- **Live Speedometer** — Animated gauge with current, max, and average speed (km/h or mph)
- **Altitude Tracking** — Real-time altitude with rising/falling/stable trend indicator
- **GPS Coordinates** — Live lat/lng display with tap-to-copy and accuracy meter
- **Offline Maps** — OSMDroid-powered map with tile caching and region download
- **Trip Recording** — Start/Pause/Stop trips with distance, duration, and route polyline
- **Trip History** — Browse, view details, and delete saved trips (Room database)
- **Export** — Share trips as GPX or CSV files
- **Background Tracking** — Foreground service continues tracking with screen locked
- **Settings** — Speed/altitude units, dark mode, keep screen on, GPS update frequency
- **Material 3 Dark Theme** — Car dashboard-style UI with large, readable text

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository pattern |
| DI | Hilt |
| Database | Room |
| Settings | DataStore Preferences |
| Location | FusedLocationProviderClient (Google Play Services) |
| Maps | OSMDroid (OpenStreetMap) |
| Async | Coroutines + Flow |
| Navigation | Navigation Compose |

## Project Structure

```
app/src/main/java/com/forrest/elgeo/
├── ElGeoApp.kt                  # Application class (Hilt, notification channel, OSMDroid config)
├── MainActivity.kt              # Entry point, permissions, bottom navigation
├── data/
│   ├── local/
│   │   ├── dao/                 # Room DAOs (TripDao, LocationPointDao)
│   │   ├── database/            # ElGeoDatabase
│   │   ├── datastore/           # SettingsDataStore (DataStore Preferences)
│   │   └── entity/              # Room entities (TripEntity, LocationPointEntity)
│   └── repository/              # LocationRepository, TripRepository, SettingsRepository
├── di/                          # Hilt modules (AppModule, DatabaseModule)
├── domain/model/                # Domain models (GpsData, AppSettings, TripState)
├── services/                    # LocationService (foreground service)
├── ui/
│   ├── navigation/              # NavGraph, Screen definitions
│   ├── screen/
│   │   ├── dashboard/           # Speedometer gauge, live stats, trip controls
│   │   ├── history/             # Trip list, trip detail with route map
│   │   ├── map/                 # Full map screen with offline download
│   │   └── settings/            # Units, display, GPS frequency options
│   └── theme/                   # Material 3 dark/light theme, colors, typography
└── utils/                       # GpsUtils (formatting), ExportUtils (GPX/CSV)
```

## Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17+**
- **Android SDK 36** (install via SDK Manager)
- A physical Android device or emulator with Google Play Services

## Setup

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd elgeo-kt
   ```

2. **Open in Android Studio**
   - File → Open → select the `elgeo-kt` directory
   - Wait for Gradle sync to complete (first sync downloads all dependencies)

3. **Verify SDK**
   - In Android Studio: Tools → SDK Manager
   - Ensure **Android API 36** SDK Platform is installed
   - Ensure **Google Play Services** is installed under SDK Tools

4. **Run on device**
   - Connect a physical Android device with USB debugging enabled
   - Select the device in the toolbar and click **Run** (▶)
   - Grant location permissions when prompted

> **Note:** GPS features require a physical device. The emulator can simulate locations via Extended Controls → Location, but real GPS hardware provides much better results.

## Building an APK

### Debug APK (for testing)

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (for distribution)

1. **Create a keystore** (one-time):
   ```bash
   keytool -genkey -v -keystore elgeo-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias elgeo
   ```

2. **Configure signing in `app/build.gradle.kts`** — add inside `android { }`:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("../elgeo-release.jks")
           storePassword = "your_store_password"
           keyAlias = "elgeo"
           keyPassword = "your_key_password"
       }
   }
   buildTypes {
       release {
           signingConfig = signingConfigs.getByName("release")
           // ... existing config
       }
   }
   ```

3. **Build the release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

### AAB for Google Play

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Permissions

| Permission | Purpose |
|-----------|---------|
| `ACCESS_FINE_LOCATION` | GPS speed, altitude, coordinates |
| `ACCESS_COARSE_LOCATION` | Fallback location |
| `ACCESS_BACKGROUND_LOCATION` | Continue tracking with screen off |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_LOCATION` | Background service for GPS |
| `POST_NOTIFICATIONS` | Service notification (Android 13+) |
| `INTERNET` | Map tile downloads |
| `WAKE_LOCK` | Prevent CPU sleep during tracking |

## Offline Maps

The app uses OpenStreetMap tiles via OSMDroid. To use maps offline:

1. Navigate to the map area you want to save
2. Tap the **download button** (↓) on the map screen
3. The current visible region (+ 3 zoom levels) will be cached locally
4. Cached tiles work without an internet connection

Downloaded tiles are stored in the app's cache directory and persist across sessions.

## Battery Optimization

- GPS update frequency is configurable (0.5s → 5s)
- The foreground service uses `PRIORITY_HIGH_ACCURACY` for best GPS results
- Lower update frequencies (2s, 5s) significantly reduce battery consumption
- The service only runs when the app is active or a trip is recording

## License

MIT
