package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilActivity : AppCompatActivity() {

    private var contrasenaActual = "password123" // Simulación de la contraseña actual
    private var preguntaSeguridad1 = "¿Tu primera mascota?" // Pregunta de seguridad actual
    private var preguntaSeguridad2 = "¿Tu lugar de nacimiento?" // Pregunta de seguridad actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Configurar BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_perfil

        // Obtener el nombre del usuario desde el Intent
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario no encontrado"

        // Actualizar el título de la opción de perfil
        bottomNavigationView.menu.findItem(R.id.navigation_perfil).title = nombreUsuario

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Pasar el nombre de usuario a LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    startActivity(intent)
                    finish() // Opcional: cerrar la actividad actual
                    true
                }
                R.id.navigation_mis_lugares -> {
                    // Pasar el nombre de usuario a MisLugaresActivity
                    val intent = Intent(this, MisLugaresActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    startActivity(intent)
                    finish() // Opcional: cerrar la actividad actual
                    true
                }
                R.id.navigation_perfil -> true // Ya estás en la pantalla de Perfil
                else -> false
            }
        }


        // Obtener referencias a los elementos de la UI
        val etContrasenaActual: EditText = findViewById(R.id.etContrasenaActual)
        val etNuevaContrasena: EditText = findViewById(R.id.etNuevaContrasena)
        val etPreguntaSeguridad1: EditText = findViewById(R.id.etPreguntaSeguridad1)
        val etPreguntaSeguridad2: EditText = findViewById(R.id.etPreguntaSeguridad2)
        val btnAceptarCambios: Button = findViewById(R.id.btnAceptarCambios)
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)
        val tvNombreUsuario: TextView = findViewById(R.id.tvNombreUsuario)

        // Mostrar el nombre de usuario en el TextView
        tvNombreUsuario.text = nombreUsuario

        // Manejar el botón de Aceptar Cambios
        btnAceptarCambios.setOnClickListener {
            val contrasenaIngresada = etContrasenaActual.text.toString()
            val nuevaContrasena = etNuevaContrasena.text.toString()
            val nuevaPreguntaSeguridad1 = etPreguntaSeguridad1.text.toString()
            val nuevaPreguntaSeguridad2 = etPreguntaSeguridad2.text.toString()

            // Validar campos de entrada
            if (contrasenaIngresada.isNotEmpty() && nuevaContrasena.isNotEmpty()) {
                if (contrasenaIngresada == contrasenaActual) {
                    // Actualizar la contraseña y las preguntas de seguridad
                    contrasenaActual = nuevaContrasena
                    preguntaSeguridad1 = nuevaPreguntaSeguridad1
                    preguntaSeguridad2 = nuevaPreguntaSeguridad2
                    Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Manejar el botón de Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            // Limpiar datos de sesión (si corresponde) aquí
            // Redirigir a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual
        }
    }

    // Método para obtener el nombre del usuario utilizando TurismoCABADBHelper
    private fun obtenerNombreUsuario(id: Int): String {
        val dbHelper = TurismoCABADBHelper(this)
        return dbHelper.obtenerNombreUsuario(id).also { dbHelper.close() } // Asegúrate de cerrar la conexión
    }
}
