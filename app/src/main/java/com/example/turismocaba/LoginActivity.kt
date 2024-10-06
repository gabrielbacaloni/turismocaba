package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView


class LoginActivity : AppCompatActivity() {

    private lateinit var lugaresAdapter: LugaresAdapter
    private val lugaresFavoritos = mutableListOf<LugarTuristico>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        val recyclerView: RecyclerView = findViewById(R.id.rvLugares)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Crear el listado de lugares con sus coordenadas
        val lugares = listOf(
            LugarTuristico(1, "Obelisco", "Descripción del Obelisco", "Ubicación del Obelisco", R.drawable.ic_obelisco_background, -34.6037, -58.3816),
            LugarTuristico(2, "San Telmo", "Descripción de San Telmo", "Ubicación de San Telmo", R.drawable.ic_santelmo_background, -34.6212, -58.3731),
            LugarTuristico(3, "Caminito", "Descripción de Caminito", "Ubicación de Caminito", R.drawable.ic_caminito_background, -34.6345, -58.3630),
            LugarTuristico(4, "Calle Corrientes", "Descripción de Calle Corrientes", "Ubicación de Calle Corrientes", R.drawable.ic_callecorrientes_background, -34.6035, -58.3820),
            LugarTuristico(5, "Congreso", "Descripción de Congreso", "Ubicación del Congreso", R.drawable.ic_congreso_background, -34.6090, -58.3928),
            LugarTuristico(6, "La Casa Rosada", "Descripción de La Casa Rosada", "Ubicación de La Casa Rosada", R.drawable.ic_lacasarosada_background, -34.6083, -58.3708),
            LugarTuristico(7, "Sede Boyacá Independiente", "La sede de Independiente ubicada en la calle Boyacá", "Boyacá, Buenos Aires, Argentina", R.drawable.ic_sedeindependiente_background, -34.6066, -58.4513),
            LugarTuristico(8, "Puerto Madero", "Descripción de Puerto Madero", "Ubicación de Puerto Madero", R.drawable.ic_puertomadero_background, -34.6114, -58.3624),
        )

        // Configurar RecyclerView
        configurarRecyclerView(recyclerView, lugares)

        // Configurar el BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Estás en Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_mis_lugares -> {
                    val intent = Intent(this, MisLugaresActivity::class.java).apply {
                        putParcelableArrayListExtra("lugaresSeleccionados", ArrayList(lugaresFavoritos))
                    }
                    startActivity(intent)
                    true
                }
                R.id.navigation_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun configurarRecyclerView(recyclerView: RecyclerView, lugares: List<LugarTuristico>) {
        lugaresAdapter = LugaresAdapter(lugares) { lugarFavorito ->
            agregarLugarAFavoritos(lugarFavorito)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun agregarLugarAFavoritos(lugar: LugarTuristico) {
        if (!lugaresFavoritos.contains(lugar)) {
            lugaresFavoritos.add(lugar)
            Toast.makeText(this, "${lugar.nombre} agregado a Mis Lugares", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "${lugar.nombre} ya está en Mis Lugares", Toast.LENGTH_SHORT).show()
        }
    }

    // Adapter modificado para usar Glide
    class LugaresAdapter(
        private val lugares: List<LugarTuristico>,
        private val onLugarFavoritoClick: (LugarTuristico) -> Unit
    ) : RecyclerView.Adapter<LugaresAdapter.LugaresViewHolder>() {

        private val lugaresFavoritos = mutableListOf<LugarTuristico>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugaresViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_lugar, parent, false)
            return LugaresViewHolder(view)
        }

        override fun onBindViewHolder(holder: LugaresViewHolder, position: Int) {
            val lugar = lugares[position]

            // Configurar el nombre del lugar
            holder.tvNombreLugar.text = lugar.nombre

            // Usar Glide para cargar la imagen del lugar
            Glide.with(holder.itemView.context)
                .load(lugar.imagen) // Cargar la imagen como recurso local
                .into(holder.imagenImageView)

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
        }

        override fun getItemCount(): Int {
            return lugares.size
        }

        class LugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
            val btnAgregarFavorito: ImageButton = itemView.findViewById(R.id.btnAgregarFavorito)
            val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen)
        }
    }
}
