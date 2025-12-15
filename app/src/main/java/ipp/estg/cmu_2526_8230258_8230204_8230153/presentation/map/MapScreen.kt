package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.net.toUri

/**
 * Ecrã do Mapa, responsável pela gestão de permissões e renderização do mapa.
 *
 * @param state O estado da UI do mapa ([MapUiState]).
 * @param onEvent Callback para enviar eventos de localização para o [MapViewModel].
 */
@Composable
fun MapScreen(
    state: MapUiState,
    onEvent: (MapEvent) -> Unit
) {
    val ctx = LocalContext.current

    var hasPermissions by remember {
        mutableStateOf(
            (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsMap ->
            hasPermissions = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    && permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    LaunchedEffect(key1 = "Permission") {
        if (!hasPermissions) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermissions) {
            MapContent(state = state, onEvent = onEvent)
        } else {
            Text(
                text = "Precisamos da tua permissão de localização para mostrar o mapa.",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Conteúdo principal do mapa, incluindo a gestão da localização do utilizador
 * e a renderização dos marcadores de locais de comida.
 *
 * Requer permissão de localização (gerida por [MapScreen]).
 *
 * @param state O estado atual da UI do mapa.
 * @param onEvent Callback para o ViewModel.
 */
@SuppressLint("MissingPermission")
@Composable
private fun MapContent(
    state: MapUiState,
    onEvent: (MapEvent) -> Unit
) {
    val ctx = LocalContext.current

    var initialLocationSet by remember { mutableStateOf(false) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.1496, -8.6109), 12f) // Posição Default: Porto
    }

    LaunchedEffect(key1 = userLocation) {
        val currentLocation = userLocation
        if (currentLocation != null) {
            if (!initialLocationSet) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 14f)
            }
        }
    }

    DisposableEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000 // Atualiza a cada 10 segundos
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLocation = latLng

                    if (!initialLocationSet) {
                        onEvent(MapEvent.OnLocationUpdate(latLng))
                        initialLocationSet = true
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            state.foodMarkers.forEach { place ->
                val markerState = remember { MarkerState (position = place.location) }
                val context = LocalContext.current

                MarkerInfoWindow (
                    state = markerState,
                    title = place.name,
                    onClick = {
                        markerState.showInfoWindow()
                        false
                    },
                    onInfoWindowClick = {
                        if (place.contact.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:${place.contact}".toUri()
                            }
                            context.startActivity(intent)
                        }
                    }
                ) {
                        marker ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = androidx.compose.ui.graphics.Color.White,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = place.name,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.Black
                                )
                            )

                            Text(
                                text = place.description,
                                color = androidx.compose.ui.graphics.Color.Gray
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (place.contact.isNotBlank()) {
                                Text(
                                    text = "Contacto: ${place.contact}",
                                    color = androidx.compose.ui.graphics.Color.Blue,
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}