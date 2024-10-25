package com.example.turismocaba

import android.content.ContentValues
import android.util.Log
import android.content.Context
import android.database.Cursor
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
        const val COLUMN_DESCRIPCION = "descripcion"
        const val COLUMN_UBICACION = "ubicacion"
        const val COLUMN_FECHA_VISITA = "fecha_visita"
        const val COLUMN_ID_USUARIO = "id_usuario"
        const val COLUMN_ID_USER_FAV ="id_user_fav"
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
                $COLUMN_LUGAR_IMAGEN INTEGER,
                $COLUMN_LUGAR_LATITUD REAL,
                $COLUMN_LUGAR_LONGITUD REAL,
                $COLUMN_FECHA_VISITA TEXT,
                $COLUMN_DESCRIPCION TEXT,
                $COLUMN_UBICACION TEXT,
                $COLUMN_ID_USUARIO INTEGER,
                $COLUMN_ID_USER_FAV INTEGER,
                FOREIGN KEY ($COLUMN_ID_USER_FAV) REFERENCES $TABLE_USERS($COLUMN_ID)   
            
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createFavoritosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
            onCreate(db)  // Vuelve a crear las tablas necesarias
        }
    }

    fun insertarLugarFavorito(idUsuario: Int, lugar: LugarTuristico): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_USER_FAV, idUsuario) /* ID del usuario */
            put(COLUMN_ID_USUARIO, lugar.id) /* ID del lugar */
            put(COLUMN_LUGAR_NOMBRE, lugar.nombre)
            put(COLUMN_LUGAR_IMAGEN, lugar.imagen)
            put(COLUMN_LUGAR_LATITUD, lugar.latitud)
            put(COLUMN_LUGAR_LONGITUD, lugar.longitud)
            put(COLUMN_FECHA_VISITA, lugar.fechaVisita)
            put(COLUMN_DESCRIPCION, lugar.descripcion)
            put(COLUMN_UBICACION, lugar.ubicacion)
        }
        val result = db.insert(TABLE_FAVORITOS, null, values)
        db.close()
        return result
    }
    fun actualizarContrasena(usuario: Usuario, nuevaContrasena: String) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, nuevaContrasena)
        }

        // Cambiamos la cláusula WHERE para usar el ID del usuario
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(usuario.id.toString())

        val rowsUpdated = db.update(TABLE_USERS, values, whereClause, whereArgs)

        if (rowsUpdated > 0) {
            println("Contraseña actualizada correctamente")
        } else {
            println("Error al actualizar la contraseña")
        }

        db.close()
    }

    fun obtenerLugaresFavoritos(idUsuario: Int): List<LugarTuristico> {
        val db = readableDatabase
        val lugaresFavoritos = mutableListOf<LugarTuristico>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITOS WHERE $COLUMN_ID_USER_FAV = ?", arrayOf(idUsuario.toString()))
        with(cursor) {
            while (moveToNext()) {
                val lugar = LugarTuristico(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID_USUARIO)),
                    nombre = getString(getColumnIndexOrThrow(COLUMN_LUGAR_NOMBRE)),
                    descripcion = getString(getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                    ubicacion = getString(getColumnIndexOrThrow(COLUMN_UBICACION)),
                    imagen = getInt(getColumnIndexOrThrow(COLUMN_LUGAR_IMAGEN)),
                    latitud = getDouble(getColumnIndexOrThrow(COLUMN_LUGAR_LATITUD)),
                    longitud = getDouble(getColumnIndexOrThrow(COLUMN_LUGAR_LONGITUD)),
                    fechaVisita = getString(getColumnIndexOrThrow(COLUMN_FECHA_VISITA))
                )
                lugaresFavoritos.add(lugar)
            }
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

    fun eliminarLugarFavorito(idUsuario: Int, idLugar: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID_USER_FAV = ? AND $COLUMN_ID_USUARIO = ?" /* ID del usuario e ID del lugar */
        val whereArgs = arrayOf(idUsuario.toString(), idLugar.toString())
        val deletedRows = db.delete(TABLE_FAVORITOS, whereClause, whereArgs)
        db.close()
        return deletedRows
    }

    fun obtenerUsuarioPorCredenciales(email: String, password: String): Usuario? {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.query(
                TABLE_USERS,
                null,
                "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
                arrayOf(email, password),
                null, null, null
            )

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO))
                val country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY))
                val favoriteTeam = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_TEAM))
                val favoriteBook = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_BOOK))

                Usuario(id, nombre, apellido, email, country, password, favoriteTeam, favoriteBook)
            } else {
                null
            }
        } finally {
            cursor?.close()
            db.close()
        }
    }


    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        val db = this.readableDatabase
        var cursor: Cursor? = null

        Log.d("DBHelper", "Buscando usuario con email: $email")

        return try {
            cursor = db.query(
                TABLE_USERS,
                null,
                "$COLUMN_EMAIL = ?",
                arrayOf(email),
                null, null, null
            )
            Log.d("DBHelper", "Número de filas encontradas: ${cursor?.count}")

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO))
                val country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val favoriteTeam = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_TEAM))
                val favoriteBook = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_BOOK))

                Usuario(id, nombre, apellido, email, country, password, favoriteTeam, favoriteBook)
            } else {
                Log.d("DBHelper", "No se encontró ningún usuario con ese email.")
                null
            }
        } finally {
            cursor?.close()
        }
    }

    fun insertarUsuario(
        nombre: String, apellido: String, email: String, country: String,
        password: String, favoriteTeam: String, favoriteBook: String
    ): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_EMAIL, email)
            put(COLUMN_COUNTRY, country)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FAVORITE_TEAM, favoriteTeam)
            put(COLUMN_FAVORITE_BOOK, favoriteBook)
        }

        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result
    }

    fun obtenerNombreUsuario(id: Int): String {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_NOMBRE),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var nombreUsuario = "Usuario no encontrado"
        if (cursor != null && cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
        }

        cursor.close()
        db.close()
        return nombreUsuario
    }
    fun obtenerUsuarioPorId(id: Int): Usuario? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDO))
            val country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val favoriteTeam = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_TEAM))
            val favoriteBook = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_BOOK))

            Usuario(id, nombre, apellido, email, country, password, favoriteTeam, favoriteBook)
        } else {
            null
        }.also {
            cursor?.close()
            db.close()
        }
    }
    fun verificarLugarEnFavoritos(idUsuario: Int, idLugar: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FAVORITOS WHERE $COLUMN_ID_USER_FAV = ? AND $COLUMN_ID_USUARIO = ?" /* ID del usuario e ID del lugar */
        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString(), idLugar.toString()))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

}


