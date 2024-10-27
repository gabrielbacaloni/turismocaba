package com.example.turismocaba

import android.net.Uri
import android.view.LayoutInflater
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource

class CarruselAdapter(fotos: List<Uri>) :
    RecyclerView.Adapter<CarruselAdapter.CarruselViewHolder>() {

    private val validFotos: List<Uri> = fotos.filter { it != null && it.toString().isNotEmpty() }

    init {
        Log.d("CarruselAdapter", "Tamaño de fotos: ${validFotos.size}")
        validFotos.forEachIndexed { index, uri ->
            Log.d("CarruselAdapter", "Foto en posición $index: $uri")
        }
    }

    class CarruselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivCarruselImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarruselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_carruzel_adapter, parent, false)
        return CarruselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarruselViewHolder, position: Int) {
        // Usar holder.adapterPosition para obtener la posición actual
        val currentPosition = holder.adapterPosition
        val imageUri = validFotos[currentPosition]
        Log.d("CarruselAdapter", "Cargando imagen en posición $currentPosition: $imageUri")

        if (imageUri != null && imageUri.toString().isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUri)
                .placeholder(R.drawable.ic_placeholder) // Placeholder mientras se carga
                .error(R.drawable.ic_image) // Imagen por defecto en caso de error
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("CarruselAdapter", "Error al cargar la imagen en posición $currentPosition", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(holder.imageView)
        } else {
            Log.e("CarruselAdapter", "La URI de la imagen en la posición $currentPosition es null o vacía")
            holder.imageView.setImageResource(R.drawable.ic_image) // Imagen por defecto
        }
    }

    override fun getItemCount(): Int = validFotos.size
}
