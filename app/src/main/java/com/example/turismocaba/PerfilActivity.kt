package com.example.turismocaba

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilActivity : AppCompatActivity() {
    private var idUsuario: Int = -1
    private lateinit var dbHelper: TurismoCABADBHelper
    private lateinit var nombreUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Leer nombre del Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario no encontrado"

        // Leer ID del usuario desde SharedPreferences
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        idUsuario = sharedPreferences.getInt("ID_USUARIO", -1)
        Log.d("PerfilActivity", "ID del usuario: $idUsuario")


        dbHelper = TurismoCABADBHelper(this)

        // Configurar BottomNavigationView
        setupBottomNavigation()

        // Obtener referencias a los elementos de la UI
        val etContrasenaActual: EditText = findViewById(R.id.etContrasenaActual)
        val etNuevaContrasena: EditText = findViewById(R.id.etNuevaContrasena)
        val etRespuestaPregunta1: EditText = findViewById(R.id.etRespuestaPregunta1)
        val etRespuestaPregunta2: EditText = findViewById(R.id.etRespuestaPregunta2)
        val btnAceptarCambios: Button = findViewById(R.id.btnAceptarCambios)
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)
        val tvNombreUsuario: TextView = findViewById(R.id.tvNombreUsuario)

        tvNombreUsuario.text = nombreUsuario

        btnAceptarCambios.setOnClickListener {
            val contrasenaIngresada = etContrasenaActual.text.toString()
            val nuevaContrasena = etNuevaContrasena.text.toString()
            val respuesta1 = etRespuestaPregunta1.text.toString()
            val respuesta2 = etRespuestaPregunta2.text.toString()

            if (contrasenaIngresada.isNotEmpty() && nuevaContrasena.isNotEmpty()) {
                val usuario = dbHelper.obtenerUsuarioPorId(idUsuario)

                if (usuario != null) {
                    if (contrasenaIngresada == usuario.password) {
                        if (respuesta1 == usuario.favoriteTeam && respuesta2 == usuario.favoriteBook) {
                            dbHelper.actualizarContrasena(usuario, nuevaContrasena)
                            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                            clearFields(etContrasenaActual, etNuevaContrasena, etRespuestaPregunta1, etRespuestaPregunta2)
                        } else {
                            Toast.makeText(this, "Respuestas de seguridad incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrarSesion.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun clearFields(vararg editTexts: EditText) {
        editTexts.forEach { it.text.clear() }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_perfil
        bottomNavigationView.menu.findItem(R.id.navigation_perfil).title = nombreUsuario

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        putExtra("NOMBRE_USUARIO", nombreUsuario)
                    })
                    finish()
                    true
                }
                R.id.navigation_mis_lugares -> {
                    startActivity(Intent(this, MisLugaresActivity::class.java).apply {
                        putExtra("NOMBRE_USUARIO", nombreUsuario)
                    })
                    finish()
                    true
                }
                R.id.navigation_perfil -> true
                else -> false
            }
        }
    }
}

