package elfak.mosis.projekat.Screens.Profil


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
//import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.projekat.Route
import elfak.mosis.projekat.Screens.Components.NavBar
import elfak.mosis.projekat.Screens.Location.LocationNotification.LocationService
/*
@Composable
fun ProfileScreen(username: String, navController: NavController) {
    val firestore = Firebase.firestore
    var userData by remember { mutableStateOf<UserProfile?>(null) }
    var userEvents by remember { mutableStateOf<List<UserEvent>>(emptyList()) }
    var showEvents by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var rankedUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var showRanking by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance() // Inicijalizuj FirebaseAuth
    var showLogoutDialog by remember { mutableStateOf(false) }

    var showImageDialog by remember { mutableStateOf(false) }

    var isServiceRunning by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val data = document.data
                    userData = UserProfile(
                        username = username,
                        firstName = data["firstName"] as? String ?: "",
                        lastName = data["lastName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        phoneNumber = data["phoneNumber"] as? String ?: "",
                        points = (data["points"] as? Long)?.toInt() ?: 0, // Učitavanje bodova
                        profileImageUrl = data["profileImageUrl"] as? String // Učitavanje URL slike
                    )
                } else {
                    Toast.makeText(
                        navController.context,
                        "User not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    navController.context,
                    "Error fetching user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }
    }

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
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    navController.context,
                    "Error fetching events: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Potvrdi odjavu") },
            text = { Text("Da li ste sigurni da želite da se odjavite?") },
            confirmButton = {
                Button(onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text("Ne")
                }
            }
        )
    }

    if (showImageDialog && userData?.profileImageUrl != null) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            buttons = {},
            text = {
                Image(
                    painter = rememberImagePainter(data = userData!!.profileImageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
    horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top App Bar
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Moj profil",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // User details
        if (isLoading) {
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            userData?.let {
                it.profileImageUrl?.let { imageUrl ->
                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(Color(0xFFFEFEFE), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberImagePainter(data = imageUrl),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color(0xFFFEFEFE), CircleShape)  // Deblje crvene ivice oko slike
                                .background(Color.Gray)
                                .clickable {
                                    showImageDialog = true
                                }, // Klik za prikaz slike preko celog ekrana
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                    Row {
                        Text(
                            text = "Username: ",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.username,
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Ime: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.firstName,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Prezime: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.lastName,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Email: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.email,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Broj telefona: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.phoneNumber,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Bodovi: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.points.toString(),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }

            Spacer(modifier = Modifier.height(10.dp))

            // Start and Stop buttons for LocationService
            Button(onClick = {
                Intent(navController.context, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    navController.context.startService(this)
                }
                isServiceRunning = false // Ažuriraj stanje kada zaustaviš servis
            }) {
                Text("Start Location Service")
            }
            //Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                Intent(navController.context, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                    navController.context.startService(this)
                }
                isServiceRunning = true // Ažuriraj stanje kada pokreneš servis
            }) {
                Text("Stop Location Service")
            }


            //Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = {
                navController.navigate("${Route.EventsAndQuizzesScreen().name}/${username}")
            }) {
                Text("Prikaži događaje i kvizove")
            }

            //Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = {
                navController.navigate(Route.LocationScreen().name)
            }) {
                Text("Podeli svoju lokaciju")
            }

            //Spacer(modifier = Modifier.height(4.dp))
/*
            Button(onClick = {
                navController.navigate(Route.RankingScreen().name)
            }) {
                Text("Prikaži rangiranje korisnika")
            }*/

            //Spacer(modifier = Modifier.height(4.dp))

          /*  Button(onClick = {
                navController.navigate(Route.AllMarkerScreen().name)
            }) {
                Text("Prikaži sve događaje i kvizove")
            }*/

            Button(onClick = {
                navController.navigate("${Route.EditProfileScreen().name}/${username}")
            }) {
                Text("Izmeni podatke")
            }

            Button(onClick = {
                showLogoutDialog = true
            }) {
                Text("Odjavi se")
            }

        }
        Spacer(modifier = Modifier.weight(1f)) // Da bi NavBar bio na dnu
        NavBar(
            navController = navController,
            username=username,
            modifier = Modifier
                //.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(0.dp)
            //.height(56.dp) // Adjust height as needed
        )

    }
}
*/
@Composable
fun ProfileScreen(username: String, navController: NavController) {
    val firestore = Firebase.firestore
    var userData by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var isServiceRunning by remember { mutableStateOf(false) }


    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(username) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val data = document.data
                    userData = UserProfile(
                        username = username,
                        firstName = data["firstName"] as? String ?: "",
                        lastName = data["lastName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        phoneNumber = data["phoneNumber"] as? String ?: "",
                        points = (data["points"] as? Long)?.toInt() ?: 0,
                        profileImageUrl = data["profileImageUrl"] as? String,
                        //jobTitle = data["jobTitle"] as? String ?: "",
                        //department = data["department"] as? String ?: "",
                        //location = data["location"] as? String ?: "",
                        //manager = data["manager"] as? String ?: ""
                    )
                } else {
                    Toast.makeText(
                        navController.context,
                        "Korisnik nije pronađen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    navController.context,
                    "Greška prilikom preuzimanja podataka korisnika: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Potvrdi odjavu") },
            text = { Text("Da li ste sigurni da želite da se odjavite?") },
            confirmButton = {
                Button(onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                    modifier = Modifier
                        .width(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false },
                    modifier = Modifier
                        .width(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Ne")
                }
            }
        )
    }

    if (showImageDialog && userData?.profileImageUrl != null) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            buttons = {},
            text = {
                Image(
                    painter = rememberImagePainter(data = userData!!.profileImageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            Text(
                text = "Učitavanje...",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            userData?.let {
                Text(
                    text = "${it.firstName} ${it.lastName}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                it.profileImageUrl?.let { imageUrl ->
                    Image(
                        painter = rememberImagePainter(data = imageUrl),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .background(Color.Gray)
                            .clickable {
                                showImageDialog = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                /*Text(
                    text = it.jobTitle,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "${it.department} - ${it.location}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )*/

                // Prikaz broja poena
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = it.points.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "BROJ POENA",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // User Details
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(text = "PODACI O KORISNIKU", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(label = "Ime", value = it.firstName,fontSize = 18.sp)
                    DetailRow(label = "Prezime", value = it.lastName,fontSize = 18.sp)
                    DetailRow(label = "Email", value = it.email,fontSize = 18.sp)
                    DetailRow(label = "Telefon", value = it.phoneNumber,fontSize = 18.sp)
                    //DetailRow(label = "Job Title", value = it.jobTitle)
                    //DetailRow(label = "Department", value = it.department)
                    //DetailRow(label = "Location", value = it.location)
                    //DetailRow(label = "Manager", value = it.manager)
                }

                //Spacer(modifier = Modifier.height(16.dp))

                // Dugmići za akcije
                Button(onClick = {
                    navController.navigate("${Route.EditProfileScreen().name}/${username}")
                },
                    modifier = Modifier
                        .width(230.dp),
                        //.padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Izmeni podatke")
                }

                //Spacer(modifier = Modifier.height(8.dp))

                //Spacer(modifier = Modifier.height(8.dp))

                // Start and Stop buttons for LocationService
                Button(onClick = {
                    Intent(navController.context, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        navController.context.startService(this)
                    }
                    isServiceRunning = false // Ažuriraj stanje kada zaustaviš servis
                },
                    modifier = Modifier
                        .width(230.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Aktiviranje Servera")
                }
                //Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = {
                    Intent(navController.context, LocationService::class.java).apply {
                        action = LocationService.ACTION_STOP
                        navController.context.startService(this)
                    }
                    isServiceRunning = true // Ažuriraj stanje kada pokreneš servis
                },
                    modifier = Modifier
                        .width(230.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Stopiranje Servera")
                }

                //Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    navController.navigate("${Route.LocationScreen().name}/$username")
                },
                    modifier = Modifier
                        .width(230.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                ) {
                    Text("Podeli svoju lokaciju")
                }

                Button(onClick = {
                    navController.navigate("${Route.EventsAndQuizzesScreen().name}/${username}")
                },
                    modifier = Modifier
                        .width(230.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Prikaži događaje i kvizove")
                }

                Button(onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .width(230.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, // Postavlja pozadinu dugmeta na belu
                        contentColor = Color.Black // Postavlja boju teksta na crnu
                    ),
                    border = BorderStroke(2.dp, Color(0xFF81D4FA)), // Plavi okvir sa zadatom nijansom i debljinom 2dp
                )  {
                    Text("Odjavi se")
                }
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
/*
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$label:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
    }
}*/

@Composable
fun DetailRow(label: String, value: String, fontSize: TextUnit = 16.sp) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = fontSize) // Dodavanje fontSize kroz TextStyle
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = TextStyle(fontSize = fontSize) // Dodavanje fontSize kroz TextStyle
        )
    }
}




// Data class for user profile
data class UserProfile(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val points: Int,
    val profileImageUrl: String?
)

// Data class for user events
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


