package com.example.turismocaba

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoginActivity : AppCompatActivity() {

    private lateinit var lugaresAdapter: LugaresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        val recyclerView: RecyclerView = findViewById(R.id.rvLugares)
        val btnPerfil: Button = findViewById(R.id.btnPerfil)
        val btnHome: Button = findViewById(R.id.btnHome)
        val btnMisLugares: Button = findViewById(R.id.btnMisLugares)

        // Crear el listado de lugares con sus coordenadas (latitud y longitud)
        val lugares = listOf(
            LugarTuristico("Obelisco", -34.6037, -58.3816),
            LugarTuristico("San Telmo", -34.6212, -58.3731),
            LugarTuristico("Caminito", -34.6345, -58.3630),
            LugarTuristico("Calle Corrientes", -34.6035, -58.3820),
            LugarTuristico("Congreso", -34.6090, -58.3928),
            LugarTuristico("La Casa Rosada", -34.6083, -58.3708),
            LugarTuristico("Recoleta", -34.5886, -58.3974),
            LugarTuristico("Puerto Madero", -34.6114, -58.3624),
            LugarTuristico("Palermo", -34.5807, -58.4260),
            LugarTuristico("Jardín Botánico", -34.5857, -58.4206),
            LugarTuristico("Teatro Colón", -34.6012, -58.3832),
            LugarTuristico("Plaza de Mayo", -34.6081, -58.3702),
            LugarTuristico("El Ateneo", -34.5968, -58.3929),
            LugarTuristico("Planetario", -34.5686, -58.4116),
            LugarTuristico("El Rosedal", -34.5743, -58.4117)
        )

        configurarRecyclerView(recyclerView, lugares)
        configurarBotones(btnPerfil, btnHome, btnMisLugares)
    }

    private fun configurarRecyclerView(recyclerView: RecyclerView, lugares: List<LugarTuristico>) {
        // Pasar la lista de lugares y el evento de clic
        lugaresAdapter = LugaresAdapter(lugares) { lugar ->
            // Intent para abrir Google Maps
            val uri = Uri.parse("geo:${lugar.latitud},${lugar.longitud}?q=${Uri.encode(lugar.nombre)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Intent sin setPackage, para dejar que el sistema elija la app adecuada
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se pudo abrir Google Maps", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun configurarBotones(btnPerfil: Button, btnHome: Button, btnMisLugares: Button) {
        // Configurar el botón de perfil
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        // Botón de Home
        btnHome.setOnClickListener {
            Toast.makeText(this, "Estás en Home", Toast.LENGTH_SHORT).show()
        }

        // Botón de "Mis Lugares"
        btnMisLugares.setOnClickListener {
            val lugaresSeleccionados = lugaresAdapter.getLugaresSeleccionados()
            if (lugaresSeleccionados.isNotEmpty()) {
                val intent = Intent(this, MisLugaresActivity::class.java).apply {
                    putExtra("lugaresSeleccionados", ArrayList(lugaresSeleccionados))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay lugares seleccionados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
