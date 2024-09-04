package elfak.mosis.projekat.Screens.Signup

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import elfak.mosis.projekat.Screens.Components.HeaderText
import elfak.mosis.projekat.Screens.Components.LoginTextField
//import elfak.mosis.projekat.Screens.Login.defaultPadding
import elfak.mosis.projekat.ui.theme.ProjekatTheme
import kotlin.math.sin




import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.projekat.Screens.FirestoreUtils.uploadImageToFirebaseStorage
//import defaultPadding
import elfak.mosis.projekat.Screens.Login.defaultPadding
//import elfak.mosis.projekat.View.AuthViewModel
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun SignUpScreen(
    //viewModel: AuthViewModel,
    onSignUpClick: ()->Unit,
    onLoginClick: ()->Unit,
    onPrivacyClick: ()->Unit,

    ) {

    //val db = Firebase.firestore

    //val defaultPadding=16.dp

    //val signUpResult by viewModel.signUpResult.observeAsState()


    val (firstName, onFirstNameChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (lastName, onLastNameChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (username, onUsernameChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (email, onEmailChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (brojTelefona, onBrojTelefonaChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (password, onPasswordChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (confirmPassword, onConfirmPasswordChange) = rememberSaveable {
        mutableStateOf("")
    }

    val (agree, onAgreeChange) = rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    /* ImagePicker(onImageSelected = { uri ->
         selectedImageUri = uri
     })*/

    var isPasswordSame by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )
    val isFieldNotEmpty=firstName.isNotEmpty() && lastName.isNotEmpty() && username.isNotEmpty()
            && email.isNotEmpty() && brojTelefona.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword.isNotEmpty() && agree
    val db = Firebase.firestore
    //var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile: File? = null

    // Create an image file name and URI
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).also { photoFile = it }
    }

    // Launch the camera to take a picture
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                selectedImageUri = Uri.fromFile(photoFile)
            }
        }
    )

    // Launch the gallery to pick an image
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(defaultPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        AnimatedVisibility(isPasswordSame) {
            Text("Lozinke se ne podudaraju!",color=MaterialTheme.colorScheme.error)
        }
        HeaderText(
            text = "Registracija",
            modifier = Modifier
                .padding(vertical = defaultPadding)
                .align(alignment = Alignment.Start)
        )
        LoginTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            labelText = "Ime",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            labelText = "Prezime",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = username,
            onValueChange = onUsernameChange,
            labelText = "Korisničko ime",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = email,
            onValueChange = onEmailChange,
            labelText = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = brojTelefona,
            onValueChange = onBrojTelefonaChange,
            labelText = "Broj telefona",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = password,
            onValueChange = onPasswordChange,
            labelText = "Lozinka",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()

        )
        Spacer(Modifier.height(defaultPadding))
        LoginTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            labelText = "Potvrdite lozinku",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()

        )

        //Button(onClick = { launcher.launch("image/*") }) {
           // Text("Izaberi sliku")
        //}


        Spacer(Modifier.height(defaultPadding))

        Button(onClick = {
            val photoURI: Uri = FileProvider.getUriForFile(
                context,
                "elfak.mosis.projekat.fileprovider",
                createImageFile()
            )
            takePictureLauncher.launch(photoURI)
        }) {
            Text("Snimite sliku")
        }
        Spacer(Modifier.height(defaultPadding))
        Button(onClick = { pickImageLauncher.launch("image/*") }) {
            Text("Izaberi sliku")
        }

        selectedImageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Odabrana slika",
                modifier = Modifier.size(128.dp)
            )
        }
        /*selectedImageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Odabrana slika",
                modifier = Modifier.size(128.dp)
            )
        }*/
        Spacer(Modifier.height(defaultPadding))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            val privacyText = "uslovima privatnosti"
            val annotatedString = buildAnnotatedString{
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                {
                    append("Slazem se sa")
                }
                append(" ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                {
                    pushStringAnnotation(tag = privacyText, privacyText)
                    append(privacyText)
                }
            }
            Checkbox(agree, onAgreeChange)
            ClickableText(annotatedString)
            { offset ->
                annotatedString.getStringAnnotations(offset, offset).forEach {
                    when (it.tag) {
                        privacyText -> {
                            Toast.makeText(context, "Privacy Text Clicked", Toast.LENGTH_SHORT)
                                .show()
                            onPrivacyClick()
                        }

                    }
                }
            }
        }
        Spacer(Modifier.height(defaultPadding + 8.dp))
        /*Button(onClick = {
            isPasswordSame=password!=confirmPassword
            if (!isPasswordSame)
            {
                onSignUpClick()
            }
        }, modifier = Modifier.fillMaxWidth(), enabled =isFieldNotEmpty ) {
            Text("Registruj se")
        }*/



        Button(onClick = {
            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(context, selectedImageUri!!,
                    onSuccess = { imageUrl ->
                        val hashedPassword = hashPassword(password)
                        val user = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username,
                            "email" to email,
                            "phoneNumber" to brojTelefona,
                            "password" to hashedPassword,
                            "profileImageUrl" to imageUrl,
                            "points" to 0
                        )
                        db.collection("users").document(username.trim()).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Uspešna registracija!", Toast.LENGTH_SHORT).show()
                                onSignUpClick()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Registracija nije uspela!", Toast.LENGTH_SHORT).show()
                            }
                    },
                    onFailure = {
                        Toast.makeText(context, "Upload slike nije uspeo.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Molimo izaberite sliku.", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth(), enabled = isFieldNotEmpty) {
            Text("Registruj se")
        }







        Spacer(Modifier.height(defaultPadding))
        val singTx = "Prijavi se"
        val signInAnnotation = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
            {
                append("Imas svoj nalog vec?")
            }
            append(" ")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
            {
                pushStringAnnotation(singTx, singTx)
                append(singTx)
            }

        }
        ClickableText(
            signInAnnotation,
        )
        {
                offset->
            signInAnnotation.getStringAnnotations(offset,offset).forEach {
                if(it.tag==singTx){
                    Toast.makeText(context, "Sign in Clicked", Toast.LENGTH_SHORT).show()
                    onLoginClick()
                }
            }
        }
    }

    /*signUpResult?.let {
        if (it.isSuccess) {
            onSignUpSuccess()
        } else {
            Toast.makeText(LocalContext.current, it.exceptionOrNull()?.message ?: "Sign up failed", Toast.LENGTH_SHORT).show()
        }
    }*/
}

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun checkPassword(password: String, hashed: String): Boolean {
    return BCrypt.checkpw(password, hashed)
}





@Preview(showSystemUi = true)
@Composable
fun PrevSignUp()
{
    ProjekatTheme {
        SignUpScreen({},{},{}/*,{}*/)
    }
}