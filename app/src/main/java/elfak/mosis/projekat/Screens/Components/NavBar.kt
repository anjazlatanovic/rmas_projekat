package elfak.mosis.projekat.Screens.Components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import elfak.mosis.projekat.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import elfak.mosis.projekat.Route

@Composable
fun MainScreenContent(navController: NavController, username: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Ostali sadržaj ekrana ide ovde

        // NavBar na dnu ekrana
        NavBar(navController = navController,username = username, modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 0.dp)) // Postavljanje na dno ekrana
    }
}

@Composable
fun NavBar(navController: NavController, username: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White) // svetlija nijansa crvene
            .padding(0.dp) // Ukloni sve dodatne padding
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavBarItem(
            icon = painterResource(id = R.drawable.events),
            label = "Marker",
            //onClick = { navController.navigate(Route.AllMarkerScreen().name) }
            onClick = { navController.navigate("${Route.AllMarkerScreen().name}/$username") }
        )
        NavBarItem(
            icon = painterResource(id = R.drawable.map),
            label = "Lokacija",
            //onClick = { navController.navigate(Route.LocationScreen().name) }
            onClick = { navController.navigate("${Route.LocationScreen().name}/$username") }
        )
        NavBarItem(
            icon = painterResource(id = R.drawable.trophy),
            label = "Rang",
            //onClick = { navController.navigate(Route.RankingScreen().name) }
            onClick = { navController.navigate("${Route.RankingScreen().name}/$username") }

        )
        NavBarItem(
            icon = painterResource(id = R.drawable.people),
            label = "Profil",
            onClick = { navController.navigate("${Route.ProfileScreen().name}/$username") }
        )
    }
}



@Composable
fun NavBarItem(icon: Painter, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .background(Color(0xFFFEFEFE), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp), // Manja veličina ikonica
            tint = Color.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp, // Manji fontSize za tekst
            color = Color.Black
        )
    }
}
