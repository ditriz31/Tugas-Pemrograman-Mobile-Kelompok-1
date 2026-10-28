package id.ac.stmi.katalogshowroom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class MobilAdapter(context: Context, data: MutableList<Mobil>) :
    ArrayAdapter<Mobil>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_mobil, parent, false)
        val mobil = getItem(position)!!

        mobil.tampilkanFoto(context, view.findViewById<ImageView>(R.id.ivMobil))
        view.findViewById<TextView>(R.id.tvNamaMobil).text = mobil.nama
        view.findViewById<TextView>(R.id.tvInfoMobil).text =
            "${mobil.tahun} • ${mobil.transmisi} • ${mobil.bahanBakar}"
        view.findViewById<TextView>(R.id.tvHargaMobil).text = mobil.hargaFormat()
        view.findViewById<ImageButton>(R.id.btnBagikan).setOnClickListener {
            MainActivity.bagikanMobil(context, listOf(mobil))
        }
        return view
    }
}
