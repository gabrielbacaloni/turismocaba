package com.example.turismocaba

import android.Manifest
import android.os.Build
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MisLugaresActivity : AppCompatActivity() {

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

        val recyclerView: RecyclerView = findViewById(R.id.rvMisLugares)

        // Obtener la lista de lugares seleccionados desde el Intent
        val lugaresSeleccionados: ArrayList<LugarTuristico> =
            intent.extras?.getParcelableArrayList("lugaresSeleccionados") ?: arrayListOf()

        Log.d("MisLugaresActivity", "Lugares seleccionados: $lugaresSeleccionados")

        // Configurar el RecyclerView
        configurarRecyclerView(recyclerView, lugaresSeleccionados)

        // Inicializar el lanzador para la captura de fotos
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show()
                    mostrarCalendario() // Mostrar calendario después de capturar la foto
                }
            }

        // Verificar y solicitar permisos para acceder a la cámara y fotos
        verificarPermisos()
    }

    override fun onResume() {
        super.onResume()
        setupBottomNavigation() // Configurar la navegación en la barra inferior al reanudar la actividad
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

            // Verifica que destinationActivity no sea null antes de crear el Intent
            if (destinationActivity != null) {
                val intent = Intent(this, destinationActivity)
                intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                startActivity(intent)
                finish()
                true
            } else {
                Log.e("MisLugaresActivity", "Error: Activity de destino es null")
                false // Retorna false si no hay un destino
            }
        }
    }


    private fun verificarPermisos() {
        val permissions = mutableListOf<String>()

        // Verificar permiso para la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        // Verificar permisos para almacenamiento dependiendo de la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requiere permisos específicos para imágenes
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Para versiones anteriores de Android
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Solicitar permisos si son necesarios
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            continuarConLaLogica() // Llamar a la lógica si ya se tienen permisos
        }
    }


    private fun continuarConLaLogica() {
        // Aquí puedes continuar con la lógica que necesita el permiso
    }

    private fun configurarRecyclerView(recyclerView: RecyclerView, lugares: List<LugarTuristico>) {
        lugaresAdapter = MisLugaresAdapter(
            lugares,
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
            onQuitarFavoritoClick = { lugar ->
                Toast.makeText(
                    this,
                    "Lugar eliminado de favoritos: ${lugar.nombre}",
                    Toast.LENGTH_SHORT
                ).show()
                // Lógica para eliminar el lugar de favoritos en la base de datos
                dbHelper.eliminarLugarFavorito(lugar.id.toLong()) // Asegúrate de convertir id a Long
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lugaresAdapter
    }

    private fun capturarFoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Verifica si hay una aplicación que pueda manejar el intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                // Crear un archivo donde almacenar la imagen
                val photoFile: File = createImageFile()
                // Obtener la URI para el archivo de imagen
                currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)

                // Pasar la URI del archivo al intent
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                takePictureLauncher.launch(takePictureIntent)
            } catch (e: Exception) {
                Log.e("MisLugaresActivity", "Error al abrir la cámara", e)
                Toast.makeText(this, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$timestamp", ".jpg", storageDir).apply {
            Log.d("MisLugaresActivity", "Archivo de imagen creado: ${absolutePath}")
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
                guardarLugarEnBaseDeDatos(it, currentPhotoUri?.toString(), fechaSeleccionada)
                Toast.makeText(this, "Fecha seleccionada: $fechaSeleccionada", Toast.LENGTH_SHORT)
                    .show()
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun guardarLugarEnBaseDeDatos(
        lugar: LugarTuristico,
        foto: String?,
        fechaVisita: String?
    ) {
        // Pasamos el objeto LugarTuristico completo
        val result = dbHelper.insertarLugarFavorito(lugar)
        if (result != -1L) {
            Toast.makeText(this, "Lugar guardado en favoritos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al guardar el lugar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*" // Establece el tipo de contenido para imágenes
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE) // Llama a la galería
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            currentPhotoUri = selectedImageUri // Guarda la URI seleccionada
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                continuarConLaLogica() // Continuar solo si todos los permisos fueron concedidos
            } else {
                Toast.makeText(this, "Se requieren permisos para usar esta funcionalidad.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

