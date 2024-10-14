package com.example.turismocaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OpcionesLugarAdapter(
    private val opciones: List<OpcionLugar>,
    private val onOpcionClick: (OpcionLugar) -> Unit // Callback para manejar clic en una opción
) : RecyclerView.Adapter<OpcionesLugarAdapter.OpcionesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpcionesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_opcion_lugar, parent, false)
        return OpcionesViewHolder(view)
    }

    override fun onBindViewHolder(holder: OpcionesViewHolder, position: Int) {
        val opcion = opciones[position]
        holder.tvOpcionNombre.text = opcion.nombre
        holder.ivOpcionIcono.setImageResource(opcion.icono)

        holder.itemView.setOnClickListener {
            onOpcionClick(opcion) // Llamar el callback cuando se presiona una opción
        }
    }

    override fun getItemCount() = opciones.size

    class OpcionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivOpcionIcono: ImageView = itemView.findViewById(R.id.ivOpcionIcono) // Icono de la opción
        val tvOpcionNombre: TextView = itemView.findViewById(R.id.tvOpcionNombre) // Nombre de la opción
    }
}
