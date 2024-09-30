package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Inicializar las vistas
        val etEquipoFutbol: EditText = findViewById(R.id.etEquipoFutbol)
        val etLibroFavorito: EditText = findViewById(R.id.etLibroFavorito)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etNuevaContrasena: EditText = findViewById(R.id.etNuevaContrasena)
        val btnResetPassword: Button = findViewById(R.id.btnResetPassword)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val tvTurismoCABA: TextView = findViewById(R.id.tvTurismoCABA)

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
                // Lógica para restablecer la contraseña
                Toast.makeText(this, "Contraseña restablecida con éxito", Toast.LENGTH_SHORT).show()
                // Navegar de regreso al login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Configurar el botón de cancelar
        btnCancel.setOnClickListener {
            // Regresar a la pantalla anterior
            finish()
        }

        // Personalizar la interacción con otros elementos
        tvTurismoCABA.setOnClickListener {
            Toast.makeText(this, "Bienvenido a Turismo CABA", Toast.LENGTH_SHORT).show()
        }
    }
}