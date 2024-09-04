/*package elfak.mosis.projekat.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddObjectChoiceDialog(onDismiss: () -> Unit, onEventSelected: () -> Unit, onQuizSelected: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Izaberite tip objekta") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onEventSelected) {
                    Text("Dodaj događaj")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onQuizSelected) {
                    Text("Dodaj kviz")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Otkaži")
            }
        }
    )
}
*/