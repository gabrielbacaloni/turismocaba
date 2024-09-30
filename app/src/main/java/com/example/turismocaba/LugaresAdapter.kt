
package com.example.turismocaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

// Adaptador actualizado para usar LugarTuristico
class LugaresAdapter(
    private val lugares: List<LugarTuristico>, // Utilizar LugarTuristico en lugar de Lugar
    private val onLugarClick: (LugarTuristico) -> Unit // Callback para manejar clic en lugar
) : RecyclerView.Adapter<LugaresAdapter.LugaresViewHolder>() {

    private val lugaresSeleccionados = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugaresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_lugar, parent, false)
        return LugaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugaresViewHolder, position: Int) {
        val lugar = lugares[position]
        holder.checkBox.text = lugar.nombre
        holder.checkBox.isChecked = lugaresSeleccionados.contains(lugar.nombre)

        // Manejar clic en el elemento del RecyclerView
        holder.itemView.setOnClickListener {
            onLugarClick(lugar) // Llamar al callback con el lugar seleccionado
        }

        // Manejar selección del CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lugaresSeleccionados.add(lugar.nombre)
            } else {
                lugaresSeleccionados.remove(lugar.nombre)
            }
        }
    }

    override fun getItemCount(): Int {
        return lugares.size
    }

    fun getLugaresSeleccionados(): Set<String> {
        return lugaresSeleccionados
    }

    class LugaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxLugar)
    }
}
