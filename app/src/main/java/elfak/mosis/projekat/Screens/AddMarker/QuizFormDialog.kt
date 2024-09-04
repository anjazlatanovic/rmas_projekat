/*package elfak.mosis.projekat.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import elfak.mosis.projekat.Utils.getUsernameFromLocal

@Composable
fun AddQuizDialog(
    onDismiss: () -> Unit,
    onSave: (/*String,*/ String, Boolean, String) -> Unit
) {
   // var title by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf(true) }
    var category by remember { mutableStateOf("") }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj kviz") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
               /* OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Naslov") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))*/
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Tekst pitanja") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("Tačan odgovor:")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = correctAnswer,
                        onClick = { correctAnswer = true }
                    )
                    Text("Da")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = !correctAnswer,
                        onClick = { correctAnswer = false }
                    )
                    Text("Ne")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Izaberite kategoriju(muzika, film, sport, gluma, ostalo)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(/*title,*/ questionText, correctAnswer, category)
                }
            ) {
                Text("Sačuvaj")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Otkaži")
            }
        }
    )
}
*/