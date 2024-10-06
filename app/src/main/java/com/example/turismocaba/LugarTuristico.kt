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
    val imagen: Int, // Cambiado de String? a Int para almacenar el recurso drawable
    val latitud: Double,
    val longitud: Double,
    var favorito: Boolean = false, // Estado de favorito
    var isSelected: Boolean = false, // Estado de selección
    var fechaVisita: String? = null // Fecha de visita
) : Parcelable {

    // Constructor para deserializar el objeto desde un Parcel
    private constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nombre = parcel.readString() ?: "",
        descripcion = parcel.readString() ?: "",
        ubicacion = parcel.readString() ?: "",
        imagen = parcel.readInt(), // Deserializa el recurso de imagen
        latitud = parcel.readDouble(),
        longitud = parcel.readDouble(),
        favorito = parcel.readByte() != 0.toByte(), // Deserializa el estado de favorito
        isSelected = parcel.readByte() != 0.toByte(), // Deserializa el estado de selección
        fechaVisita = parcel.readString() // Deserializa la fecha de visita
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeString(ubicacion)
        parcel.writeInt(imagen) // Serializa el recurso de imagen
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
        parcel.writeByte(if (favorito) 1 else 0) // Serializa el estado de favorito
        parcel.writeByte(if (isSelected) 1 else 0) // Serializa el estado de selección
        parcel.writeString(fechaVisita) // Serializa la fecha de visita
    }

    companion object CREATOR : Parcelable.Creator<LugarTuristico> {
        override fun createFromParcel(parcel: Parcel): LugarTuristico {
            return LugarTuristico(parcel)
        }

        override fun newArray(size: Int): Array<LugarTuristico?> {
            return arrayOfNulls(size)
        }
    }

    // Método para abrir Google Maps con las coordenadas del lugar
    fun abrirEnGoogleMaps(context: Context) {
        val gmmIntentUri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombre)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }
}
