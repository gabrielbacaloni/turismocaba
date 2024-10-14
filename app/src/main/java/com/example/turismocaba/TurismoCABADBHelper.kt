package com.example.turismocaba

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class TurismoCABADBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "turismo_caba.db"
        const val DATABASE_VERSION = 3
        const val TABLE_USERS = "users"
        const val TABLE_FAVORITOS = "favoritos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_APELLIDO = "apellido"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FAVORITE_TEAM = "favorite_team"
        const val COLUMN_FAVORITE_BOOK = "favorite_book"
        const val COLUMN_LUGAR_NOMBRE = "lugar_nombre"
        const val COLUMN_LUGAR_IMAGEN = "lugar_imagen"
        const val COLUMN_LUGAR_LATITUD = "lugar_latitud"
        const val COLUMN_LUGAR_LONGITUD = "lugar_longitud"
        const val COLUMN_FECHA_VISITA = "fecha_visita"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT,
                $COLUMN_APELLIDO TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_COUNTRY TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_FAVORITE_TEAM TEXT,
                $COLUMN_FAVORITE_BOOK TEXT
            )
        """.trimIndent()

        val createFavoritosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_FAVORITOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LUGAR_NOMBRE TEXT,
                $COLUMN_LUGAR_IMAGEN TEXT,
                $COLUMN_LUGAR_LATITUD REAL,
                $COLUMN_LUGAR_LONGITUD REAL,
                $COLUMN_FECHA_VISITA TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createFavoritosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            // Solo eliminar la tabla favoritos y recrearla, ya que `users` debe preservarse
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
            onCreate(db)  // Vuelve a crear las tablas necesarias
        }
    }

    fun insertarLugarFavorito(lugar: LugarTuristico): Long {
        val db = this.writableDatabase

        // Suponiendo que 'LUGARES_FAVORITOS' es el nombre de la tabla
        val contentValues = ContentValues().apply {
            put("nombre", lugar.nombre)
            put("descripcion", lugar.descripcion)
            put("ubicacion", lugar.ubicacion)
            put("imagen", lugar.imagen) // Asegúrate de que este campo esté bien definido en la base de datos
            put("latitud", lugar.latitud)
            put("longitud", lugar.longitud)
            put("favorito", if (lugar.favorito) 1 else 0)
            put("isSelected", if (lugar.isSelected) 1 else 0)
            put("fechaVisita", lugar.fechaVisita)
        }

        // Insertar en la base de datos
        val result = db.insert("LUGARES_FAVORITOS", null, contentValues)

        db.close()
        return result // Retorna el valor de inserción, que es un ID o -1 en caso de error
    }

    fun actualizarContrasena(usuario: Usuario, nuevaContrasena: String) {
        val db = this.writableDatabase

        // Crear un objeto ContentValues para actualizar la contraseña
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, nuevaContrasena)  // Asigna la nueva contraseña
        }

        // Especificar la condición de la actualización: email del usuario
        val whereClause = "$COLUMN_EMAIL = ?"
        val whereArgs = arrayOf(usuario.email)  // Usar el email del usuario como criterio de búsqueda

        // Realizar la actualización
        val rowsUpdated = db.update(TABLE_USERS, values, whereClause, whereArgs)

        if (rowsUpdated > 0) {
            println("Contraseña actualizada correctamente")
        } else {
            println("Error al actualizar la contraseña")
        }

        db.close()  // Cerrar la base de datos
    }

    // Método para obtener todos los lugares favoritos
    fun obtenerTodosLosLugaresFavoritos(): List<LugarTuristico> {
        val lugaresFavoritos = mutableListOf<LugarTuristico>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITOS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))  // Obtener el ID
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LUGAR_NOMBRE))
                val imagenNombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LUGAR_IMAGEN))
                val latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LUGAR_LATITUD))
                val longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LUGAR_LONGITUD))
                val fechaVisita = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_VISITA))

                // Resolver el nombre de la imagen en su ID de recurso
                val imagenId = obtenerIdRecursoPorNombre(imagenNombre)

                val lugar = LugarTuristico(id.toInt(), nombre, "", "", imagenId, latitud, longitud)
                lugar.fechaVisita = fechaVisita  // Agregar la fecha de visita
                lugaresFavoritos.add(lugar)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lugaresFavoritos
    }

    private fun obtenerIdRecursoPorNombre(nombreImagen: String): Int {
        return when (nombreImagen) {
            "ic_obelisco" -> R.mipmap.ic_obelisco
            "ic_santelmo" -> R.mipmap.ic_santelmo
            "ic_caminito" -> R.mipmap.ic_caminito
            "ic_callecorrientes" -> R.mipmap.ic_callecorrientes
            "ic_congreso" -> R.mipmap.ic_congreso
            "ic_lacasarosada" -> R.mipmap.ic_lacasarosada
            "ic_sedeindependiente" -> R.mipmap.ic_sedeindependiente
            "ic_puertomadero" -> R.mipmap.ic_puertomadero
            else -> throw IllegalArgumentException("Imagen no encontrada para $nombreImagen")
        }
    }

    // Método para eliminar un lugar favorito utilizando el ID
    fun eliminarLugarFavorito(id: Long): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        val deletedRows = db.delete(TABLE_FAVORITOS, whereClause, whereArgs)
        db.close()
        return deletedRows
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,  // Nombre de la tabla de usuarios
            null,  // Selecciona todas las columnas
            "$COLUMN_EMAIL = ?",  // Condición de búsqueda por email
            arrayOf(email),  // Parámetro con el email que buscas
            null, null, null
        )

        // Si encontramos un usuario, lo devolvemos
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO))
            val country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val favoriteTeam = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_TEAM))
            val favoriteBook = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_BOOK))

            // Devuelve un objeto Usuario con los datos obtenidos
            Usuario(id, nombre, apellido, email, country, password, favoriteTeam, favoriteBook)
        } else {
            null  // Si no se encuentra un usuario con ese email
        }
    }
    fun insertarUsuario(
        nombre: String, apellido: String, email: String, country: String,
        password: String, favoriteTeam: String, favoriteBook: String
    ): Long {
        val db = this.writableDatabase

        // Crear un objeto ContentValues para insertar los datos
        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_EMAIL, email)
            put(COLUMN_COUNTRY, country)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FAVORITE_TEAM, favoriteTeam)
            put(COLUMN_FAVORITE_BOOK, favoriteBook)
        }

        // Insertar los datos en la base de datos
        val result = db.insert(TABLE_USERS, null, contentValues)

        db.close()  // Cerrar la base de datos
        return result // Retorna el ID de la fila insertada o -1 si hubo un error
    }


    fun obtenerNombreUsuario(id: Int): String {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,  // Nombre de la tabla de usuarios
            arrayOf(COLUMN_NOMBRE),  // Seleccionamos solo el nombre del usuario
            "$COLUMN_ID = ?",  // Condición: buscamos por ID
            arrayOf(id.toString()),  // Parámetro: el ID del usuario
            null, null, null
        )

        var nombreUsuario = "Usuario no encontrado"  // Valor por defecto en caso de no encontrar el usuario
        if (cursor != null && cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
        }

        cursor.close()
        db.close()
        return nombreUsuario
    }
}
