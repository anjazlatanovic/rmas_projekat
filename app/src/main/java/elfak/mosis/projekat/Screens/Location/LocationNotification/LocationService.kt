package elfak.mosis.projekat.Screens.Location.LocationNotification

import android.util.Log
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.projekat.MainActivity
import elfak.mosis.projekat.R
import elfak.mosis.projekat.Screens.FirestoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService :Service(){
    private val serviceScope= CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient= DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "onStartCommand called with action: ${intent?.action}")
        when(intent?.action){
            ACTION_START ->start()
            ACTION_STOP ->stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

private fun start() {
    // Kreirajte Intent koji otvara vašu aplikaciju
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    // Kreirajte PendingIntent za otvaranje aplikacije
    val pendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(this, "location")
        .setContentTitle("Tracking location...")
        .setContentText("Location: null")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setOngoing(true)
        .setContentIntent(pendingIntent) // Dodajte PendingIntent
        .setAutoCancel(true) // Opcionalno, uklonite notifikaciju kada korisnik klikne na nju

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    locationClient
        .getLocationUpdates(10000L)
        .catch { e -> e.printStackTrace() }
        .onEach { location ->
            val lat = location.latitude
            val long = location.longitude

            // Ažuriraj notifikaciju sa trenutnom lokacijom
            var updateNotification = notification.setContentText("Location:($lat,$long)")
            notificationManager.notify(1, updateNotification.build())

            // Preuzmi objekte u blizini trenutne lokacije
            getNearbyObjects(lat, long) { nearbyObjects ->
                // Kreiraj string sa informacijama o objektima
                val objectInfo = nearbyObjects.joinToString("\n") { obj ->
                    "${obj.title} - ${obj.description}"
                }

                // Koristi BigTextStyle za proširivanje notifikacije
                notification.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Vaša lokacija:($lat,$long)\nObjekti u Vašoj blizini:\n$objectInfo")
                )

                // Ažuriraj notifikaciju sa informacijama o objektima
                val fullNotificationText = "Location:($lat,$long)\n:\n$objectInfo"
                updateNotification = notification.setContentText(fullNotificationText)
                notificationManager.notify(1, updateNotification.build())
            }
        }
        .launchIn(serviceScope)

    startForeground(1, notification.build())
}

    private fun getNearbyObjects(latitude: Double, longitude: Double, onResult: (List<FirestoreUtils.UserEvent>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val radiusInMeters = 1000.0 // Primer: 1km radijus
        val nearbyObjects = mutableListOf<FirestoreUtils.UserEvent>()

        firestore.collection("objects")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val objLat = document.getDouble("latitude") ?: continue
                    val objLong = document.getDouble("longitude") ?: continue
                    val distance = FloatArray(1)
                    Location.distanceBetween(latitude, longitude, objLat, objLong, distance)

                    if (distance[0] <= radiusInMeters) {
                        nearbyObjects.add(
                            FirestoreUtils.UserEvent(
                                title = document.getString("title") ?: "",
                                description = document.getString("description") ?: "",
                                date = document.getString("date") ?: "",
                                time = document.getString("time") ?: "",
                                latitude = objLat,
                                longitude = objLong,
                                isQuiz = false,
                                rating = document.getDouble("rating") ?: 0.0,
                                comments = document.get("comments") as? List<String> ?: listOf(),
                                username = document.getString("username") ?: "",
                                imageUri = document.getString("imageUrl") // Dodano čitanje URL-a slike

                            )
                        )
                    }
                }
                onResult(nearbyObjects)
            }
    }


    private fun stop(){
        // Zaustavi servis iz foreground-a
        stopForeground(true)

        // Ukloni notifikaciju
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)

        // Zaustavi servis
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START="ACTION_START"
        const val ACTION_STOP="ACTION_STOP"
    }

}