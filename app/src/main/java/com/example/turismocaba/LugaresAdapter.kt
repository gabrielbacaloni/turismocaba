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
    private val idUsuario: Int, // ID del usuario actual
    private val TurismoCABADBHelper: TurismoCABADBHelper,
    private val lugaresFavoritos: List<LugarTuristico>,
    private val onOpcionClick: (LugarTuristico, OpcionTipo) -> Unit,
    private val onQuitarFavoritoClick: (LugarTuristico) -> Unit,
    private val esMisLugaresActivity: Boolean
) : RecyclerView.Adapter<MisLugaresAdapter.MisLugaresViewHolder>() {

    private val lugaresMutable = lugaresFavoritos.toMutableList() // Copia mutable para la lógica de la estrella

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisLugaresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_lugar_favorito, parent, false)
        return MisLugaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: MisLugaresViewHolder, position: Int) {
        val lugar = lugaresMutable[position]

        // Configurar el nombre del lugar
        holder.tvNombreLugar.text = lugar.nombre

        // Cargar la imagen
        Glide.with(holder.itemView.context)
            .load(lugar.imagen)
            .transform(CircleCrop())
            .into(holder.imagenImageView)

        val estaEnFavoritos = TurismoCABADBHelper.verificarLugarEnFavoritos(idUsuario, lugar.id)

        if (estaEnFavoritos) {
            holder.btnQuitarFavorito.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            holder.btnQuitarFavorito.setImageResource(R.drawable.ic_favorite_border)
        }

        // Configurar el botón de quitar favorito
        holder.btnQuitarFavorito.setOnClickListener {
            if (esMisLugaresActivity) {
                onQuitarFavoritoClick(lugar)
                lugaresMutable.remove(lugar) // Eliminar de la lista mutable
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, lugaresMutable.size)
            }
        }


        // Configurar el carrusel de opciones (Fuera del setOnClickListener)
        val opcionesLugar = listOf(
            OpcionLugar("Ir a ubicación", R.drawable.ic_mapa, OpcionTipo.UBICACION),
            OpcionLugar("Sacar foto", R.drawable.ic_camera, OpcionTipo.FOTO),
            OpcionLugar("Abrir calendario", R.drawable.ic_calendar, OpcionTipo.CALENDARIO)
        )
        val opcionesAdapter = OpcionesLugarAdapter(opcionesLugar) { opcion ->
            onOpcionClick(lugar, opcion.tipo)
        }
        holder.rvOpcionesLugar.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvOpcionesLugar.adapter = opcionesAdapter
    }

    override fun getItemCount(): Int {
        return lugaresMutable.size // Usar la lista mutable
    }

    class MisLugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
        val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen)
        val rvOpcionesLugar: RecyclerView = itemView.findViewById(R.id.rvOpcionesLugar)
        val btnQuitarFavorito: ImageButton = itemView.findViewById(R.id.btnQuitarFavorito)
    }
}