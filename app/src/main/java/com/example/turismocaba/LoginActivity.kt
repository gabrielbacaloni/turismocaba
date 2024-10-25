package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.turismocaba.MisLugaresAdapter.MisLugaresViewHolder
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginActivity : AppCompatActivity() {

    private lateinit var TurismoCABADBHelper: TurismoCABADBHelper
    private lateinit var lugaresAdapter: MisLugaresAdapter
    private val lugaresFavoritos = mutableListOf<LugarTuristico>()  // Lista mutable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val recyclerView: RecyclerView = findViewById(R.id.rvLugares)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        TurismoCABADBHelper = TurismoCABADBHelper(this)

        // Recibir el nombre del usuario desde el Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Perfil"
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        sharedPreferences.edit().putString("NOMBRE_USUARIO", nombreUsuario).apply()

        // Obtener el ID del usuario desde SharedPreferences
        val idUsuario = sharedPreferences.getInt("ID_USUARIO", -1) // Recuperar el ID del usuario

        // Log para verificar el ID del usuario
        Log.d("LoginActivity", "ID del usuario: $idUsuario")

        // Obtener el usuario desde la base de datos usando el ID
        val dbHelper = TurismoCABADBHelper(this)
        val usuario = dbHelper.obtenerUsuarioPorId(idUsuario) // Asegúrate de que este método esté bien implementado

        // Configurar el nombre del usuario en la barra de navegación
        bottomNavigation.menu.findItem(R.id.navigation_perfil).title = nombreUsuario

        // Crear el listado de lugares con sus coordenadas
        val lugares = listOf(
            LugarTuristico(1, "Obelisco", "Descripción del Obelisco", "Ubicación del Obelisco", R.mipmap.ic_obelisco, -34.6037, -58.3816),
            LugarTuristico(2, "San Telmo", "Descripción de San Telmo", "Ubicación de San Telmo", R.mipmap.ic_santelmo, -34.6212, -58.3731),
            LugarTuristico(3, "Caminito", "Descripción de Caminito", "Ubicación de Caminito", R.mipmap.ic_caminito, -34.6345, -58.3630),
            LugarTuristico(4, "Calle Corrientes", "Descripción de Calle Corrientes", "Ubicación de Calle Corrientes", R.mipmap.ic_callecorrientes, -34.6035, -58.3820),
            LugarTuristico(5, "Congreso", "Descripción de Congreso", "Ubicación del Congreso", R.mipmap.ic_congreso, -34.6090, -58.3928),
            LugarTuristico(6, "La Casa Rosada", "Descripción de La Casa Rosada", "Ubicación de La Casa Rosada", R.mipmap.ic_lacasarosada, -34.6083, -58.3708),
            LugarTuristico(7, "Sede Boyacá Independiente", "La sede de Independiente ubicada en la calle Boyacá", "Boyacá, Buenos Aires, Argentina", R.mipmap.ic_sedeindependiente, -34.6066, -58.4513),
            LugarTuristico(8, "Puerto Madero", "Descripción de Puerto Madero", "Ubicación de Puerto Madero", R.mipmap.ic_puertomadero, -34.6114, -58.3624)
        )
        // Cargar los lugares favoritos desde la base de datos
        cargarLugaresFavoritos(idUsuario)

        // Configurar RecyclerView
        configurarRecyclerView(recyclerView, lugares, idUsuario)

        // Configurar el BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Estás en Home", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.navigation_mis_lugares -> {
                    val intent = Intent(this, MisLugaresActivity::class.java).apply {
                        putExtra(
                            "NOMBRE_USUARIO",
                            nombreUsuario
                        ) // Pasar el nombre del usuario a MisLugaresActivity
                        putParcelableArrayListExtra(
                            "lugaresSeleccionados",
                            ArrayList(lugaresFavoritos)
                        )
                    }
                    startActivity(intent)
                    true
                }

                R.id.navigation_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java).apply {
                        putExtra(
                            "NOMBRE_USUARIO",
                            nombreUsuario
                        ) // Pasar el nombre del usuario a PerfilActivity
                        putExtra("ID_USUARIO", idUsuario) // Pasar el ID del usuario
                    }
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Obtener el nombre del usuario desde SharedPreferences
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("NOMBRE_USUARIO", "Usuario") ?: "Usuario"

        // Actualizar el nombre del usuario en la barra de navegación
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.menu.findItem(R.id.navigation_perfil).title = nombreUsuario
    }

    private fun configurarRecyclerView(
        recyclerView: RecyclerView,
        lugares: List<LugarTuristico>,
        idUsuario: Int
    ) {
        lugaresAdapter = MisLugaresAdapter(
            idUsuario,
            TurismoCABADBHelper,
            lugares,
            { lugar, opcionTipo ->
                // No se hace nada con las opciones en LoginActivity
            },
            { lugar ->
                // No se hace nada con "Quitar favorito" en LoginActivity
            }, false // esMisLugaresActivity (false porque estamos en LoginActivity)
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun obtenerLugaresFavoritos(lugar: LugarTuristico, idUsuario: Int) {
        if (idUsuario != -1) {
            val lugarYaEnFavoritos =
                TurismoCABADBHelper.verificarLugarEnFavoritos(idUsuario, lugar.id)

            if (!lugarYaEnFavoritos) {
                lugaresFavoritos.add(lugar)
                lugaresAdapter.notifyDataSetChanged()

                val lugarConFecha = lugar.copy(
                    fechaVisita = lugar.fechaVisita ?: System.currentTimeMillis().toString()
                )
                TurismoCABADBHelper.insertarLugarFavorito(idUsuario, lugarConFecha)

                Toast.makeText(this, "${lugar.nombre} agregado a Mis Lugares", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "${lugar.nombre} ya está en Mis Lugares", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(
                this,
                "Error al guardar favorito. ID de usuario no encontrado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun cargarLugaresFavoritos(idUsuario: Int) {
        if (idUsuario != -1) {
            lugaresFavoritos.clear()
            lugaresFavoritos.addAll(TurismoCABADBHelper.obtenerLugaresFavoritos(idUsuario))
        } else {
            Toast.makeText(
                this,
                "Error al cargar lugares favoritos. ID de usuario no encontrado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    class MisLugaresAdapter(
        private val idUsuario: Int,
        private val TurismoCABADBHelper: TurismoCABADBHelper,
        private val lugaresFavoritos: List<LugarTuristico>,
        private val onOpcionClick: (LugarTuristico, OpcionTipo) -> Unit,
        private val onQuitarFavoritoClick: (LugarTuristico) -> Unit,
        private val esMisLugaresActivity: Boolean
    ) : RecyclerView.Adapter<MisLugaresAdapter.MisLugaresViewHolder>() {

        private val lugaresMutable = lugaresFavoritos.toMutableList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MisLugaresViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.item_lugar_favorito,
                parent,
                false
            ) // Usar el layout correcto para LoginActivity
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

            // Mostrar el botón de favoritos y configurar su estado
            holder.btnQuitarFavorito.visibility =
                View.VISIBLE // Asegurarse de que el botón sea visible
            holder.btnQuitarFavorito.setImageResource(if (estaEnFavoritos) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)

            // Manejar el click en el botón de favoritos
            holder.btnQuitarFavorito.setOnClickListener {
                if (estaEnFavoritos) {
                    // Quitar de favoritos
                    TurismoCABADBHelper.eliminarLugarFavorito(idUsuario, lugar.id)
                    lugaresMutable.remove(lugar)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, lugaresMutable.size)
                } else {
                    // Agregar a favoritos
                    TurismoCABADBHelper.insertarLugarFavorito(
                        idUsuario,
                        lugar.copy(fechaVisita = System.currentTimeMillis().toString())
                    )
                    lugaresMutable.add(lugar)
                    notifyItemInserted(lugaresMutable.size - 1)
                }

                // Actualizar el icono del botón
                holder.btnQuitarFavorito.setImageResource(
                    if (TurismoCABADBHelper.verificarLugarEnFavoritos(
                            idUsuario,
                            lugar.id
                        )
                    ) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }
        }


        override fun getItemCount(): Int {
            return lugaresMutable.size
        }

        class MisLugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNombreLugar: TextView = itemView.findViewById(R.id.tvNombreLugar)
            val imagenImageView: ImageView = itemView.findViewById(R.id.ivImagen)
            val btnQuitarFavorito: ImageButton = itemView.findViewById(R.id.btnQuitarFavorito)
        }
    }
}