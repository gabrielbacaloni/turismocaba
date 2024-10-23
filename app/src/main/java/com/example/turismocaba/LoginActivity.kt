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
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginActivity : AppCompatActivity() {

    private lateinit var lugaresAdapter: LugaresAdapter
    private val lugaresFavoritos = mutableListOf<LugarTuristico>()  // Lista mutable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val recyclerView: RecyclerView = findViewById(R.id.rvLugares)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

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
        cargarLugaresFavoritos()

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
                        putExtra("NOMBRE_USUARIO", nombreUsuario) // Pasar el nombre del usuario a MisLugaresActivity
                        putParcelableArrayListExtra("lugaresSeleccionados", ArrayList(lugaresFavoritos))
                    }
                    startActivity(intent)
                    true
                }
                R.id.navigation_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java).apply {
                        putExtra("NOMBRE_USUARIO", nombreUsuario) // Pasar el nombre del usuario a PerfilActivity
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

    private fun configurarRecyclerView(recyclerView: RecyclerView, lugares: List<LugarTuristico>) {
        lugaresAdapter = LugaresAdapter(lugares, lugaresFavoritos) { lugarFavorito ->
            agregarLugarAFavoritos(lugarFavorito)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun agregarLugarAFavoritos(lugar: LugarTuristico) {
        if (!lugaresFavoritos.contains(lugar)) {
            // Agregar lugar a la lista mutable
            lugaresFavoritos.add(lugar)

            // Notificar al adaptador que los datos han cambiado
            lugaresAdapter.notifyDataSetChanged() // Usar notifyDataSetChanged() en lugar de notifyItemChanged()

            // Guardar lugar en la base de datos
            val dbHelper = TurismoCABADBHelper(this)

            // Asignar la fecha de visita si no está presente (usando la fecha y hora actual)
            val lugarConFecha = lugar.copy(fechaVisita = lugar.fechaVisita ?: System.currentTimeMillis().toString())

            // Llamar al método de la base de datos para insertar el lugar con fecha de visita
            dbHelper.insertarLugarFavorito(lugarConFecha)

            Toast.makeText(this, "${lugar.nombre} agregado a Mis Lugares", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "${lugar.nombre} ya está en Mis Lugares", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarLugaresFavoritos() {
        val dbHelper = TurismoCABADBHelper(this)
        lugaresFavoritos.clear()
        lugaresFavoritos.addAll(dbHelper.obtenerTodosLosLugaresFavoritos())  // Método para obtener favoritos desde la DB
    }

    class LugaresAdapter(
        private val lugares: List<LugarTuristico>,
        private val lugaresFavoritos: MutableList<LugarTuristico>,  // Lista mutable para que se pueda modificar
        private val onLugarFavoritoClick: (LugarTuristico) -> Unit
    ) : RecyclerView.Adapter<LugaresAdapter.LugaresViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugaresViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_lugar, parent, false)
            return LugaresViewHolder(view)
        }

        override fun onBindViewHolder(holder: LugaresViewHolder, position: Int) {
            val lugar = lugares[position]

            holder.tvNombreLugar.text = lugar.nombre

            Glide.with(holder.itemView.context)
                .load(lugar.imagen)  // Usar directamente el ID de recurso de la imagen
                .into(holder.imagenImageView)

            holder.btnAgregarFavorito.setImageResource(
                if (lugaresFavoritos.contains(lugar)) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )

            holder.btnAgregarFavorito.setOnClickListener {
                onLugarFavoritoClick(lugar)

                // Cambiar el icono del botón según si el lugar está o no en favoritos
                holder.btnAgregarFavorito.setImageResource(
                    if (lugaresFavoritos.contains(lugar)) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
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
