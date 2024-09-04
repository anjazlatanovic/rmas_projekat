package elfak.mosis.projekat.Utils

import android.content.Context

fun saveUsernameToLocal(context: Context, username: String) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("username", username)
    editor.apply()
}

fun getUsernameFromLocal(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("username", null)
}