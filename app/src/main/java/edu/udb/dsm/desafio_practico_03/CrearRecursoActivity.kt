package edu.udb.dsm.desafio_practico_03

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.udb.dsm.desafio_practico_03.model.Recurso
import edu.udb.dsm.desafio_practico_03.services.ApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CrearRecursoActivity : AppCompatActivity() {

    private lateinit var tilTitulo: TextInputLayout
    private lateinit var tilDescripcion: TextInputLayout
    private lateinit var tilTipo: TextInputLayout
    private lateinit var tilEnlace: TextInputLayout
    private lateinit var tilImagen: TextInputLayout

    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var etTipo: TextInputEditText
    private lateinit var etEnlace: TextInputEditText
    private lateinit var etImagen: TextInputEditText

    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_recurso)

        // Inicializar vistas
        inicializarVistas()

        // Configurar Retrofit
        configurarRetrofit()

        // Configurar listeners
        configurarListeners()
    }

    private fun inicializarVistas() {
        tilTitulo = findViewById(R.id.tilTitulo)
        tilDescripcion = findViewById(R.id.tilDescripcion)
        tilTipo = findViewById(R.id.tilTipo)
        tilEnlace = findViewById(R.id.tilEnlace)
        tilImagen = findViewById(R.id.tilImagen)

        etTitulo = findViewById(R.id.etTitulo)
        etDescripcion = findViewById(R.id.etDescripcion)
        etTipo = findViewById(R.id.etTipo)
        etEnlace = findViewById(R.id.etEnlace)
        etImagen = findViewById(R.id.etImagen)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        progressBar = findViewById(R.id.progressBarCrear)
    }

    private fun configurarRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://681d6b31f74de1d219afad4b.mockapi.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    private fun configurarListeners() {
        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                crearRecurso()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        // Validar título
        if (etTitulo.text.isNullOrBlank()) {
            tilTitulo.error = "El título es obligatorio"
            esValido = false
        } else {
            tilTitulo.error = null
        }

        // Validar descripción
        if (etDescripcion.text.isNullOrBlank()) {
            tilDescripcion.error = "La descripción es obligatoria"
            esValido = false
        } else {
            tilDescripcion.error = null
        }

        // Validar tipo
        if (etTipo.text.isNullOrBlank()) {
            tilTipo.error = "El tipo es obligatorio"
            esValido = false
        } else {
            tilTipo.error = null
        }

        // Validar enlace
        if (etEnlace.text.isNullOrBlank()) {
            tilEnlace.error = "El enlace es obligatorio"
            esValido = false
        } else {
            tilEnlace.error = null
        }

        // Validar imagen
        if (etImagen.text.isNullOrBlank()) {
            tilImagen.error = "La URL de la imagen es obligatoria"
            esValido = false
        } else {
            tilImagen.error = null
        }

        return esValido
    }

    private fun crearRecurso() {
        // Mostrar progress bar
        progressBar.visibility = View.VISIBLE

        // Desactivar botones
        btnGuardar.isEnabled = false
        btnCancelar.isEnabled = false

        // Crear objeto Recurso
        val nuevoRecurso = Recurso(
            titulo = etTitulo.text.toString(),
            descripcion = etDescripcion.text.toString(),
            tipo = etTipo.text.toString(),
            enlace = etEnlace.text.toString(),
            imagen = etImagen.text.toString(),
            id = "" // El id será asignado por la API
        )

        // Llamar al servicio para crear el recurso
        lifecycleScope.launch {
            try {
                api.createRecursos(nuevoRecurso)

                Toast.makeText(
                    this@CrearRecursoActivity,
                    "Recurso creado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Cerrar activity y volver a MainActivity
                finish()
            } catch (e: Exception) {
                Log.e("CrearRecursoActivity", "Error al crear recurso", e)

                Toast.makeText(
                    this@CrearRecursoActivity,
                    "Error al crear recurso: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Ocultar progress bar
                progressBar.visibility = View.GONE

                // Activar botones
                btnGuardar.isEnabled = true
                btnCancelar.isEnabled = true
            }
        }
    }
}