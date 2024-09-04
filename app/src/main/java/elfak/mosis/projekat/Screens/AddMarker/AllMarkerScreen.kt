package elfak.mosis.projekat.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.projekat.Screens.Components.NavBar

@Composable
fun AllMarkerScreen(navController: NavController, username: String) {
    val firestore = FirebaseFirestore.getInstance()
    var allEvents by remember { mutableStateOf<List<FirestoreUtils.UserEvent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        firestore.collection("objects")
            .get()
            .addOnSuccessListener { snapshot ->
                allEvents = snapshot.documents.map { document ->
                    FirestoreUtils.UserEvent(
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        date = document.getString("date") ?: "",
                        time = document.getString("time") ?: "",
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        isQuiz = document.getBoolean("isQuiz") ?: false,
                        rating = document.getDouble("rating") ?: 0.0,
                        comments = document.get("comments") as? List<String> ?: emptyList(),
                        username = document.getString("username") ?: "",
                        imageUri = document.getString("imageUrl")
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(navController.context, "Error fetching events: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Svi događaji",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Text(text = "Učitavanje...", fontSize = 18.sp, color = Color.Gray)
        } else {
            // Prikaz zaglavlja tabele
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB8B8B8)) // Plava boja
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Naslov",
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ocena",
                    modifier = Modifier.weight(0.5f).padding(start = 0.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Korisnik",
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Prikaz događaja u tabeli
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(allEvents) { index, event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)  // Fiksna visina reda
                            .background(if (index % 2 == 0) Color.White else Color(0xFFE3F2FD)) // Plavi tonovi
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.title,
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${event.rating}",
                            modifier = Modifier.weight(0.5f).padding(start = 8.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = event.username,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material.Button(
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
            androidx.compose.material.Text("Nazad")
        }
        Spacer(modifier = Modifier.weight(1f)) // Da bi NavBar bio na dnu
        NavBar(
            navController = navController,
            username = username,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        )
    }
}


@Composable
fun EventItem(event: FirestoreUtils.UserEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            //.background(Color.LightGray)
            .padding(8.dp)
    ) {
        Text(text = event.title, modifier = Modifier.weight(1f), color = Black)
        Text(text = "${event.rating}", modifier = Modifier.width(80.dp), color = Black)
        Text(text = event.username, modifier = Modifier.weight(1f), color = Black)
        //Text(text = event.description, modifier = Modifier.weight(2f), color = Black)
    }
}