package edu.udb.dsm.desafio_practico_03

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.udb.dsm.desafio_practico_03.auth.AuthActivity

class MainActivity : AuthActivity() {
    override val activityLayout = R.layout.activity_main
    override val activityTitle = R.string.app_name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // early execution for when the app is launched
        authStateListener.onAuthStateChanged(auth)
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser === null) return
    }
}