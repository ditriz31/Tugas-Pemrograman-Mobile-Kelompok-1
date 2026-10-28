package id.ac.stmi.katalogshowroom

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Akun(val nama: String, val email: String, val noHp: String)

// Menyimpan semua akun yang sudah terdaftar (SharedPreferences terpisah dari sesi login)
object AkunManager {
    private const val PREF_AKUN = "akun_pref"
    private const val KEY_DAFTAR = "daftar_akun"

    fun semua(context: Context): List<Akun> {
        val json = context.getSharedPreferences(PREF_AKUN, Context.MODE_PRIVATE)
            .getString(KEY_DAFTAR, null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map {
            val o = array.getJSONObject(it)
            Akun(o.getString("nama"), o.getString("email"), o.getString("noHp"))
        }
    }

    fun cari(context: Context, email: String): Akun? =
        semua(context).find { it.email.equals(email.trim(), ignoreCase = true) }

    // Mengembalikan false jika email sudah terdaftar
    fun daftar(context: Context, akun: Akun): Boolean {
        if (cari(context, akun.email) != null) return false
        val array = JSONArray()
        (semua(context) + akun.copy(email = akun.email.trim().lowercase())).forEach {
            array.put(JSONObject().put("nama", it.nama).put("email", it.email).put("noHp", it.noHp))
        }
        context.getSharedPreferences(PREF_AKUN, Context.MODE_PRIVATE)
            .edit().putString(KEY_DAFTAR, array.toString()).apply()
        return true
    }

    // Email akun yang sedang login (dari sesi di LoginActivity)
    fun emailAktif(context: Context): String? =
        context.getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
            .getString(LoginActivity.KEY_EMAIL, null)?.takeIf { it.isNotBlank() }

    fun akunAktif(context: Context): Akun? = emailAktif(context)?.let { cari(context, it) }
}
