package elfak.mosis.projekat.Screens.Ranking

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import elfak.mosis.projekat.R
import elfak.mosis.projekat.Screens.Components.NavBar
import elfak.mosis.projekat.Screens.FirestoreUtils
import elfak.mosis.projekat.Screens.Profil.UserProfile

/*
@Composable
fun RankingScreen(navController: NavController,username:String) {
    var rankedUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirestoreUtils.getUsersRankedByPoints(
            onSuccess = { users ->
                rankedUsers = users
                isLoading = false
            },
            onFailure = { e ->
                Toast.makeText(navController.context, "Error fetching ranking: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Rangiranje korisnika",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Text(text = "Loading...", fontSize = 18.sp, color = Color.Gray)
        } else {
            // Prikaz zaglavlja tabele
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
                .background(Color.LightGray)) {
                Text(text = "Mesto", modifier = Modifier.width(50.dp), fontWeight = FontWeight.Bold)
                Text(text = "Korisničko ime", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
                Text(text = "Ime", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold)
                Text(text = "Prezime", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold)
                Text(text = "Poeni", modifier = Modifier.width(70.dp), fontWeight = FontWeight.Bold)
            }

            // Prikaz korisnika u tabeli
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(rankedUsers) { index, user ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)  // Fiksna visina reda
                        .background(if (index % 2 == 0) Color.White else Color(0xFFF0F0F0))) {
                        Text(text = " ${index + 1}", modifier = Modifier.width(50.dp))
                        Text(text = user.username, modifier = Modifier.width(100.dp))
                        Text(text = user.firstName, modifier = Modifier.width(80.dp))
                        Text(text = user.lastName, modifier = Modifier.width(80.dp))
                        Text(text = "${user.points}", modifier = Modifier.width(70.dp))
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


            Spacer(modifier = Modifier.weight(1f)) // Da bi NavBar bio na dnu
            NavBar(
                navController = navController,
                username=username,
                modifier = Modifier
                    //.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(0.dp)
                //.height(56.dp) // Adjust height as needed
            )        }
    }
}*/

@Composable
fun RankingScreen(navController: NavController, username: String) {
    var rankedUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirestoreUtils.getUsersRankedByPoints(
            onSuccess = { users ->
                rankedUsers = users
                isLoading = false
            },
            onFailure = { e ->
                Toast.makeText(navController.context, "Error fetching ranking: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Rangiranje korisnika",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Text(text = "Loading...", fontSize = 18.sp, color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(rankedUsers) { index, user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(if (index == 0) Color(0xFF81D4FA ) else Color.White)
                            .border(1.dp, Color(0xFF81D4FA )) // Dodavanje ivice u plavoj boji
                            .padding(8.dp)
                    ) {
                        // Profilna slika korisnika
                        /*Image(
                            painter = painterResource(id = R.drawable.people), // Zameniti sa stvarnim izvorom slike
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )*/

                        Text(
                            text = "${index + 1}.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (index == 0) Color.White else Color.Black,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        val imagePainter = rememberImagePainter(user.profileImageUrl)
                        Image(
                            painter = imagePainter,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                            //.border(4.dp, Color(0xFFFEFEFE), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = user.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (index == 0) Color.White else Color.Black
                            )
                            Text(
                                text = "${user.points} poena",
                                fontSize = 16.sp,
                                color = if (index == 0) Color.White else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Trofej za prvog mesta
                        if (index == 0) {
                            Icon(
                                painter = painterResource(id = R.drawable.trophy), // Zameniti sa stvarnim izvorom slike
                                contentDescription = "Trophy",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFB8B8B8), // Boja pozadine dugmeta
                    contentColor = Color.White // Boja teksta dugmeta
                ),
                modifier = Modifier
                    .padding(end = 16.dp) // Razmak od desne ivice
                    .align(Alignment.End) // Poravnanje prema centru horizontale
            ) {
                Text("Nazad")
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
}
