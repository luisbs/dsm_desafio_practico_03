package edu.udb.dsm.desafio_practico_03

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import edu.udb.dsm.desafio_practico_03.auth.AuthActivity
import edu.udb.dsm.desafio_practico_03.auth.LoginActivity
import edu.udb.dsm.desafio_practico_03.services.ApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AuthActivity() {
    override val activityLayout = R.layout.activity_main
    override val activityTitle = R.string.app_name

    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("https://681d6b31f74de1d219afad4b.mockapi.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        api = retrofit.create(ApiService::class.java)

        clickListener(R.id.btnLogout) {
            auth.signOut()
            switchTo(LoginActivity::class)
        }

        // early execution for when the app is launched
        authStateListener.onAuthStateChanged(auth)

        obtenerAlumnos()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser === null) return
    }

    private fun obtenerAlumnos() {
        lifecycleScope.launch {
            try {
                val alumnos = api.getRecursos()
                // Aquí puedes actualizar tu UI, por ejemplo:
                alumnos.forEach {
                    Log.d("MainActivity", "Alumno: ${it.titulo}, ${it.descripcion}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al obtener alumnos", e)
            }
        }
    }
}