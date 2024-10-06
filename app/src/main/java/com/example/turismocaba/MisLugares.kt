package com.example.turismocaba

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MisLugaresActivity : AppCompatActivity() {

    private lateinit var lugaresAdapter: LugaresAdapter
    private lateinit var dbHelper: TurismoCABADBHelper
    private var currentLugar: LugarTuristico? = null
    private var currentPhotoUri: Uri? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_lugares)

        // Inicializar el DBHelper
        dbHelper = TurismoCABADBHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.rvMisLugares)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView) // Referencia al BottomNavigationView

        // Obtener la lista de lugares seleccionados
        val lugaresSeleccionados = intent.getParcelableArrayListExtra<LugarTuristico>("lugaresSeleccionados") ?: arrayListOf()

        // Configurar el RecyclerView
        configurarRecyclerView(recyclerView, lugaresSeleccionados)

        // Configurar el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_mis_lugares -> {
                    Toast.makeText(this, "Estás en Mis Lugares", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun configurarRecyclerView(recyclerView: RecyclerView, lugares: List<LugarTuristico>) {
        lugaresAdapter = LugaresAdapter(lugares) { lugar ->
            currentLugar = lugar
            capturarFoto(lugar) // Capturar foto al seleccionar un lugar
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun capturarFoto(lugar: LugarTuristico) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarCalendario() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth/${monthOfYear + 1}/$year"
            guardarLugarEnBaseDeDatos(currentLugar!!, currentPhotoUri?.toString(), fechaSeleccionada)
            Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT).show()
        }, year, month, day)

        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            currentPhotoUri = imageUri
            Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show()
            mostrarCalendario() // Mostrar calendario después de capturar la foto
        }
    }

    private fun guardarLugarEnBaseDeDatos(lugar: LugarTuristico, foto: String?, fechaVisita: String?) {
        val result = dbHelper.insertarLugarFavorito(lugar.nombre, foto, fechaVisita)
        if (result != -1L) {
            Toast.makeText(this, "Lugar guardado en favoritos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al guardar el lugar", Toast.LENGTH_SHORT).show()
        }
    }
}
