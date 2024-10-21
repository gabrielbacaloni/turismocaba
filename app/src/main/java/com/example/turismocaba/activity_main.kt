package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: TurismoCABADBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar las vistas
        val etUsername: EditText = findViewById(R.id.etUsername)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val btnForgotPassword: Button = findViewById(R.id.btnForgotPassword)
        val tvTurismoCABA: TextView = findViewById(R.id.tvTurismoCABA)

        // Inicializar la base de datos
        dbHelper = TurismoCABADBHelper(this)

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar las credenciales en la base de datos
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM ${TurismoCABADBHelper.TABLE_USERS} WHERE ${TurismoCABADBHelper.COLUMN_EMAIL} = ? AND ${TurismoCABADBHelper.COLUMN_PASSWORD} = ?"
            val cursor = db.rawQuery(query, arrayOf(email, password))

            if (cursor.moveToFirst()) {
                // Iniciar sesión exitoso
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                // Obtener el nombre del usuario desde la base de datos
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(TurismoCABADBHelper.COLUMN_NOMBRE))

                // Redirigir a LoginActivity con el nombre del usuario
                val intent = Intent(this, LoginActivity::class.java).apply {
                    putExtra("NOMBRE_USUARIO", nombreUsuario)  // Pasar el nombre de usuario
                }
                startActivity(intent)
                finish() // Finalizar la actividad actual para que no se pueda volver a ella
            } else {
                // Fallo de inicio de sesión
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        // Configurar el botón de registro
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Configurar el botón de "Olvidé Contraseña"
        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Personalizar la interacción con otros elementos
        tvTurismoCABA.setOnClickListener {
            Toast.makeText(this, "Bienvenido a Turismo CABA", Toast.LENGTH_SHORT).show()
        }
    }
}
