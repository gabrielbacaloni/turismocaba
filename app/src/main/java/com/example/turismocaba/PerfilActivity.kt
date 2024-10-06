package com.example.turismocaba

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Configurar BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Establecer el item seleccionado en la vista actual
        bottomNavigationView.selectedItemId = R.id.navigation_perfil

        // Manejar la selección de ítems en el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0) // Sin animación
                    true
                }
                R.id.navigation_mis_lugares -> {
                    val intent = Intent(this, MisLugaresActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_perfil -> {
                    // Ya estás en la pantalla de Perfil, no es necesario hacer nada
                    true
                }
                else -> false
            }
        }
    }
}
