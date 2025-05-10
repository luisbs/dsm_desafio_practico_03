package edu.udb.dsm.desafio_practico_03

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.udb.dsm.desafio_practico_03.adapter.RecursoAdapter
import edu.udb.dsm.desafio_practico_03.auth.AuthActivity
import edu.udb.dsm.desafio_practico_03.auth.LoginActivity
import edu.udb.dsm.desafio_practico_03.model.Recurso
import edu.udb.dsm.desafio_practico_03.services.ApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AuthActivity() {
    override val activityLayout = R.layout.activity_main
    override val activityTitle = R.string.app_name

    private lateinit var api: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var recursoAdapter: RecursoAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar vistas
        recyclerView = findViewById(R.id.rvRecursos)
        progressBar = findViewById(R.id.progressBar)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recursoAdapter = RecursoAdapter(
            onEditClick = { recurso ->
                // Navegar a la pantalla de actualización
                val intent = Intent(this, ActualizarRecursoActivity::class.java).apply {
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_ID, recurso.id)
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_TITULO, recurso.titulo)
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_DESCRIPCION, recurso.descripcion)
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_TIPO, recurso.tipo)
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_ENLACE, recurso.enlace)
                    putExtra(ActualizarRecursoActivity.EXTRA_RECURSO_IMAGEN, recurso.imagen)
                }
                startActivity(intent)
            },
            onDeleteClick = { recurso ->
                // Mostrar alerta de confirmación para eliminar
                mostrarDialogoConfirmacion(recurso)
            }
        )
        recyclerView.adapter = recursoAdapter

        // Configurar botón para agregar recurso
        findViewById<View>(R.id.fabAgregarRecurso).setOnClickListener {
            val intent = Intent(this, CrearRecursoActivity::class.java)
            startActivity(intent)
        }

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

        // Cargar los recursos cuando se inicia la actividad
        cargarRecursos()
    }

    override fun onResume() {
        super.onResume()
        // Recargar recursos cada vez que la actividad se reanude
        // (para reflejar posibles cambios después de crear un nuevo recurso)
        if (auth.currentUser != null) {
            cargarRecursos()
        }
    }

    private fun cargarRecursos() {
        // Mostrar el indicador de progreso
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val recursos = api.getRecursos()
                // Actualizar el adaptador con los datos obtenidos
                recursoAdapter.updateRecursos(recursos)
                Log.d("MainActivity", "Recursos cargados: ${recursos.size}")
                // Ocultar el indicador de progreso
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al obtener recursos", e)
                Toast.makeText(this@MainActivity,
                    "Error al cargar los recursos: ${e.message}",
                    Toast.LENGTH_LONG).show()
                // Ocultar el indicador de progreso
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun mostrarDialogoConfirmacion(recurso: Recurso) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro que desea eliminar el recurso '${recurso.titulo}'?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarRecurso(recurso)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarRecurso(recurso: Recurso) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val id = recurso.id.toInt()
                api.deleteRecursosById(id)
                Toast.makeText(this@MainActivity,
                    "Recurso '${recurso.titulo}' eliminado correctamente",
                    Toast.LENGTH_SHORT).show()

                // Recargar la lista de recursos para ver los cambios
                cargarRecursos()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al eliminar recurso", e)
                Toast.makeText(this@MainActivity,
                    "Error al eliminar el recurso: ${e.message}",
                    Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }
    }
}