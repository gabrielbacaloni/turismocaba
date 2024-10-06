package com.example.turismocaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.bumptech.glide.Glide

class LugaresAdapter(
    private val lugares: List<LugarTuristico>, // Lista de lugares turísticos
    private val onLugarFavoritoClick: (LugarTuristico) -> Unit // Callback para manejar clic en "Favoritos"
) : RecyclerView.Adapter<LugaresAdapter.LugaresViewHolder>() {

    private val lugaresFavoritos = mutableListOf<LugarTuristico>() // Lista para almacenar los lugares favoritos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugaresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_lugar, parent, false)
        return LugaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugaresViewHolder, position: Int) {
        val lugar = lugares[position]

        // Configurar el nombre del lugar
        holder.tvNombreLugar.text = lugar.nombre

        // Configurar el botón de favoritos (estrella)
        holder.btnAgregarFavorito.setImageResource(
            if (lugaresFavoritos.contains(lugar)) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )

        holder.btnAgregarFavorito.setOnClickListener {
            if (lugaresFavoritos.contains(lugar)) {
                lugaresFavoritos.remove(lugar)
            } else {
                lugaresFavoritos.add(lugar)
            }

            // Cambiar el icono de la estrella según si es favorito o no
            holder.btnAgregarFavorito.setImageResource(
                if (lugaresFavoritos.contains(lugar)) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )

            // Llamar al callback para agregar el lugar a "Mis Lugares"
            onLugarFavoritoClick(lugar)
        }

        // Mostrar la imagen del lugar usando Glide
        Log.d("LugaresAdapter", "Imagen asignada: ${lugar.imagen}")

        Glide.with(holder.itemView.context)
            .load(lugar.imagen)  // Cargar la imagen (asumiendo que lugar.imagen es un recurso o URL)
            .placeholder(R.drawable.placeholder_image) // Imagen de carga
            .error(R.drawable.error_image) // Imagen de error
            .into(holder.imagenImageView) // Carga la imagen en el ImageView
    }

    override fun getItemCount(): Int {
        return lugares.size
    }

    // Clase ViewHolder para las vistas
    class LugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
        val btnAgregarFavorito: ImageButton = itemView.findViewById(R.id.btnAgregarFavorito) // Referencia al botón de favoritos
        val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen) // Imagen del lugar
    }
}
