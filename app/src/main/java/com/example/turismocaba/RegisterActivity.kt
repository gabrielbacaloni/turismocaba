package com.example.turismocaba

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
        val ivBackArrow: ImageView = findViewById(R.id.ivBackArrow)

        // Cambiar el texto del botón cancelar a "Limpiar Filtros"
        btnCancel.text = "Limpiar Filtros"

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

            // Validar los campos
            if (!validarCampos(nombre, apellido, email, pais, contrasena, equipoFutbol, libroFavorito)) {
                return@setOnClickListener
            }

            // Comprobar si el email ya está registrado
            if (dbHelper.obtenerUsuarioPorEmail(email) != null) {
                Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertar el usuario en la base de datos
            val newRowId = dbHelper.insertarUsuario(
                nombre = nombre,
                apellido = apellido,
                email = email,
                country = pais,
                password = contrasena,
                favoriteTeam = equipoFutbol,
                favoriteBook = libroFavorito
            )

            if (newRowId != -1L) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                // Limpiar los campos después del registro exitoso
                limpiarCampos(etNombre, etApellido, etEmail, etPais, etContrasena, etEquipoFutbol, etLibroFavorito)
            } else {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el botón de limpiar filtros
        btnCancel.setOnClickListener {
            limpiarCampos(etNombre, etApellido, etEmail, etPais, etContrasena, etEquipoFutbol, etLibroFavorito)
            Toast.makeText(this, "Campos de texto limpiados", Toast.LENGTH_SHORT).show()
        }

        // Interacción opcional con el TextView
        tvTurismoCABA.setOnClickListener {
            Toast.makeText(this, "Bienvenido a Turismo CABA", Toast.LENGTH_SHORT).show()
        }

        // Manejar el botón de retroceso
        ivBackArrow.setOnClickListener {
            finish() // Cierra la actividad actual
        }

        // Crear el callback para el botón de retroceso en la acción del sistema
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // Cierra la actividad actual
            }
        }

        // Registrar el callback
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    // Función para limpiar los campos
    private fun limpiarCampos(
        etNombre: EditText, etApellido: EditText, etEmail: EditText, etPais: EditText,
        etContrasena: EditText, etEquipoFutbol: EditText, etLibroFavorito: EditText
    ) {
        etNombre.text.clear()
        etApellido.text.clear()
        etEmail.text.clear()
        etPais.text.clear()
        etContrasena.text.clear()
        etEquipoFutbol.text.clear()
        etLibroFavorito.text.clear()
    }

    // Función para validar los campos
    private fun validarCampos(
        nombre: String,
        apellido: String,
        email: String,
        pais: String,
        contrasena: String,
        equipoFutbol: String,
        libroFavorito: String
    ): Boolean {
        val regexLetras = Regex("^[a-zA-ZÀ-ÿ\\s]*$")
        if (!regexLetras.matches(nombre)) {
            Toast.makeText(this, "El nombre no puede contener números", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!regexLetras.matches(apellido)) {
            Toast.makeText(this, "El apellido no puede contener números", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!regexLetras.matches(pais)) {
            Toast.makeText(this, "El país no puede contener números", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un email válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || pais.isEmpty() ||
            contrasena.isEmpty() || equipoFutbol.isEmpty() || libroFavorito.isEmpty()
        ) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}

