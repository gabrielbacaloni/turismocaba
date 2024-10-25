package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginActivity : AppCompatActivity() {

    private lateinit var TurismoCABADBHelper: TurismoCABADBHelper
    private lateinit var lugaresAdapter: LugaresAdapterLogin
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

        // Configurar el nombre del usuario en la barra de navegación
        bottomNavigation.menu.findItem(R.id.navigation_perfil).title = nombreUsuario


        // Crear el listado de lugares con sus coordenadas
        val lugares = listOf(
            LugarTuristico(
                1,
                "Obelisco",
                "Descripción del Obelisco",
                "Ubicación del Obelisco",
                R.mipmap.ic_obelisco,
                -34.6037,
                -58.3816
            ),
            LugarTuristico(
                2,
                "San Telmo",
                "Descripción de San Telmo",
                "Ubicación de San Telmo",
                R.mipmap.ic_santelmo,
                -34.6212,
                -58.3731
            ),
            LugarTuristico(
                3,
                "Caminito",
                "Descripción de Caminito",
                "Ubicación de Caminito",
                R.mipmap.ic_caminito,
                -34.6345,
                -58.3630
            ),
            LugarTuristico(
                4,
                "Calle Corrientes",
                "Descripción de Calle Corrientes",
                "Ubicación de Calle Corrientes",
                R.mipmap.ic_callecorrientes,
                -34.6035,
                -58.3820
            ),
            LugarTuristico(
                5,
                "Congreso",
                "Descripción de Congreso",
                "Ubicación del Congreso",
                R.mipmap.ic_congreso,
                -34.6090,
                -58.3928
            ),
            LugarTuristico(
                6,
                "La Casa Rosada",
                "Descripción de La Casa Rosada",
                "Ubicación de La Casa Rosada",
                R.mipmap.ic_lacasarosada,
                -34.6083,
                -58.3708
            ),
            LugarTuristico(
                7,
                "Sede Boyacá Independiente",
                "La sede de Independiente ubicada en la calle Boyacá",
                "Boyacá, Buenos Aires, Argentina",
                R.mipmap.ic_sedeindependiente,
                -34.6066,
                -58.4513
            ),
            LugarTuristico(
                8,
                "Puerto Madero",
                "Descripción de Puerto Madero",
                "Ubicación de Puerto Madero",
                R.mipmap.ic_puertomadero,
                -34.6114,
                -58.3624
            )
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

    private fun configurarRecyclerView(
        recyclerView: RecyclerView,
        lugares: List<LugarTuristico>,
        idUsuario: Int
    ) {
        lugaresAdapter = LugaresAdapterLogin(
            idUsuario,
            TurismoCABADBHelper,
            lugares
        ) { lugar ->
            obtenerLugaresFavoritos(lugar, idUsuario)
        }

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
}
