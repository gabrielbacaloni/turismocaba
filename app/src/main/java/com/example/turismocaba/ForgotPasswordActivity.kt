package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    // Inicializar el helper de la base de datos
    private lateinit var dbHelper: TurismoCABADBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        dbHelper = TurismoCABADBHelper(this) // Inicializa el helper

        // Inicializar las vistas
        val etEquipoFutbol: EditText = findViewById(R.id.etEquipoFutbol)
        val etLibroFavorito: EditText = findViewById(R.id.etLibroFavorito)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etNuevaContrasena: EditText = findViewById(R.id.etNuevaContrasena)
        val btnResetPassword: Button = findViewById(R.id.btnResetPassword)
        val tvTurismoCABA: TextView = findViewById(R.id.tvTurismoCABA)
        val btnBack: ImageButton = findViewById(R.id.btnBack)

        // Configurar el botón de restablecer contraseña
        btnResetPassword.setOnClickListener {
            val equipoFutbol = etEquipoFutbol.text.toString().trim()
            val libroFavorito = etLibroFavorito.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val nuevaContrasena = etNuevaContrasena.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (equipoFutbol.isEmpty() || libroFavorito.isEmpty() || email.isEmpty() || nuevaContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, ingrese un correo válido", Toast.LENGTH_SHORT).show()
            } else {
                // Verificar si el usuario existe
                val usuario = dbHelper.obtenerUsuarioPorEmail(email)

                if (usuario != null) {

                    dbHelper.actualizarContrasena(usuario, nuevaContrasena) // Pasar el objeto 'usuario' completo
                    Toast.makeText(this, "Contraseña restablecida con éxito", Toast.LENGTH_SHORT).show()

                    // Redirigir a LoginActivity con el nombre del usuario
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        putExtra("NOMBRE_USUARIO", usuario.nombre)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar el botón de retroceso
        btnBack.setOnClickListener {
            finish()  // Regresar a la pantalla anterior
        }

        // Personalizar la interacción con otros elementos
        tvTurismoCABA.setOnClickListener {
            Toast.makeText(this, "Bienvenido a Turismo CABA", Toast.LENGTH_SHORT).show()
        }
    }
}
