package edu.udb.dsm.desafio_practico_03.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.udb.dsm.desafio_practico_03.MainActivity
import java.lang.Exception
import kotlin.reflect.KClass

abstract class AuthActivity : AppCompatActivity() {
    protected open var parentActivity: KClass<*>? = null
    protected abstract val activityLayout: Int
    protected abstract val activityTitle: Int

    // authentication
    protected open var guestActivity = false
    protected lateinit var auth: FirebaseAuth
    protected var authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser === null) {
            if (!guestActivity) switchTo(LoginActivity::class)
        } else if (guestActivity) switchTo(MainActivity::class)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityLayout)

        auth = FirebaseAuth.getInstance()

        supportActionBar?.setTitle(activityTitle)
        // only shows back button when there is a parentActivity
        if (parentActivity !== null) supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId && parentActivity !== null) {
            startActivity(Intent(this, parentActivity?.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun notify(message: Int) {
        Log.i("DSM App", getString(message))
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun notify(message: String?) {
        Log.i("DSM App", message ?: "")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun switchTo(cls: KClass<*>, message: Int? = null) {
        if (message != null) notify(message)
        startActivity(Intent(this, cls.java))
        finish()
    }

    protected fun clickListener(viewId: Int, listener: OnClickListener) {
        findViewById<View>(viewId).setOnClickListener(listener)
    }

    protected fun failureListener(e: Exception) {
        notify(e.message)
    }
}