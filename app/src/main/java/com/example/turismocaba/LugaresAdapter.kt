package com.example.turismocaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class MisLugaresAdapter(
    private val lugaresFavoritos: List<LugarTuristico>,
    private val onOpcionClick: (LugarTuristico, OpcionTipo) -> Unit,
    private val onQuitarFavoritoClick: (LugarTuristico) -> Unit
) : RecyclerView.Adapter<MisLugaresAdapter.MisLugaresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisLugaresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_lugar_favorito, parent, false)
        return MisLugaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: MisLugaresViewHolder, position: Int) {
        val lugar = lugaresFavoritos[position]

        // Configurar el nombre del lugar
        holder.tvNombreLugar.text = lugar.nombre

        // Cargar la imagen del lugar de forma circular con Glide usando el ID de recurso directamente
        Glide.with(holder.itemView.context)
            .load(lugar.imagen) // Usar directamente el ID del recurso de imagen
            .transform(CircleCrop()) // Aplicar la transformación circular
            .into(holder.imagenImageView)

        // Configurar el botón de quitar favorito
        holder.btnQuitarFavorito.setOnClickListener {
            onQuitarFavoritoClick(lugar)
        }

        // Configurar el carrusel de opciones
        val opcionesLugar = listOf(
            OpcionLugar("Ir a ubicación", R.drawable.ic_mapa, OpcionTipo.UBICACION),
            OpcionLugar("Sacar foto", R.drawable.ic_camera, OpcionTipo.FOTO),
            OpcionLugar("Abrir calendario", R.drawable.ic_calendar, OpcionTipo.CALENDARIO)
        )

        // Crear un adaptador para las opciones del lugar
        val opcionesAdapter = OpcionesLugarAdapter(opcionesLugar) { opcion ->
            onOpcionClick(lugar, opcion.tipo)
        }

        // Configurar el RecyclerView de las opciones
        holder.rvOpcionesLugar.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvOpcionesLugar.adapter = opcionesAdapter
    }

    override fun getItemCount(): Int {
        return lugaresFavoritos.size
    }

    // Clase ViewHolder para las vistas
    class MisLugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
        val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen)
        val rvOpcionesLugar: RecyclerView = itemView.findViewById(R.id.rvOpcionesLugar)
        val btnQuitarFavorito: ImageButton = itemView.findViewById(R.id.btnQuitarFavorito)
    }
}