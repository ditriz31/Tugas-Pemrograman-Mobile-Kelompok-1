package id.ac.stmi.katalogshowroom

import android.content.Context
import android.widget.ImageView

data class Mobil(
    val id: Int,
    val nama: String,
    val merek: String,
    val tahun: Int,
    val harga: Long,
    val transmisi: String,
    val bahanBakar: String,
    val mesin: String,
    val tenaga: String,
    val kapasitas: Int,
    val warna: String,
    val deskripsi: String,
    // Nama file foto di res/drawable (tanpa ekstensi), contoh: "foto_avanza"
    val gambar: String = ""
) {
    fun hargaFormat(): String = "Rp " + String.format("%,d", harga).replace(',', '.')

    // Menampilkan foto mobil. Jika file foto belum ada di res/drawable, tampil ikon mobil sebagai cadangan.
    fun tampilkanFoto(context: Context, imageView: ImageView) {
        val resId = context.resources.getIdentifier(gambar, "drawable", context.packageName)
        if (resId != 0) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(0, 0, 0, 0)
            imageView.setImageResource(resId)
        } else {
            val pad = (16 * context.resources.displayMetrics.density).toInt()
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.setPadding(pad, pad, pad, pad)
            imageView.setImageResource(R.drawable.ic_mobil)
        }
    }
}
