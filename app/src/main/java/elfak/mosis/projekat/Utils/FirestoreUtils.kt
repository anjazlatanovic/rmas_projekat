package elfak.mosis.projekat.Screens

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.projekat.Screens.Location.MarkerData
//import elfak.mosis.projekat.Screens.Location.MarkerData
import elfak.mosis.projekat.Screens.Profil.UserProfile
import org.w3c.dom.Comment
import java.util.UUID

object FirestoreUtils {

    private val firestore = Firebase.firestore

    fun addEvent(
        context: Context,
        title: String,
        description: String,
        latitude: Double,
        longitude: Double,
        date: String,
        time: String,
        category: String,
        username: String,
        imageUri: Uri?, // Dodato

        quizTitle: String?, // Dodato
        quizQuestion: String?, // Dodato
        quizCorrectAnswer: Boolean? // Dodato
    ) {
        val firestore = FirebaseFirestore.getInstance()

        if (imageUri != null) {
            uploadImageToFirebaseStorage(context, imageUri,
                onSuccess = { imageUrl ->
                    saveEventToFirestore(context, title, description, latitude, longitude, date, time, category, username, imageUrl, quizTitle, quizQuestion, quizCorrectAnswer)

                },
                onFailure = { exception ->
                    Toast.makeText(context, "Greška pri snimanju slike: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            saveEventToFirestore(context, title, description, latitude, longitude, date, time, category, username, null,quizTitle, quizQuestion, quizCorrectAnswer)
        }
    }

    private fun saveEventToFirestore(
        context: Context,
        title: String,
        description: String,
        latitude: Double,
        longitude: Double,
        date: String,
        time: String,
        category: String,
        username: String,
        imageUrl: String?,


        quizTitle: String?, // Dodato
        quizQuestion: String?, // Dodato
        quizCorrectAnswer: Boolean? // Dodato
    ) {
        val newObject = hashMapOf(
            "title" to title,
            "description" to description,
            "latitude" to latitude,
            "longitude" to longitude,
            "date" to date,
            "time" to time,
            "category" to category,
            "username" to username,
            "imageUrl" to imageUrl, // Dodato


            "isQuiz" to false,
            "isQuiz" to (quizTitle != null),
            "quizTitle" to quizTitle,
            "quizQuestion" to quizQuestion,
            "quizCorrectAnswer" to quizCorrectAnswer
        )

        FirebaseFirestore.getInstance().collection("objects")
            .add(newObject)
            .addOnSuccessListener {
                Toast.makeText(context, "Događaj uspešno dodat!", Toast.LENGTH_SHORT).show()
                updatePoints(username, 10) // +10 poena
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Greška prilikom dodavanja događaja: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



    fun uploadImageToFirebaseStorage(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
                Toast.makeText(context, "Greška pri snimanju slike!", Toast.LENGTH_SHORT).show()
            }
    }

    fun checkAndAwardPoints(context: Context, username: String, marker: MarkerData) {
        val visitedRef = firestore.collection("visited_objects").document(username)

        visitedRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Pretpostavljamo da je "visited" lista tipa MutableList<String>
                val visitedObjects = document.get("visited") as? MutableList<String> ?: mutableListOf()

                if (!visitedObjects.contains(marker.title)) {
                    visitedObjects.add(marker.title)
                    visitedRef.update("visited", visitedObjects).addOnCompleteListener {
                        if (it.isSuccessful) {
                             updatePoints(username, 2)
                            Toast.makeText(context, "Uspešno ste posetili ${marker.title}!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    //Toast.makeText(context, "Već ste posetili ${marker.title}!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newVisitedData = hashMapOf(
                    "visited" to mutableListOf(marker.title) // Koristi MutableList za novi dokument
                )
                visitedRef.set(newVisitedData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //updatePoints(username, 2)
                        //Toast.makeText(context, "Uspešno ste posetili ${marker.title}!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Greška prilikom pristupa bazi podataka.", Toast.LENGTH_SHORT).show()
        }


    }


    fun updatePoints(username: String, pointsToAdd: Int) {
        val userRef = firestore.collection("users").whereEqualTo("username", username)

        userRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val currentPoints = document.getLong("points") ?: 0
                    val newPoints = currentPoints + pointsToAdd

                    document.reference.update("points", newPoints)
                }
            }
            .addOnFailureListener { e ->
                // U slučaju greške pri ažuriranju poena
                e.printStackTrace()
            }
    }


    fun rateObject(
        context: Context,
        title: String,
        newRating: Double,
        comment: String,
        username: String,
        selectedAnswer: Boolean? // Dodajemo argument za odgovor
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val objectsRef = firestore.collection("objects").whereEqualTo("title", title)

        objectsRef.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.documents.isNotEmpty()) {
                val document = querySnapshot.documents[0]
                val isQuiz = document.getBoolean("isQuiz") ?: false

                val currentRating = document.getDouble("rating") ?: 0.0
                val ratingCount = document.getLong("ratingCount") ?: 0

                val newRatingCount = ratingCount + 1
                val updatedRating = (currentRating * ratingCount + newRating) / newRatingCount

                val correctAnswer = document.getBoolean("correctAnswer") ?: false

                // Ažuriraj ocenu, dodaj komentar i proveri odgovor
                document.reference.update(
                    mapOf(
                        "rating" to updatedRating,
                        "ratingCount" to newRatingCount,
                        "comments" to FieldValue.arrayUnion(comment)
                    )
                ).addOnSuccessListener {
                    Log.d("FirestoreUtils", "Ocena i komentar uspešno ažurirani.")

                    // Logika specifična za kviz
                    if (isQuiz) {
                        var points = 5
                        if (selectedAnswer == correctAnswer) {
                            showQuizResult(context, true) // Prikazi obaveštenje za tačan odgovor
                        } else {
                            points += 7 // Dodajemo 7 poena za tačan odgovor
                            showQuizResult(context, false) // Prikazi obaveštenje za pogrešan odgovor
                        }
                        updatePoints(username, points)
                    } else {
                        // Logika specifična za događaj
                        updatePoints(username, 5) // +5 poena za ocenu ili komentar
                    }
                }.addOnFailureListener { e ->
                    Log.e("FirestoreUtils", "Greška pri ažuriranju: ${e.message}")
                    Toast.makeText(context, "Greška pri ažuriranju: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Objekat nije pronađen.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun showQuizResult(context: Context, isCorrect: Boolean) {
        val message = if (isCorrect) {
            "Nažalost, niste tačno odgovorili na pitanje."
        } else {
            "Čestitamo! Uspešno ste odgovorili na pitanje."
        }

        AlertDialog.Builder(context)
            .setTitle("Rezultat")
            .setMessage(message)
            .setPositiveButton("U redu", null)
            .show()
    }


    fun getUsersRankedByPoints(onSuccess: (List<UserProfile>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users")
            .orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val users = documents.map { document ->
                    UserProfile(
                        username = document.getString("username") ?: "",
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        email = document.getString("email") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        points = (document.getLong("points") ?: 0).toInt(),
                        profileImageUrl = document.getString("profileImageUrl") ?: "" // Dodajemo profileImageUrl
                    )
                }
                onSuccess(users)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // U FirestoreUtils.kt
    fun getCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        FirebaseFirestore.getInstance().collection("objects")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val categories = querySnapshot.documents
                    .mapNotNull { it.getString("category") }
                    .distinct()  // Koristi Kotlin kolekciju za jedinstvene vrednosti
                onSuccess(categories)
            }
            .addOnFailureListener { e ->
                onFailure(e)
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
        val comments: List<String>,
        val username: String, // Dodano polje
        //val isCorrectAnswer: Boolean? = null // Dodato polje za odgovor
        val imageUri: String? // Dodajemo ovo

    )
}