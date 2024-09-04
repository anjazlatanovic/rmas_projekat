package elfak.mosis.projekat.Screens.Radius

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import elfak.mosis.projekat.Screens.Location.MarkerData
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState

@Composable
fun RadiusSearchSection(
    context: Context,
    currentLocation: LatLng,
    markers: List<MarkerData>,
    cameraPositionState: CameraPositionState,
    onMarkersFiltered: (List<MarkerData>) -> Unit
) {
    var showRadiusField by remember { mutableStateOf(false) }
    var radius by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        /*modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Dodajemo padding kako bismo osigurali da dugmadi ne budu previše blizu ivice ekrana*/
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showRadiusField = true }) {
            Text(text = "Unesi radijus")
        }

        if (showRadiusField) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = radius,
                onValueChange = { radius = it },
                label = { Text("Unesite radijus u metrima") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val radiusInMeters = radius.text.toDoubleOrNull()
                if (radiusInMeters != null) {
                    val filteredMarkers = markers.filter { marker ->
                        val distance = calculateDistanceInMeters(currentLocation, marker.position)
                        distance <= radiusInMeters
                    }
                    onMarkersFiltered(filteredMarkers)

                    Toast.makeText(
                        context,
                        "Prikazano je ${filteredMarkers.size} objekata u radijusu od $radiusInMeters metara.",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (filteredMarkers.isNotEmpty()) {
                        val firstMarker = filteredMarkers.first()
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            firstMarker.position, 12f
                        )
                    }
                } else {
                    Toast.makeText(context, "Unesite validan radijus.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Prikaži u radijusu")
            }
        }
    }
}

// Funkcija za izračunavanje udaljenosti između dve tačke u metrima
fun calculateDistanceInMeters(start: LatLng, end: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        start.latitude,
        start.longitude,
        end.latitude,
        end.longitude,
        results
    )
    return results[0]
}