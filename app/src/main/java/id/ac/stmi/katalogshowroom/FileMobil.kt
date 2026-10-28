package id.ac.stmi.katalogshowroom

import android.content.Context

// Menyimpan id mobil ke File di memori internal aplikasi.
// Satu file per akun untuk "Tersimpan" dan satu lagi untuk "Terakhir Dilihat".
object FileMobil {
    private const val MAKS_DILIHAT = 10

    private fun kode(email: String) = email.trim().lowercase().hashCode()
    private fun fileSimpan(email: String) = "mobil_tersimpan_${kode(email)}.txt"
    private fun fileDilihat(email: String) = "mobil_dilihat_${kode(email)}.txt"

    // ===== Tersimpan =====
    fun simpan(context: Context, email: String, mobilBaru: List<Mobil>) {
        val ids = bacaFile(context, fileSimpan(email)).toMutableSet()
        mobilBaru.forEach { ids.add(it.id) }
        tulisFile(context, fileSimpan(email), ids)
    }

    // Menyimpan jika belum ada, menghapus jika sudah ada. Mengembalikan true jika sekarang tersimpan.
    fun toggle(context: Context, email: String, idMobil: Int): Boolean {
        val ids = bacaFile(context, fileSimpan(email)).toMutableList()
        val sekarangTersimpan = if (ids.contains(idMobil)) {
            ids.remove(idMobil)
            false
        } else {
            ids.add(idMobil)
            true
        }
        tulisFile(context, fileSimpan(email), ids)
        return sekarangTersimpan
    }

    // Terbaru di atas
    fun baca(context: Context, email: String): List<Mobil> =
        bacaFile(context, fileSimpan(email)).reversed().mapNotNull { DataMobil.cariById(it) }

    fun sudahTersimpan(context: Context, email: String, idMobil: Int): Boolean =
        bacaFile(context, fileSimpan(email)).contains(idMobil)

    fun hapusSemua(context: Context, email: String) {
        context.deleteFile(fileSimpan(email))
    }

    // ===== Terakhir Dilihat =====
    fun catatDilihat(context: Context, email: String, idMobil: Int) {
        val ids = bacaFile(context, fileDilihat(email)).toMutableList()
        ids.remove(idMobil)
        ids.add(idMobil)
        tulisFile(context, fileDilihat(email), ids.takeLast(MAKS_DILIHAT))
    }

    fun bacaDilihat(context: Context, email: String): List<Mobil> =
        bacaFile(context, fileDilihat(email)).reversed().mapNotNull { DataMobil.cariById(it) }

    fun hapusDilihat(context: Context, email: String, idMobil: Int) {
        val ids = bacaFile(context, fileDilihat(email)).toMutableList()
        ids.remove(idMobil)
        tulisFile(context, fileDilihat(email), ids)
    }

    fun hapusSemuaDilihat(context: Context, email: String) {
        context.deleteFile(fileDilihat(email))
    }

    // ===== Baca / tulis file =====
    private fun tulisFile(context: Context, nama: String, ids: Collection<Int>) {
        // Menulis file: satu id mobil per baris
        context.openFileOutput(nama, Context.MODE_PRIVATE).use { output ->
            output.write(ids.joinToString("\n").toByteArray())
        }
    }

    private fun bacaFile(context: Context, nama: String): List<Int> {
        if (!context.getFileStreamPath(nama).exists()) return emptyList()

        // Membaca file baris per baris
        return context.openFileInput(nama).bufferedReader().use { reader ->
            reader.readLines().mapNotNull { it.trim().toIntOrNull() }
        }
    }
}
