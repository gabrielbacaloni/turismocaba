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

    private var idUsuario: Int = -1 // ID del usuario
    private lateinit var dbHelper: TurismoCABADBHelper // Instancia de la base de datos
    private lateinit var nombreUsuario: String // Variable para almacenar el nombre del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Obtener el nombre del usuario y el ID desde el Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario no encontrado"
        idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        Log.d("PerfilActivity", "ID del usuario: $idUsuario")

        // Inicializar el helper de la base de datos
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

        // Mostrar el nombre de usuario en el TextView
        tvNombreUsuario.text = nombreUsuario

        // Manejar el botón de Aceptar Cambios
        btnAceptarCambios.setOnClickListener {
            val contrasenaIngresada = etContrasenaActual.text.toString()
            val nuevaContrasena = etNuevaContrasena.text.toString()
            val respuesta1 = etRespuestaPregunta1.text.toString()
            val respuesta2 = etRespuestaPregunta2.text.toString()

            // Validar campos de entrada
            if (contrasenaIngresada.isNotEmpty() && nuevaContrasena.isNotEmpty()) {
                val usuario = dbHelper.obtenerUsuarioPorId(idUsuario)

                if (usuario != null) {
                    if (contrasenaIngresada == usuario.password) {
                        // Validar las respuestas de seguridad
                        if (respuesta1 == usuario.favoriteTeam && respuesta2 == usuario.favoriteBook) {
                            // Actualizar la contraseña
                            dbHelper.actualizarContrasena(usuario, nuevaContrasena)
                            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                            // Limpiar los campos
                            etContrasenaActual.text.clear()
                            etNuevaContrasena.text.clear()
                            etRespuestaPregunta1.text.clear()
                            etRespuestaPregunta2.text.clear()
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

        // Manejar el botón de Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show() // Mensaje de cierre de sesión
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_perfil

        // Actualizar el título de la opción de perfil
        bottomNavigationView.menu.findItem(R.id.navigation_perfil).title = nombreUsuario

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_mis_lugares -> {
                    val intent = Intent(this, MisLugaresActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_perfil -> true // Ya estás en la pantalla de Perfil
                else -> false
            }
        }
    }
}
