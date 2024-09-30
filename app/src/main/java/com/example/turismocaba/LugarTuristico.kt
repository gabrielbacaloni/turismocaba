package com.example.turismocaba

import android.os.Parcel
import android.os.Parcelable

data class LugarTuristico(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val imagenUri: String?
) : Parcelable {

    // Constructor para deserializar el objeto desde un Parcel
    private constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nombre = parcel.readString() ?: "",
        descripcion = parcel.readString() ?: "",
        ubicacion = parcel.readString() ?: "",
        imagenUri = parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeString(ubicacion)
        parcel.writeString(imagenUri)
    }

    companion object CREATOR : Parcelable.Creator<LugarTuristico> {
        override fun createFromParcel(parcel: Parcel): LugarTuristico {
            return LugarTuristico(parcel)
        }

        override fun newArray(size: Int): Array<LugarTuristico?> {
            return arrayOfNulls(size)
        }
    }
}
