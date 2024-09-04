package elfak.mosis.projekat.Screens.Location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.location.*
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import elfak.mosis.projekat.Screens.FirestoreUtils
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.projekat.Screens.AddEventDialog
//import elfak.mosis.projekat.Screens.AddObjectChoiceDialog
//import elfak.mosis.projekat.Screens.AddQuizDialog
import elfak.mosis.projekat.Utils.getUsernameFromLocal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.QuerySnapshot
import elfak.mosis.projekat.R
import elfak.mosis.projekat.Screens.Components.NavBar
import elfak.mosis.projekat.Screens.Radius.RadiusSearchSection


@SuppressLint("MissingPermission")
@Composable
fun LocationScreen(navController: NavHostController, username: String) {
    val context = LocalContext.current
    //var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var currentLocation by remember { mutableStateOf(LatLng(43.3219, 21.8954)) } // Postavite početnu lokaciju na Niš
    /*var cameraPositionState by remember {
        mutableStateOf(CameraPositionState(position = CameraPosition.fromLatLngZoom(currentLocation, 10f)))
    }*/
    var cameraPositionState by remember {
        mutableStateOf(CameraPositionState(position = CameraPosition.fromLatLngZoom(LatLng(25.0, 8.0), 2f)))// Postavite početni zoom na Niš
    }

    var showDialog by remember { mutableStateOf<String?>(null) }
    var showChoiceDialog by remember { mutableStateOf(false) }
    var markers by remember { mutableStateOf<List<MarkerData>>(emptyList()) }
    var shouldMoveCamera by remember { mutableStateOf(false) }

    val firestore = FirebaseFirestore.getInstance()

    var selectedAnswer by remember { mutableStateOf<Boolean?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var locationShared by remember { mutableStateOf(false) }


    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
   /* val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    if (shouldMoveCamera) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
                        shouldMoveCamera = false
                    }
                    //cameraPositionState = CameraPositionState(
                    //position = CameraPosition.fromLatLngZoom(currentLocation, 10f) )

                }
            }
        }
    }*/


    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    if (shouldMoveCamera) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
                        shouldMoveCamera = false
                    }

                    // Provera blizine korisnika za svaki objekat
                    markers.forEach { marker ->
                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            currentLocation.latitude, currentLocation.longitude,
                            marker.position.latitude, marker.position.longitude,
                            distance
                        )

                        if (distance[0] < 10) { // 10 metara raspon
                            val username = getUsernameFromLocal(context)
                            if (username != null) {
                                FirestoreUtils.checkAndAwardPoints(context, username, marker)
                            }
                        }
                    }
                }
            }
        }
    }


    MapsInitializer.initialize(context, MapsInitializer.Renderer.LATEST, null)

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1000L
    ).setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(3000)
        .setMaxUpdateDelayMillis(100)
        .build()

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        if (permissionsMap.values.all { it }) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Toast.makeText(context, "Permisija odobrena", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permisija odbijena", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        firestore.collection("objects")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, "Error fetching objects: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                snapshot?.let {
                    markers = it.documents.map { document ->
                        MarkerData(
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: "",
                            position = LatLng(
                                document.getDouble("latitude") ?: 0.0,
                                document.getDouble("longitude") ?: 0.0
                            ),
                            rating = document.getDouble("rating") ?: 0.0,
                            comments = document.get("comments") as? List<String> ?: emptyList(),  // Preuzimanje komentara
                            category = document.getString("category") ?: "",
                            username = document.getString("username")?:"",
                            date = document.getString("date") ?: "",
                            isQuiz = document.getBoolean("isQuiz") ?: false,
                            imageUrl = document.getString("imageUrl") // slika
                        )
                    }
                }
            }
    }

    var selectedMarker by remember { mutableStateOf<MarkerData?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var ratingValue by remember { mutableStateOf(TextFieldValue("")) }
    var commentValue by remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Vi",
                snippet = "Nalazite se ovde!",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE) // Promeni boju markera
            )

            markers.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title,
                    snippet = "${marker.description}\nOcena: ${marker.rating}",
                    onClick = {
                        selectedMarker = marker
                        showRatingDialog = true
                        true
                    }
                )
            }
        }

/*
        if (showRatingDialog && selectedMarker != null) {
            var showImageDialog by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                //title = { Text(selectedMarker?.title ?: "") },
                title = {
                    Text(
                        text = selectedMarker?.title ?: "",
                        modifier = Modifier.fillMaxWidth(), // Postavlja širinu naslova da bude puna širina
                        textAlign = TextAlign.Center // Centrira tekst unutar naslova
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        if (selectedMarker?.imageUrl != null) {
                            Image(
                                painter = rememberImagePainter(selectedMarker?.imageUrl),
                                contentDescription = "Slika objekta",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        4.dp,
                                        Color(0xFFFEFEFE),
                                        CircleShape
                                    )  // Deblje crvene ivice oko slike
                                    .clickable { showImageDialog = true },
                                contentScale = ContentScale.Crop

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                         }
                        Text("Opis: ${selectedMarker?.description}")
                        Text("Ocena: ${selectedMarker?.rating}")

                        Text("Komentari:")
                        selectedMarker?.comments?.forEach { comment ->
                            Text("- $comment")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedMarker?.isQuiz == true) {
                            // Dodajte ovo za odabir odgovora na pitanje
                            Text("Odaberite odgovor na pitanje:")
                            Row {
                                RadioButton(
                                    selected = selectedAnswer == true,
                                    onClick = { selectedAnswer = true }
                                )
                                Text("Da")
                                Spacer(modifier = Modifier.width(8.dp))
                                RadioButton(
                                    selected = selectedAnswer == false,
                                    onClick = { selectedAnswer = false }
                                )
                                Text("Ne")
                            }

                            Text("Unesite ocenu za kviz (1-5):")
                            OutlinedTextField(
                                value = ratingValue,
                                onValueChange = { ratingValue = it },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )
                        }
                        else {
                            Text("Unesite ocenu za kviz (1-5):")
                            OutlinedTextField(
                                value = ratingValue,
                                onValueChange = { ratingValue = it },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Komentar:")
                            OutlinedTextField(
                                value = commentValue,
                                onValueChange = { commentValue = it }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val newRating = ratingValue.text.toDoubleOrNull()
                        val comment = commentValue.text
                        val username = getUsernameFromLocal(context)

                        if (newRating != null && newRating in 1.0..5.0) {
                            if (username != null) {
                                FirestoreUtils.rateObject(
                                    context = context,
                                    title = selectedMarker!!.title,
                                    newRating = newRating,
                                    comment = comment,
                                    username = username,
                                    selectedAnswer = selectedAnswer // Prosleđujemo odgovor
                                )
                                Toast.makeText(context, "Uspešno ocenjen ${selectedMarker!!.title}!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Niste prijavljeni. Molimo prijavite se ponovo.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Unesite validnu ocenu između 1 i 5.", Toast.LENGTH_SHORT).show()
                        }
                        showRatingDialog = false
                        ratingValue = TextFieldValue("")
                    }) {
                        Text("Sačuvaj")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showRatingDialog = false
                        ratingValue = TextFieldValue("")
                    }) {
                        Text("Otkaži")
                    }
                }
            )
            // Uvećana slika prilikom klika
            if (showImageDialog) {
                AlertDialog(
                    onDismissRequest = { showImageDialog = false },
                    text = {
                        Image(
                            painter = rememberImagePainter(selectedMarker?.imageUrl),
                            contentDescription = "Uvećana slika objekta",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showImageDialog = false }) {
                            Text("Zatvori")
                        }
                    }
                )
            }
        }*/


        if (showRatingDialog && selectedMarker != null) {
            var showImageDialog by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                title = {
                    Text(
                        text = selectedMarker?.title ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        if (selectedMarker?.imageUrl != null) {
                            Image(
                                painter = rememberImagePainter(selectedMarker?.imageUrl),
                                contentDescription = "Slika objekta",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color(0xFFFEFEFE), CircleShape)
                                    .clickable { showImageDialog = true },
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text("Opis: ${selectedMarker?.description}")
                        Text("Ocena: ${selectedMarker?.rating}")

                        Text("Komentari:")
                        selectedMarker?.comments?.forEach { comment ->
                            Text("- $comment")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedMarker?.isQuiz == true) {
                            Text("Odaberite odgovor na pitanje:")
                            Row {
                                RadioButton(
                                    selected = selectedAnswer == true,
                                    onClick = { selectedAnswer = true }
                                )
                                Text("Da")
                                Spacer(modifier = Modifier.width(8.dp))
                                RadioButton(
                                    selected = selectedAnswer == false,
                                    onClick = { selectedAnswer = false }
                                )
                                Text("Ne")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Unesite ocenu za događaj (1-5):")
                        OutlinedTextField(
                            value = ratingValue,
                            onValueChange = { ratingValue = it },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Komentar:")
                        OutlinedTextField(
                            value = commentValue,
                            onValueChange = { commentValue = it }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val newRating = ratingValue.text.toDoubleOrNull()
                        val comment = commentValue.text
                        val username = getUsernameFromLocal(context)

                        if (newRating != null && newRating in 1.0..5.0) {
                            if (username != null) {
                                FirestoreUtils.rateObject(
                                    context = context,
                                    title = selectedMarker!!.title,
                                    newRating = newRating,
                                    comment = comment,
                                    username = username,
                                    selectedAnswer = selectedMarker?.isQuiz?.let { selectedAnswer } // Prosleđujemo odgovor samo ako je kviz
                                )
                                Toast.makeText(context, "Uspešno ocenjen ${selectedMarker!!.title}!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Niste prijavljeni. Molimo prijavite se ponovo.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Unesite validnu ocenu između 1 i 5.", Toast.LENGTH_SHORT).show()
                        }
                        showRatingDialog = false
                        ratingValue = TextFieldValue("")
                        commentValue = TextFieldValue("") // Resetovanje polja za unos komentara
                    }) {
                        Text("Sačuvaj")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showRatingDialog = false
                        ratingValue = TextFieldValue("")
                        commentValue = TextFieldValue("") // Resetovanje polja za unos komentara
                    }) {
                        Text("Otkaži")
                    }
                }
            )
            // Uvećana slika prilikom klika
            if (showImageDialog) {
                AlertDialog(
                    onDismissRequest = { showImageDialog = false },
                    text = {
                        Image(
                            painter = rememberImagePainter(selectedMarker?.imageUrl),
                            contentDescription = "Uvećana slika objekta",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showImageDialog = false }) {
                            Text("Zatvori")
                        }
                    }
                )
            }
        }



        var categoryFilter by remember { mutableStateOf(TextFieldValue("")) }

        var categories by remember { mutableStateOf<List<String>>(emptyList()) }
        var showCategoriesDialog by remember { mutableStateOf(false) }

        var users by remember { mutableStateOf<List<String>>(emptyList()) }
        var showUserFilterDialog by remember { mutableStateOf(false) }
        var userFilter by remember { mutableStateOf(TextFieldValue("")) }

        var showFilterOptionsDialog by remember { mutableStateOf(false) }
        var selectedFilterOption by remember { mutableStateOf("category") }

        var showCategoryFilterDialog by remember { mutableStateOf(false) }


        var showDateFields by remember { mutableStateOf(false) }
        var startDate by remember { mutableStateOf(TextFieldValue("")) }
        var endDate by remember { mutableStateOf(TextFieldValue("")) }

        var ratingFilter by remember { mutableStateOf(TextFieldValue("")) }

        var showRatingFilterField by remember { mutableStateOf(false) }



        LaunchedEffect(Unit) {
            // Učitaj sve korisnike
            firestore.collection("users")
                .get()
                .addOnSuccessListener { snapshot: QuerySnapshot ->
                    users = snapshot.documents.map { it.getString("username") ?: "" }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Greška prilikom učitavanja korisnika: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


        LaunchedEffect(Unit) {
            FirestoreUtils.getCategories(
                onSuccess = { fetchedCategories ->
                    categories = fetchedCategories
                },
                onFailure = { e ->
                    Toast.makeText(context, "Greška prilikom učitavanja kategorija: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Dodajemo padding kako bismo osigurali da dugmadi ne budu previše blizu ivice ekrana
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Text(text = "Vaša lokacija: ${currentLocation.latitude}/${currentLocation.longitude}")
            //Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                shouldMoveCamera = true
                if (permissions.all {
                        ContextCompat.checkSelfPermission(
                            context,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }) {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    locationShared = true // Postavljamo da je lokacija podeljena
                } else {
                    launcher.launch(permissions)
                }
            }) {
                Text(text = "Podeli svoju lokaciju!")
            }

            if (showCategoriesDialog) {
                AlertDialog(
                    onDismissRequest = { showCategoriesDialog = false },
                    title = { Text("Dostupne kategorije") },
                    text = {
                        Column {
                            categories.forEach { category ->
                                Text(text = category)
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showCategoriesDialog = false }) {
                            Text("Zatvori")
                        }
                    }
                )
            }

            if (locationShared) {
                //Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly, // Rasporedite dugmad ravnomerno
                    modifier = Modifier.fillMaxWidth() // Da bi se dugmad širile na celu širinu
                ) {
                    Button(onClick = { showFilterOptionsDialog = true }) {
                        Text(text = "Filtriraj")
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Razmak između dugmadi

                    if (showFilterOptionsDialog) {
                        AlertDialog(
                            onDismissRequest = { showFilterOptionsDialog = false },
                            title = { Text("Izaberite opciju filtriranja") },
                            text = {
                                Column {
                                    Button(onClick = {
                                        selectedFilterOption = "category"
                                        showFilterOptionsDialog = false
                                        showCategoryFilterDialog = true
                                    }) {
                                        Text("Filtriraj po kategoriji")
                                    }
                                    Button(onClick = {
                                        selectedFilterOption = "user"
                                        showFilterOptionsDialog = false
                                        showUserFilterDialog = true
                                    }) {
                                        Text("Filtriraj po korisniku")
                                    }
                                    Button(onClick = {
                                        selectedFilterOption = "date"
                                        showFilterOptionsDialog = false
                                        showDateFields = true
                                    }) {
                                        Text("Filtriraj po datumu")
                                    }

                                    Button(onClick = {
                                        selectedFilterOption = "rating"
                                        showFilterOptionsDialog = false
                                        showRatingFilterField = true
                                    }) {
                                        Text("Filtriraj po oceni")
                                    }

                                    RadiusSearchSection(
                                        context= context,
                                        currentLocation = currentLocation,
                                        markers = markers,
                                        cameraPositionState = cameraPositionState,
                                        onMarkersFiltered = { filteredMarkers -> markers = filteredMarkers }
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = { showFilterOptionsDialog = false }) {
                                    Text("Zatvori")
                                }
                            }
                        )
                    }


                    Button(onClick = { showDialog = "event" }) {
                        Text("Dodaj događaj")
                    }

                    /*Button(onClick = { showChoiceDialog = true }) {
                        Text(text = "Dodaj objekat")
                    }
                    // Prikaz dijaloga za dodavanje objekta kao ranije
                    if (showChoiceDialog) {
                        AddObjectChoiceDialog(
                            onDismiss = { showChoiceDialog = false },
                            onEventSelected = {
                                showChoiceDialog = false
                                showDialog = "event"
                            },
                            onQuizSelected = {
                                showChoiceDialog = false
                                showDialog = "quiz"
                            }
                        )
                    }*/
                }
                Spacer(modifier = Modifier.weight(1f))

                androidx.compose.material.Button(
                    onClick = { navController.navigateUp() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFB8B8B8),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        //.fillMaxWidth() // Postavlja dugme da zauzme celu širinu
                        .padding(bottom = 0.dp) // Razmak od donje ivice
                ) {
                    androidx.compose.material.Text("Nazad")
                }
            }
            if (showCategoryFilterDialog) {
                AlertDialog(
                    onDismissRequest = { showCategoryFilterDialog = false },
                    title = { Text("Filtriraj po kategoriji") },
                    text = {
                        Column {
                            Text("Izaberite kategoriju:")
                            Spacer(modifier = Modifier.height(8.dp))
                            categories.forEach { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            categoryFilter = TextFieldValue(category)
                                        }
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = categoryFilter.text == category,
                                        onClick = {
                                            categoryFilter = TextFieldValue(category)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(category)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = categoryFilter,
                                onValueChange = { categoryFilter = it },
                                label = { Text("Unesite kategoriju za filtriranje") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val filter = categoryFilter.text
                            if (filter.isNotBlank()) {
                                val filteredMarkers = markers.filter { marker ->
                                    marker.category.equals(filter, ignoreCase = true)
                                }
                                markers = filteredMarkers
                                Toast.makeText(
                                    context,
                                    "Prikazano je ${filteredMarkers.size} objekata za kategoriju '$filter'.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Odzumiravanje mape
                                if (filteredMarkers.isNotEmpty()) {
                                    val firstMarker = filteredMarkers.first()
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                        firstMarker.position,
                                        12f
                                    ) // Postavite odgovarajući zoom
                                }
                            }
                            showCategoryFilterDialog = false
                            categoryFilter = TextFieldValue("") // Resetujemo unos
                        }) {
                            Text("Filtriraj")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showCategoryFilterDialog = false
                            categoryFilter = TextFieldValue("") // Resetujemo unos
                        }) {
                            Text("Otkaži")
                        }
                    }
                )
            }

            if (showUserFilterDialog) {
                AlertDialog(
                    onDismissRequest = { showUserFilterDialog = false },
                    title = { Text("Filtriraj po korisniku") },
                    text = {
                        Column {
                            Text("Izaberite korisnika:")
                            Spacer(modifier = Modifier.height(8.dp))
                            users.forEach { user ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            userFilter = TextFieldValue(user)
                                        }
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = userFilter.text == user,
                                        onClick = {
                                            userFilter = TextFieldValue(user)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(user)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = userFilter,
                                onValueChange = { userFilter = it },
                                label = { Text("Unesite korisnika za filtriranje") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val filter = userFilter.text
                            if (filter.isNotBlank()) {
                                val filteredMarkers = markers.filter { marker ->
                                    marker.username.equals(filter, ignoreCase = true)
                                }
                                markers = filteredMarkers
                                Toast.makeText(
                                    context,
                                    "Prikazano je ${filteredMarkers.size} objekata koje je kreirao korisnik '$filter'.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Odzumiravanje mape
                                if (filteredMarkers.isNotEmpty()) {
                                    val firstMarker = filteredMarkers.first()
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                        firstMarker.position,
                                        12f
                                    ) // Postavite odgovarajući zoom
                                }
                            }
                            showUserFilterDialog = false
                            userFilter = TextFieldValue("") // Resetujemo unos
                        }) {
                            Text("Filtriraj")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showUserFilterDialog = false
                            userFilter = TextFieldValue("") // Resetujemo unos
                        }) {
                            Text("Otkaži")
                        }
                    }
                )
            }

          if (showDateFields) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Datum od (YYYY-MM-DD)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Datum do (YYYY-MM-DD)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Logika za filtriranje po datumu
                    val filteredMarkers = markers.filter { marker ->
                        marker.date >= startDate.text && marker.date <= endDate.text
                    }
                    markers = filteredMarkers
                    Toast.makeText(context, "Prikazano je ${filteredMarkers.size} objekata u zadatom opsegu datuma.", Toast.LENGTH_SHORT).show()
                    if (filteredMarkers.isNotEmpty()) {
                        val firstMarker = filteredMarkers.first()
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            firstMarker.position,
                            12f
                        )
                    }
                }) {
                    Text("Primeni filter")
                }
            }


            if (showRatingFilterField) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = ratingFilter,
                    onValueChange = { ratingFilter = it },
                    label = { Text("Unesite ocenu za filtriranje") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val rating = ratingFilter.text.toDoubleOrNull()
                    if (rating != null) {
                        val filteredMarkers = markers.filter { it.rating == rating }
                        markers = filteredMarkers
                        Toast.makeText(
                            context,
                            "Prikazano je ${filteredMarkers.size} objekata sa ocenom $rating.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Odzumiravanje mape ako postoje rezultati
                        if (filteredMarkers.isNotEmpty()) {
                            val firstMarker = filteredMarkers.first()
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                firstMarker.position,
                                12f
                            )
                        }
                    } else {
                        Toast.makeText(context, "Unesite validnu ocenu.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = "Prikaži po oceni")
                }
            }

            /*if (showChoiceDialog) {
                AddObjectChoiceDialog(
                    onDismiss = { showChoiceDialog = false },
                    onEventSelected = {
                        showChoiceDialog = false
                        showDialog = "event"
                    },
                    onQuizSelected = {
                        showChoiceDialog = false
                        showDialog = "quiz"
                    }
                )
            }*/
            val username = getUsernameFromLocal(context)
/*
            when (showDialog) {
                "event" -> {
                    AddEventDialog(
                        onDismiss = { showDialog = null },
                        onSave = { title, description, date, time, category,imageUri ->
                            if (username != null) {
                                FirestoreUtils.addEvent(
                                    context = context,
                                    title = title,
                                    description = description,
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude,
                                    date = date,
                                    time = time,
                                    category=category,
                                    username = username,
                                    imageUri = imageUri // Pass imageUrl to FirestoreUtils
                                )
                            } else {
                                Toast.makeText(context, "Niste prijavljeni. Molimo prijavite se ponovo.", Toast.LENGTH_SHORT).show()
                            }
                            //Toast.makeText(context, "Događaj uspešno dodat!", Toast.LENGTH_SHORT).show()
                            showDialog = null
                        }
                    )
                }
                "quiz" -> {
                    AddQuizDialog(
                        onDismiss = { showDialog = null },
                        onSave = { title, questionText, correctAnswer,category->
                            if (username != null) {
                                FirestoreUtils.addQuiz(
                                    context = context,
                                    title = title,
                                    description = questionText,
                                    correctAnswer = correctAnswer,
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude,
                                    category=category,
                                    //isCorrectAnswer = isCorrectAnswer,
                                    username = username,
                                )
                            } else {
                                Toast.makeText(context, "Niste prijavljeni. Molimo prijavite se ponovo.", Toast.LENGTH_SHORT).show()
                            }
                            //Toast.makeText(context, "Kviz uspešno dodat!", Toast.LENGTH_SHORT).show()
                            showDialog = null
                        }
                    )
                }
            }*/
            when (showDialog) {
                "event" -> {
                    AddEventDialog(
                        onDismiss = { showDialog = null },
                        onSave = { title, description, date, time, category, imageUri, quizTitle, quizQuestion, quizCorrectAnswer ->
                            if (username != null) {
                                FirestoreUtils.addEvent(
                                    context = context,
                                    title = title,
                                    description = description,
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude,
                                    date = date,
                                    time = time,
                                    category = category,
                                    username = username,
                                    imageUri = imageUri,
                                    quizTitle = quizTitle,
                                    quizQuestion = quizQuestion,
                                    quizCorrectAnswer = quizCorrectAnswer
                                )
                            } else {
                                Toast.makeText(context, "Niste prijavljeni. Molimo prijavite se ponovo.", Toast.LENGTH_SHORT).show()
                            }
                            showDialog = null
                        }
                    )
                }
            }
        }
    }
}

data class MarkerData(
    val title: String,
    val description: String,
    val position: LatLng,
    val rating: Double,
    val comments: List<String> = emptyList(),  // Dodato polje za komentare
    val category: String,
    val username:String,
    val date: String,
    val isQuiz: Boolean,
    val correctAnswer: Boolean? = null, // Dodajemo correctAnswer polje
    val imageUrl: String? = null // Dodato
)

@Composable
fun MarkerTitle(title: String) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .border(1.dp, Color.Black)
            .padding(8.dp)
    ) {
        Text(text = title)
    }
}
