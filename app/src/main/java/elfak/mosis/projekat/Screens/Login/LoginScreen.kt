package elfak.mosis.projekat.Screens.Login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import elfak.mosis.projekat.Screens.Components.HeaderText
import elfak.mosis.projekat.Screens.Components.LoginTextField
import elfak.mosis.projekat.ui.theme.ProjekatTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.projekat.Utils.saveUsernameToLocal
import org.mindrot.jbcrypt.BCrypt

val defaultPadding = 16.dp
val itemSpacing = 8.dp

@Composable
fun LoginScreen(
    onLoginClick: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val (userName, setUsername) = rememberSaveable { mutableStateOf("") }
    val (password, setPassword) = rememberSaveable { mutableStateOf("") }
    val (checked, onCheckedChange) = rememberSaveable { mutableStateOf(false) }

    val isFieldsEmpty = userName.isNotEmpty() && password.isNotEmpty()
    val context = LocalContext.current
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(defaultPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(
            text = "Login",
            modifier = Modifier
                .padding(vertical = defaultPadding)
                .align(alignment = Alignment.Start)
        )
        LoginTextField(
            value = userName,
            onValueChange = setUsername,
            labelText = "Korisničko ime",
            leadingIcon = Icons.Default.Person,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(itemSpacing))

        LoginTextField(
            value = password,
            onValueChange = setPassword,
            labelText = "Password",
            leadingIcon = Icons.Default.Lock,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(itemSpacing))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                //Checkbox(checked = checked, onCheckedChange = onCheckedChange)
                //Text("Zapamti me")
            }
            //TextButton(onClick = { }) {
                //Text("Zaboravio si lozinku?")
            //}
        }
        Spacer(Modifier.height(itemSpacing))

        Button(
            onClick = {
                if (userName.isNotEmpty() && password.isNotEmpty()) {
                    db.collection("users")
                        .whereEqualTo("username", userName) // Proveravamo da li postoji korisnik sa unetim korisničkim imenom
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                Toast.makeText(context, "Pogrešno korisničko ime ili lozinka!", Toast.LENGTH_SHORT).show()
                            } else {
                                val document = documents.first()
                                val storedHashedPassword = document.getString("password") ?: ""

                                // Proveravamo da li unesena lozinka odgovara hash-ovanoj lozinki
                                if (checkPassword(password, storedHashedPassword)) {
                                    Toast.makeText(context, "Uspešna prijava!", Toast.LENGTH_SHORT).show()
                                    saveUsernameToLocal(context, userName) // Sačuvaj username u lokalno skladište
                                    onLoginClick(userName) // Prosledi username
                                    onLoginClick(userName) // Uspešan login, prosleđujemo username

                                } else {
                                    Toast.makeText(context, "Pogrešna lozinka!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Login", "Error getting documents", e)
                            Toast.makeText(context, "Prijava nije uspela!", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFieldsEmpty
        ) {
            Text("Login")
        }
        AlternativeLoginOptions(onSignUpClick = onSignUpClick, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrevLoginScreen() {
    ProjekatTheme {
        LoginScreen({}, {})
    }
}

@Composable
fun AlternativeLoginOptions(
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nemate svoj nalog?")
            Spacer(Modifier.height(itemSpacing))
            TextButton(onClick = { onSignUpClick() }) {
                Text("Registrujte se")
            }
        }
    }
}

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun checkPassword(password: String, hashed: String): Boolean {
    return BCrypt.checkpw(password, hashed)
}
