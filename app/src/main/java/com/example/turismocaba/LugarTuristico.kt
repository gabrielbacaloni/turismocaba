package com.example.turismocaba

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class LugarTuristico(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val imagen: Int, // Manteniendo la imagen como Int (referencia a recursos locales en mipmap)
    val latitud: Double,
    val longitud: Double,
    var favorito: Boolean = false,
    var isSelected: Boolean = false,
    var fechaVisita: String? = null
) : Parcelable {

    private constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nombre = parcel.readString() ?: "",
        descripcion = parcel.readString() ?: "",
        ubicacion = parcel.readString() ?: "",
        imagen = parcel.readInt(), // Lee la imagen como Int
        latitud = parcel.readDouble(),
        longitud = parcel.readDouble(),
        favorito = parcel.readByte() != 0.toByte(),
        isSelected = parcel.readByte() != 0.toByte(),
        fechaVisita = parcel.readString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeString(ubicacion)
        parcel.writeInt(imagen) // Escribe la imagen como Int
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
        parcel.writeByte(if (favorito) 1 else 0)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeString(fechaVisita)
    }

    companion object CREATOR : Parcelable.Creator<LugarTuristico> {
        override fun createFromParcel(parcel: Parcel): LugarTuristico {
            return LugarTuristico(parcel)
        }

        override fun newArray(size: Int): Array<LugarTuristico?> {
            return arrayOfNulls(size)
        }

        // Lista estática de lugares turísticos
        val lugaresTuristicos = listOf(
            LugarTuristico(
                id = 1,
                nombre = "Obelisco",
                descripcion = "Descripción del Obelisco",
                ubicacion = "Ubicación del Obelisco",
                imagen = R.mipmap.ic_obelisco, // Referencia a mipmap
                latitud = -34.6037,
                longitud = -58.3816
            ),
            LugarTuristico(
                id = 2,
                nombre = "San Telmo",
                descripcion = "Descripción de San Telmo",
                ubicacion = "Ubicación de San Telmo",
                imagen = R.mipmap.ic_santelmo, // Referencia a mipmap
                latitud = -34.6212,
                longitud = -58.3731
            ),
            LugarTuristico(
                id = 3,
                nombre = "Caminito",
                descripcion = "Descripción de Caminito",
                ubicacion = "Ubicación de Caminito",
                imagen = R.mipmap.ic_caminito, // Referencia a mipmap
                latitud = -34.6345,
                longitud = -58.3630
            ),
            LugarTuristico(
                id = 4,
                nombre = "Calle Corrientes",
                descripcion = "Descripción de Calle Corrientes",
                ubicacion = "Ubicación de Calle Corrientes",
                imagen = R.mipmap.ic_callecorrientes, // Referencia a mipmap
                latitud = -34.6035,
                longitud = -58.3820
            ),
            LugarTuristico(
                id = 5,
                nombre = "Congreso",
                descripcion = "Descripción de Congreso",
                ubicacion = "Ubicación del Congreso",
                imagen = R.mipmap.ic_congreso, // Referencia a mipmap
                latitud = -34.6090,
                longitud = -58.3928
            ),
            LugarTuristico(
                id = 6,
                nombre = "La Casa Rosada",
                descripcion = "Descripción de La Casa Rosada",
                ubicacion = "Ubicación de La Casa Rosada",
                imagen = R.mipmap.ic_lacasarosada, // Referencia a mipmap
                latitud = -34.6083,
                longitud = -58.3708
            ),
            LugarTuristico(
                id = 7,
                nombre = "Sede Boyacá Independiente",
                descripcion = "La sede de Independiente ubicada en la calle Boyacá",
                ubicacion = "Boyacá, Buenos Aires, Argentina",
                imagen = R.mipmap.ic_sedeindependiente, // Referencia a mipmap
                latitud = -34.6066,
                longitud = -58.4513
            ),
            LugarTuristico(
                id = 8,
                nombre = "Puerto Madero",
                descripcion = "Descripción de Puerto Madero",
                ubicacion = "Ubicación de Puerto Madero",
                imagen = R.mipmap.ic_puertomadero, // Referencia a mipmap
                latitud = -34.6114,
                longitud = -58.3624
            )
        )
    }

    // Función para abrir el lugar en Google Maps
    fun abrirEnGoogleMaps(context: Context) {
        val gmmIntentUri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombre)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }
}