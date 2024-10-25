package com.example.turismocaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LugaresAdapterLogin(
    private val idUsuario: Int,
    private val turismoCABADBHelper: TurismoCABADBHelper,
    private val lugares: List<LugarTuristico>,
    private val onFavoritoClick: (LugarTuristico) -> Unit
) : RecyclerView.Adapter<LugaresAdapterLogin.LugaresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugaresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_lugar, parent, false)
        return LugaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugaresViewHolder, position: Int) {
        val lugar = lugares[position]
        holder.tvNombreLugar.text = lugar.nombre
        Glide.with(holder.itemView.context).load(lugar.imagen).into(holder.imagenImageView)

        // Comprobar si el lugar es favorito usando el helper y el ID del lugar
        val estaEnFavoritos = turismoCABADBHelper.verificarLugarEnFavoritos(idUsuario, lugar.id)

        // Establecer el icono de favorito adecuado
        holder.btnAgregarFavorito.setImageResource(
            if (estaEnFavoritos) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )

        holder.btnAgregarFavorito.setOnClickListener {
            if (estaEnFavoritos) {
                // Si es favorito, quitarlo usando el ID
                turismoCABADBHelper.eliminarLugarFavorito(idUsuario, lugar.id) // Llama a la función con ambos IDs
            } else {
                // Si no es favorito, agregarlo usando el ID
                turismoCABADBHelper.insertarLugarFavorito(idUsuario, lugar) // Método para agregar a favoritos
            }
            onFavoritoClick(lugar) // Notificar el cambio
            notifyItemChanged(position) // Notificar cambio para actualizar el icono
        }
    }

    override fun getItemCount(): Int {
        return lugares.size
    }

    inner class LugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
        val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen)
        val btnAgregarFavorito: ImageButton = itemView.findViewById(R.id.btnAgregarFavorito)
    }
}
