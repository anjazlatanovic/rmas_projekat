package elfak.mosis.projekat.Screens.Profil

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun EditProfileScreen(username: String, navController: NavController) {
    val firestore = Firebase.firestore
    val storage = Firebase.storage

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    LaunchedEffect(username) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                val document = documents.firstOrNull()
                if (document != null) {
                    firstName = document.getString("firstName") ?: ""
                    lastName = document.getString("lastName") ?: ""
                    phoneNumber = document.getString("phoneNumber") ?: ""
                    profileImageUrl = document.getString("profileImageUrl")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

Column(horizontalAlignment = Alignment.CenterHorizontally,
    //verticalArrangement = Arrangement.Center
) {
    Spacer(modifier = Modifier.height(80.dp))
    Text(text = "IZMENI PROFIL", fontSize = 20.sp, fontWeight = FontWeight.Bold)

    Spacer(modifier = Modifier.height(26.dp))

    profileImageUrl?.let {
        Image(
            painter = rememberImagePainter(data = selectedImageUri ?: it),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentScale = ContentScale.Crop
        )
    }
/*}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {*/
        Spacer(modifier = Modifier.height(36.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Ime") },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Black, // Plava boja kada je fokusiran
                //unfocusedIndicatorColor = Color(0xFF81D4FA), // Plava boja kada nije fokusiran
                focusedLabelColor = Color.Black, // Plava boja za labelu kada je fokusiran
                //unfocusedLabelColor = Color(0xFF81D4FA), // Plava boja za labelu kada nije fokusiran
                textColor = Color.Black // Boja teksta unutar TextField
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Prezime") },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Black, // Plava boja kada je fokusiran
                //unfocusedIndicatorColor = Color(0xFF81D4FA), // Plava boja kada nije fokusiran
                focusedLabelColor = Color.Black, // Plava boja za labelu kada je fokusiran
                //unfocusedLabelColor = Color(0xFF81D4FA), // Plava boja za labelu kada nije fokusiran
                textColor = Color.Black // Boja teksta unutar TextField
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Broj telefona") },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Black, // Plava boja kada je fokusiran
                //unfocusedIndicatorColor = Color(0xFF81D4FA), // Plava boja kada nije fokusiran
                focusedLabelColor = Color.Black, // Plava boja za labelu kada je fokusiran
                //unfocusedLabelColor = Color(0xFF81D4FA), // Plava boja za labelu kada nije fokusiran
                textColor = Color.Black // Boja teksta unutar TextField
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val userRef = firestore.collection("users").document(username)
            val updatedData = hashMapOf<String, Any>(
                "firstName" to firstName,
                "lastName" to lastName,
                "phoneNumber" to phoneNumber
            )

            if (selectedImageUri != null) {
                val imageRef = storage.reference.child("profile_images/${username}.jpg")
                val uploadTask = imageRef.putFile(selectedImageUri!!)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Failed to upload image")
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        updatedData["profileImageUrl"] = downloadUri.toString()
                        userRef.update(updatedData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Podaci uspešno ažurirani", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Greška pri ažuriranju podataka: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            } else {
                userRef.update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Podaci uspešno ažurirani", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Greška pri ažuriranju podataka: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        },
            modifier = Modifier
                .width(150.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                contentColor = Color.Black // Postavlja boju teksta na crnu
            ),
            border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
        )  {
            Text("Sačuvaj izmene")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigateUp() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFB8B8B8), // Boja pozadine dugmeta
                contentColor = Color.White // Boja teksta dugmeta
            ),
            modifier = Modifier
                //.fillMaxWidth()
                .padding(end = 16.dp) // Razmak od desne ivice
                .align(Alignment.End) // Poravnanje prema centru horizontale
        ) {
            Text("Nazad")
        }
    }
}
