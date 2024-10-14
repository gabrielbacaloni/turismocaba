package com.example.turismocaba

import android.Manifest
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_lugares)

        dbHelper = TurismoCABADBHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.rvMisLugares)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Obtener la lista de lugares seleccionados desde el Intent
        val lugaresSeleccionados: ArrayList<LugarTuristico> =
            intent.extras?.getParcelableArrayList("lugaresSeleccionados") ?: arrayListOf()

        Log.d("MisLugaresActivity", "Lugares seleccionados: $lugaresSeleccionados")

        // Configurar el RecyclerView
        configurarRecyclerView(recyclerView, lugaresSeleccionados)

        // Configurar la navegación en la barra inferior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                R.id.navigation_mis_lugares -> {
                    Toast.makeText(this, "Estás en Mis Lugares", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }

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

    private fun verificarPermisos() {
        val permissions = mutableListOf<String>()

        // Verificar permiso para la cámara
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }

        // Solicitar permisos si son necesarios
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            continuarConLaLogica()
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
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createImageFile()
                currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                takePictureLauncher.launch(takePictureIntent)
            } catch (e: Exception) {
                Log.e("MisLugaresActivity", "Error al abrir la cámara", e)
                Toast.makeText(this, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            if (grantResults.isNotEmpty()) {
                val deniedPermissions = permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }
                if (deniedPermissions.isNotEmpty()) {
                    Toast.makeText(this, "Los siguientes permisos fueron denegados: ${deniedPermissions.joinToString(", ")}", Toast.LENGTH_SHORT).show()
                } else {
                    continuarConLaLogica()
                }
            } else {
                Toast.makeText(this, "No se otorgaron permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
