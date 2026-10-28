package id.ac.stmi.katalogshowroom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class BookingAdapter(context: Context, data: MutableList<Booking>) :
    ArrayAdapter<Booking>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_booking, parent, false)
        val booking = getItem(position)!!
        val mobil = DataMobil.cariById(booking.idMobil)

        mobil?.tampilkanFoto(context, view.findViewById<ImageView>(R.id.ivMobil))
        view.findViewById<TextView>(R.id.tvNamaMobil).text = mobil?.nama ?: "-"
        view.findViewById<TextView>(R.id.tvJadwal).text =
            context.getString(R.string.format_jadwal, booking.tanggal, booking.jam)
        view.findViewById<TextView>(R.id.tvCatatan).apply {
            text = booking.catatan
            visibility = if (booking.catatan.isBlank()) View.GONE else View.VISIBLE
        }
        return view
    }
}
