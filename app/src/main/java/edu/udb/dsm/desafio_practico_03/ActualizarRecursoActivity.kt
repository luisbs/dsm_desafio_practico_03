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

class ActualizarRecursoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECURSO_ID = "extra_recurso_id"
        const val EXTRA_RECURSO_TITULO = "extra_recurso_titulo"
        const val EXTRA_RECURSO_DESCRIPCION = "extra_recurso_descripcion"
        const val EXTRA_RECURSO_TIPO = "extra_recurso_tipo"
        const val EXTRA_RECURSO_ENLACE = "extra_recurso_enlace"
        const val EXTRA_RECURSO_IMAGEN = "extra_recurso_imagen"
    }

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

    private lateinit var btnActualizar: Button
    private lateinit var btnCancelar: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var api: ApiService
    private var recursoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_recurso)

        // Inicializar vistas
        inicializarVistas()

        // Configurar Retrofit
        configurarRetrofit()

        // Obtener datos del recurso a actualizar
        obtenerDatosDelIntent()

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

        btnActualizar = findViewById(R.id.btnActualizar)
        btnCancelar = findViewById(R.id.btnCancelar)
        progressBar = findViewById(R.id.progressBarActualizar)
    }

    private fun configurarRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://681d6b31f74de1d219afad4b.mockapi.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    private fun obtenerDatosDelIntent() {
        // Recuperar datos del intent
        intent.extras?.let { extras ->
            recursoId = extras.getString(EXTRA_RECURSO_ID, "")
            etTitulo.setText(extras.getString(EXTRA_RECURSO_TITULO, ""))
            etDescripcion.setText(extras.getString(EXTRA_RECURSO_DESCRIPCION, ""))
            etTipo.setText(extras.getString(EXTRA_RECURSO_TIPO, ""))
            etEnlace.setText(extras.getString(EXTRA_RECURSO_ENLACE, ""))
            etImagen.setText(extras.getString(EXTRA_RECURSO_IMAGEN, ""))
        }

        // Si no hay ID, cerrar la actividad
        if (recursoId.isEmpty()) {
            Toast.makeText(this, "Error: no se proporcionó ID del recurso", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun configurarListeners() {
        btnActualizar.setOnClickListener {
            if (validarCampos()) {
                actualizarRecurso()
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

    private fun actualizarRecurso() {
        // Mostrar progress bar
        progressBar.visibility = View.VISIBLE

        // Desactivar botones
        btnActualizar.isEnabled = false
        btnCancelar.isEnabled = false

        // Crear objeto Recurso con los datos actualizados
        val recursoActualizado = Recurso(
            id = recursoId,
            titulo = etTitulo.text.toString(),
            descripcion = etDescripcion.text.toString(),
            tipo = etTipo.text.toString(),
            enlace = etEnlace.text.toString(),
            imagen = etImagen.text.toString()
        )

        // Llamar al servicio para actualizar el recurso
        lifecycleScope.launch {
            try {
                // Convertir el ID a entero ya que la API lo requiere así
                val id = recursoId.toInt()

                // Utilizamos await() para esperar la respuesta
                api.updateRecursos(id, recursoActualizado)

                Toast.makeText(
                    this@ActualizarRecursoActivity,
                    "Recurso actualizado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Cerrar activity y volver a MainActivity
                finish()
            } catch (e: Exception) {
                Log.e("ActualizarRecursoActivity", "Error al actualizar recurso", e)

                Toast.makeText(
                    this@ActualizarRecursoActivity,
                    "Error al actualizar recurso: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                // Ocultar progress bar
                progressBar.visibility = View.GONE

                // Activar botones
                btnActualizar.isEnabled = true
                btnCancelar.isEnabled = true
            }
        }
    }
}