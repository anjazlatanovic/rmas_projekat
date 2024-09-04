package elfak.mosis.projekat.Screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.projekat.Utils.getUsernameFromLocal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Uri?, quizTitle: String?, quizQuestion: String?, quizCorrectAnswer: Boolean?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var time by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }



    var quizTitle by remember { mutableStateOf("") }
    var quizQuestion by remember { mutableStateOf("") }
    var quizCorrectAnswer by remember { mutableStateOf(true) }
    var addQuiz by remember { mutableStateOf(false) }




    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj događaj") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Naslov") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Datum (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Vreme (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Izaberite kategoriju(muzika, film, sport, gluma, ostalo)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Odaberite sliku")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (imageUri != null) {
                    Text("Slika odabrana")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Dodaj kviz?", modifier = Modifier.clickable { addQuiz = !addQuiz })
                if (addQuiz) {
                    Spacer(modifier = Modifier.height(8.dp))
                    /*OutlinedTextField(
                        value = quizTitle,
                        onValueChange = { quizTitle = it },
                        label = { Text("Naslov kviza") },
                        modifier = Modifier.fillMaxWidth()
                    )*/
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quizQuestion,
                        onValueChange = { quizQuestion = it },
                        label = { Text("Tekst pitanja") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Tačan odgovor:")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = quizCorrectAnswer,
                            onClick = { quizCorrectAnswer = true }
                        )
                        Text("Da")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            /*selected = !quizCorrectAnswer,
                            onClick = { quizCorrectAnswer = false }*/
                            selected = !quizCorrectAnswer,
                            onClick = { quizCorrectAnswer = true }
                        )
                        Text("Ne")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, description, date, time, category, imageUri,      if (addQuiz) quizTitle else null, if (addQuiz) quizQuestion else null, if (addQuiz) quizCorrectAnswer else null)
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


