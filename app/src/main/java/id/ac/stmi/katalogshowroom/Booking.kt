package id.ac.stmi.katalogshowroom

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Booking(
    val id: Long,
    val idMobil: Int,
    val tanggal: String,
    val jam: String,
    val catatan: String
)

// Menyimpan booking test drive per akun di SharedPreferences (format JSON)
object BookingManager {
    private const val PREF = "booking_pref"

    private fun key(email: String) = "booking_${email.trim().lowercase().hashCode()}"

    // Terbaru di atas
    fun semua(context: Context, email: String): List<Booking> {
        val json = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(key(email), null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map {
            val o = array.getJSONObject(it)
            Booking(
                o.getLong("id"), o.getInt("idMobil"),
                o.getString("tanggal"), o.getString("jam"), o.optString("catatan", "")
            )
        }.reversed()
    }

    fun tambah(context: Context, email: String, booking: Booking) {
        simpanSemua(context, email, semua(context, email).reversed() + booking)
    }

    fun batal(context: Context, email: String, id: Long) {
        simpanSemua(context, email, semua(context, email).reversed().filter { it.id != id })
    }

    private fun simpanSemua(context: Context, email: String, daftar: List<Booking>) {
        val array = JSONArray()
        daftar.forEach {
            array.put(
                JSONObject().put("id", it.id).put("idMobil", it.idMobil)
                    .put("tanggal", it.tanggal).put("jam", it.jam).put("catatan", it.catatan)
            )
        }
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(key(email), array.toString()).apply()
    }
}
