package com.example.turismocaba

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        val etNombre: EditText = findViewById(R.id.etNombre)
        val etApellido: EditText = findViewById(R.id.etApellido)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPais: EditText = findViewById(R.id.etPais)
        val etContrasena: EditText = findViewById(R.id.etContrasena)
        val etEquipoFutbol: EditText = findViewById(R.id.etEquipoFutbol)
        val etLibroFavorito: EditText = findViewById(R.id.etLibroFavorito)
        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val tvTurismoCABA: TextView = findViewById(R.id.tvTurismoCABA)

        // Inicializar la base de datos
        val dbHelper = TurismoCABADBHelper(this)

        // Configurar el botón de registrar
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pais = etPais.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val equipoFutbol = etEquipoFutbol.text.toString().trim()
            val libroFavorito = etLibroFavorito.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pais.isEmpty() || contrasena.isEmpty() || equipoFutbol.isEmpty() || libroFavorito.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Insertar el usuario en la base de datos usando el método insertarUsuario
                val newRowId = dbHelper.insertarUsuario(
                    name = nombre,
                    surname = apellido,
                    email = email,
                    country = pais,
                    password = contrasena,
                    favoriteTeam = equipoFutbol,
                    favoriteBook = libroFavorito
                )

                if (newRowId != -1L) {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    // Limpiar los campos después del registro exitoso
                    etNombre.text.clear()
                    etApellido.text.clear()
                    etEmail.text.clear()
                    etPais.text.clear()
                    etContrasena.text.clear()
                    etEquipoFutbol.text.clear()
                    etLibroFavorito.text.clear()
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar el botón de cancelar
        btnCancel.setOnClickListener {
            // Limpiar los campos de texto
            etNombre.text.clear()
            etApellido.text.clear()
            etEmail.text.clear()
            etPais.text.clear()
            etContrasena.text.clear()
            etEquipoFutbol.text.clear()
            etLibroFavorito.text.clear()

            Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
        }

        // Configurar interacción con el TextView tvTurismoCABA (opcional)
        tvTurismoCABA.setOnClickListener {
            Toast.makeText(this, "Bienvenido a Turismo CABA", Toast.LENGTH_SHORT).show()
        }
    }
}
