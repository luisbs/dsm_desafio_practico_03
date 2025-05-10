package edu.udb.dsm.desafio_practico_03.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.udb.dsm.desafio_practico_03.R
import edu.udb.dsm.desafio_practico_03.model.Recurso

class RecursoAdapter(
    private var recursos: List<Recurso> = emptyList(),
    private val onEditClick: (Recurso) -> Unit = {},
    private val onDeleteClick: (Recurso) -> Unit = {}
) : RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    class RecursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurso, parent, false)
        return RecursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = recursos[position]

        holder.tvTitulo.text = recurso.titulo
        holder.tvDescripcion.text = recurso.descripcion
        holder.tvTipo.text = "Tipo: ${recurso.tipo}"

        holder.btnEditar.setOnClickListener {
            onEditClick(recurso)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(recurso)
        }
    }

    override fun getItemCount(): Int = recursos.size

    fun updateRecursos(newRecursos: List<Recurso>) {
        recursos = newRecursos
        notifyDataSetChanged()
    }

    fun removeRecurso(recurso: Recurso) {
        val position = recursos.indexOf(recurso)
        if (position != -1) {
            (recursos as MutableList).removeAt(position)
            notifyItemRemoved(position)
        }
    }
}