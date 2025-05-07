package edu.udb.dsm.desafio_practico_03.auth

import android.os.Bundle
import android.widget.EditText
import edu.udb.dsm.desafio_practico_03.MainActivity
import edu.udb.dsm.desafio_practico_03.R
import kotlin.reflect.KClass

class SignupActivity : AuthActivity() {
    override var parentActivity: KClass<*>? = LoginActivity::class
    override val activityLayout = R.layout.activity_signup
    override val activityTitle = R.string.auth_signup_label
    override var guestActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailInput = findViewById<EditText>(R.id.auth_email)
        val passInput = findViewById<EditText>(R.id.auth_pass)
        val checkInput = findViewById<EditText>(R.id.auth_pass_check)

        clickListener(R.id.auth_login) { switchTo(LoginActivity::class) }
        clickListener(R.id.auth_signup) {
            val error = when {
                emailInput.text.isEmpty() -> R.string.auth_email_err
                passInput.text.isEmpty() -> R.string.auth_pass_err
                passInput.text.toString() != checkInput.text.toString() -> R.string.auth_check_err
                else -> -1
            }

            if (error != -1) notify(error)
            else authenticate(
                emailInput.text.toString().trim(), //
                passInput.text.toString().trim()
            )
        }
    }

    private fun authenticate(email: String, pass: String) {
        // authenticate
        auth.createUserWithEmailAndPassword(email, pass) //
            .addOnFailureListener(::failureListener) //
            .addOnSuccessListener {
                switchTo(MainActivity::class, R.string.auth_welcome)
            }
    }
}