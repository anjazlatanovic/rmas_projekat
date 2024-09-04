package elfak.mosis.projekat.Screens.EventsAndQuizzes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.projekat.Screens.Components.NavBar
import elfak.mosis.projekat.Screens.Profil.UserEvent
/*
@Composable
fun EventsAndQuizzesScreen(username: String, navController: NavController) {
    val firestore = Firebase.firestore
    var userEvents by remember { mutableStateOf<List<UserEvent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var eventsList by remember { mutableStateOf<List<UserEvent>>(emptyList()) }
    var quizzesList by remember { mutableStateOf<List<UserEvent>>(emptyList()) }

    LaunchedEffect(username) {
        firestore.collection("objects")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                userEvents = documents.map { document ->
                    UserEvent(
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        date = document.getString("date") ?: "",
                        time = document.getString("time") ?: "",
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        isQuiz = document.getBoolean("isQuiz") ?: false,
                        rating = document.getDouble("rating") ?: 0.0,
                        comments = document.get("comments") as? List<String> ?: emptyList()
                    )
                }

                // Razdvajanje događaja i kvizova
                eventsList = userEvents.filter { !it.isQuiz }
                quizzesList = userEvents.filter { it.isQuiz }

                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    navController.context,
                    "Error fetching events: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
            text = "Događaji i kvizovi",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            // Prikaz događaja
            Text(
                text = "Događaji",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                   // .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))


            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
                .background(Color.LightGray)) {
                Text(text = "Naslov", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(text = "Datum", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
                Text(text = "Vreme", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
                Text(text = "Ocena", modifier = Modifier.width(70.dp), fontWeight = FontWeight.Bold)
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(eventsList) { index, event ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(if (index % 2 == 0) Color.White else Color(0xFFF0F0F0))) {
                        Text(text = event.title, modifier = Modifier.weight(1f))
                        Text(text = event.date, modifier = Modifier.width(100.dp))
                        Text(text = event.time, modifier = Modifier.width(100.dp))
                        Text(text = "${event.rating}", modifier = Modifier.width(70.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prikaz kvizova
            Text(
                text = "Kvizovi",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    //.background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))


            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
                .background(Color.LightGray)) {
                Text(text = "Naslov", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(text = "Opis", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                Text(text = "Ocena", modifier = Modifier.width(70.dp), fontWeight = FontWeight.Bold)
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(quizzesList) { index, quiz ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(if (index % 2 == 0) Color.White else Color(0xFFF0F0F0))) {
                        Text(text = quiz.title, modifier = Modifier.weight(1f))
                        Text(text = quiz.description, modifier = Modifier.weight(2f))
                        //Text(text = quiz.date, modifier = Modifier.width(100.dp))
                        //Text(text = quiz.time, modifier = Modifier.width(100.dp))
                        Text(text = "${quiz.rating}", modifier = Modifier.width(70.dp))
                    }
                }
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
}
*/


@Composable
fun EventsAndQuizzesScreen(username: String, navController: NavController) {
    val firestore = Firebase.firestore
    var userEvents by remember { mutableStateOf<List<UserEvent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var eventsList by remember { mutableStateOf<List<UserEvent>>(emptyList()) }
    var quizzesList by remember { mutableStateOf<List<UserEvent>>(emptyList()) }

    LaunchedEffect(username) {
        firestore.collection("objects")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                userEvents = documents.map { document ->
                    UserEvent(
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        date = document.getString("date") ?: "",
                        time = document.getString("time") ?: "",
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        isQuiz = document.getBoolean("isQuiz") ?: false,
                        rating = document.getDouble("rating") ?: 0.0,
                        comments = document.get("comments") as? List<String> ?: emptyList()
                    )
                }

                // Razdvajanje događaja i kvizova
                eventsList = userEvents.filter { !it.isQuiz }
                quizzesList = userEvents.filter { it.isQuiz }

                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    navController.context,
                    "Error fetching events: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
            text = "Vasi događaji",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .border(4.dp, Color(0xFFE3F2FD )) // Dodavanje ivice u plavoj boji
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text(
                text = "Učitavanje...",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            // Prikaz događaja
            Text(
                text = "Događaji",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB8B8B8)) // Koristi boju iz AllMarkerScreen
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
                    text = "Datum",
                    modifier = Modifier.width(100.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Vreme",
                    modifier = Modifier.width(100.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ocena",
                    modifier = Modifier.width(70.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(eventsList) { index, event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // Koristi visinu iz AllMarkerScreen
                            .background(if (index % 2 == 0) Color.White else Color(0xFFE3F2FD)) // Alternativna boja iz AllMarkerScreen
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
                            text = event.date,
                            modifier = Modifier.width(100.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = event.time,
                            modifier = Modifier.width(100.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${event.rating}",
                            modifier = Modifier.width(70.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prikaz kvizova
            /*Text(
                text = "Kvizovi",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB8B8B8)) // Koristi boju iz AllMarkerScreen
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
                    text = "Opis",
                    modifier = Modifier.weight(2f).padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ocena",
                    modifier = Modifier.width(70.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }*/
/*
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(quizzesList) { index, quiz ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // Koristi visinu iz AllMarkerScreen
                            .background(if (index % 2 == 0) Color.White else Color(1)) // Alternativna boja iz AllMarkerScreen
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = quiz.title,
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = quiz.description,
                            modifier = Modifier.weight(2f).padding(start = 8.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${quiz.rating}",
                            modifier = Modifier.width(70.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }*/

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFB8B8B8), // Boja pozadine dugmeta
                    contentColor = Color.White // Boja teksta dugmeta
                ),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End)
            ) {
                Text("Nazad")
            }
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




data class UserEvent(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val isQuiz: Boolean,
    val rating: Double,
    val comments: List<String>
)
