package com.example.turismocaba

import android.Manifest
import android.os.Build
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Environment
import java.util.*
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat

class MisLugaresActivity : AppCompatActivity() {

    private var lugares: ArrayList<LugarTuristico> = arrayListOf()
    private lateinit var lugaresAdapter: MisLugaresAdapter
    private lateinit var dbHelper: TurismoCABADBHelper
    private var currentLugar: LugarTuristico? = null
    private var currentPhotoUri: Uri? = null

    // Lanzador para la captura de fotos
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

    // Constantes para el código de solicitud de permisos
    private companion object {
        const val REQUEST_CODE_PERMISSIONS = 100
        const val REQUEST_CODE_SELECT_IMAGE = 101
    }

    private lateinit var nombreUsuario: String // Variable para almacenar el nombre del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_lugares)

        // Obtener el nombre del usuario desde el Intent
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        // Configurar la navegación en la barra inferior
        setupBottomNavigation()

        dbHelper = TurismoCABADBHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMisLugares)

        // Obtener el ID del usuario logueado (puedes implementar el método obtenerIdUsuarioActual)
        val idUsuario = obtenerIdUsuarioActual()

        // Obtener los lugares favoritos del usuario desde la base de datos
        val lugaresFavoritos = dbHelper.obtenerLugaresFavoritos(idUsuario)
        lugares.addAll(lugaresFavoritos) // Añadir los lugares recuperados a la lista 'lugares'
        Log.d("MisLugares", "Lugares favoritos recuperados: ${lugares.size}") // Ahora mostrará el tamaño correcto

        // Configurar el RecyclerView
        configurarRecyclerView(recyclerView, lugares, idUsuario, TurismoCABADBHelper(this))

        // Inicializar el lanzador para la captura de fotos
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show()
                    mostrarCalendario() // Mostrar calendario después de capturar la foto
                }
            }
    }

    override fun onResume() {
        super.onResume()
        setupBottomNavigation()

        // Limpiar la lista de lugares antes de cargar nuevos datos
        lugares.clear()

        // Obtener el ID del usuario actual
        val idUsuario = obtenerIdUsuarioActual()

        // Recuperar todos los lugares favoritos del usuario
        val lugaresFavoritos = dbHelper.obtenerLugaresFavoritos(idUsuario)

        // Añadir los lugares favoritos a la lista 'lugares'
        lugares.addAll(lugaresFavoritos)

        // Notificar cambios al adaptador
        lugaresAdapter.notifyDataSetChanged()

        // Log para verificar el número de lugares favoritos recuperados
        Log.d("MisLugares", "Lugares favoritos recuperados: ${lugares.size}")
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.menu.findItem(R.id.navigation_perfil).title = nombreUsuario

        bottomNavigation.setOnItemSelectedListener { item ->
            val destinationActivity = when (item.itemId) {
                R.id.navigation_home -> LoginActivity::class.java
                R.id.navigation_perfil -> PerfilActivity::class.java
                else -> null // Asegúrate de que haya un valor por defecto
            }

            if (destinationActivity != null) {
                val intent = Intent(this, destinationActivity)
                intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                startActivity(intent)
                finish()
                true
            } else {
                Log.e("MisLugaresActivity", "Error: Activity de destino es null")
                false
            }
        }
    }

    private fun configurarRecyclerView(
        recyclerView: RecyclerView,
        lugaresFavoritos: List<LugarTuristico>,
        idUsuario:Int,
        turismoCABADBHelper: TurismoCABADBHelper
    ) {
        lugaresAdapter = MisLugaresAdapter(
            idUsuario, // ID del usuario
            turismoCABADBHelper, // Instancia de TurismoCABADBHelper
            lugaresFavoritos,
            onOpcionClick = { lugar, opcionTipo ->
                when (opcionTipo) {
                    OpcionTipo.UBICACION -> lugar.abrirEnGoogleMaps(this)
                    OpcionTipo.FOTO -> {
                        currentLugar = lugar
                        capturarFoto()
                    }
                    OpcionTipo.CALENDARIO -> {
                        currentLugar = lugar
                        mostrarCalendario()
                    }
                    OpcionTipo.SELECCIONAR_IMAGEN -> {
                        currentLugar = lugar
                        seleccionarImagen()
                    }
                }
            },
            onQuitarFavoritoClick = { lugar -> quitarFavorito(lugar) },
            esMisLugaresActivity = true // Indica que estamos en MisLugaresActivity
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    // Método para eliminar un lugar de favoritos
    private fun quitarFavorito(lugar: LugarTuristico) {
        val idUsuario = obtenerIdUsuarioActual() // Obtén el id del usuario
        val deletedRows = dbHelper.eliminarLugarFavorito(idUsuario, lugar.id) // Pasamos ambos IDs como Int

        if (deletedRows > 0) { // Verifica si se eliminó alguna fila
            Toast.makeText(this, "${lugar.nombre} ha sido eliminado de tus favoritos", Toast.LENGTH_SHORT).show()
            // ... resto del código ...
        } else {
            Toast.makeText(this, "Error al eliminar de favoritos",Toast.LENGTH_SHORT).show()
        }
    }

    // Resto de métodos como capturarFoto, mostrarCalendario, seleccionarImagen, etc.

    private fun capturarFoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
            return
        }

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    currentPhotoUri = imageUri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    takePictureLauncher.launch(takePictureIntent)
                } else {
                    Toast.makeText(this, "No se pudo guardar la imagen en la galería", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("MisLugaresActivity", "Error al abrir la cámara", e)
                Toast.makeText(this, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
            currentLugar?.let {
                // Asegúrate de tener el idUsuario disponible aquí
                val idUsuario = obtenerIdUsuarioActual() // Método que debes implementar para obtener el ID del usuario

                // Llama a guardarLugarEnBaseDeDatos pasando el idUsuario
                guardarLugarEnBaseDeDatos(idUsuario, it, currentPhotoUri?.toString(), fechaSeleccionada)
                Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT).show()
            }
        }, year, month, day)

        datePickerDialog.show()
    }


    private fun seleccionarImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_PERMISSIONS)
                return
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
                return
            }
        }
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    private fun guardarLugarEnBaseDeDatos(
        idUsuario: Int,  // Asegúrate de tener el ID del usuario disponible
        lugar: LugarTuristico,
        foto: String?,
        fechaVisita: String?
    ) {
        lugar.fechaVisita = fechaVisita // Actualiza la fecha de visita en el lugar

        val result = dbHelper.insertarLugarFavorito(idUsuario, lugar)
        if (result != -1L) {
            Toast.makeText(this, "Lugar guardado en favoritos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al guardar el lugar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerIdUsuarioActual(): Int {
        // Método que devuelve el ID del usuario actualmente logueado
        // Puedes obtener este valor desde SharedPreferences o desde el intent
        return 1 // Ejemplo, ajustar según tu lógica
    }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            currentPhotoUri = selectedImageUri // Guarda la URI seleccionada
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

}

