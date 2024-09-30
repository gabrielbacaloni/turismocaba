package com.example.turismocaba

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TurismoCABADBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "turismo_caba.db"
        const val DATABASE_VERSION = 2 // Cambié la versión de la base de datos a 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla "users"
        val createUsersTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                surname TEXT,
                email TEXT,
                country TEXT,
                password TEXT,
                favorite_team TEXT,
                favorite_book TEXT
            )
        """.trimIndent()

        // Crear la tabla "favoritos"
        val createFavoritosTable = """
            CREATE TABLE favoritos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                lugar TEXT,
                foto TEXT,
                fecha_visita TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createFavoritosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createFavoritosTable = """
                CREATE TABLE favoritos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    lugar TEXT,
                    foto TEXT,
                    fecha_visita TEXT
                )
            """.trimIndent()
            db.execSQL(createFavoritosTable)
        }
    }

    // Método para insertar un usuario
    fun insertarUsuario(name: String, surname: String, email: String, country: String, password: String, favoriteTeam: String, favoriteBook: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", name)
            put("surname", surname)
            put("email", email)
            put("country", country)
            put("password", password)
            put("favorite_team", favoriteTeam)
            put("favorite_book", favoriteBook)
        }
        return db.insert("users", null, contentValues)
    }

    // Método para insertar un lugar favorito
    fun insertarLugarFavorito(lugar: String, foto: String?, fechaVisita: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("lugar", lugar)
            put("foto", foto)
            put("fecha_visita", fechaVisita)
        }
        return db.insert("favoritos", null, contentValues)
    }

    // Método para obtener los lugares favoritos
    fun obtenerLugaresFavoritos(): List<LugarFavorito> {
        val lugaresFavoritos = mutableListOf<LugarFavorito>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM favoritos", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val lugar = cursor.getString(cursor.getColumnIndexOrThrow("lugar"))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                val fechaVisita = cursor.getString(cursor.getColumnIndexOrThrow("fecha_visita"))
                lugaresFavoritos.add(LugarFavorito(id, lugar, foto, fechaVisita))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lugaresFavoritos
    }
}

data class LugarFavorito(
    val id: Int,
    val lugar: String,
    val foto: String?,
    val fechaVisita: String?
)
