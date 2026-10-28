package id.ac.stmi.katalogshowroom

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

// Kerangka utama: toolbar + isi tab (Fragment) + Bottom Navigation
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tampilkanLifecycle("onCreate")

        // Tanpa akun aktif, kembali ke halaman login
        if (AkunManager.akunAktif(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            tampilkanTab(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_beranda
        } else {
            supportActionBar?.title = judulTab(bottomNav.selectedItemId)
        }
    }

    // Dipanggil dari fragment lain, contoh: tombol "Lihat Semua Mobil" di Beranda
    fun bukaTab(itemId: Int) {
        bottomNav.selectedItemId = itemId
    }

    private fun tampilkanTab(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_belanja -> BelanjaFragment()
            R.id.nav_order -> OrderFragment()
            R.id.nav_riwayat -> RiwayatFragment()
            R.id.nav_akun -> AkunFragment()
            else -> BerandaFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        supportActionBar?.title = judulTab(itemId)
    }

    private fun judulTab(itemId: Int): String = getString(
        when (itemId) {
            R.id.nav_belanja -> R.string.judul_belanja
            R.id.nav_order -> R.string.judul_order
            R.id.nav_riwayat -> R.string.nav_riwayat
            R.id.nav_akun -> R.string.judul_akun
            else -> R.string.app_name
        }
    )

    // ===== Activity Lifecycle =====
    override fun onStart() {
        super.onStart()
        tampilkanLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        tampilkanLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        tampilkanLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        tampilkanLifecycle("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        tampilkanLifecycle("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        tampilkanLifecycle("onDestroy")
    }

    // Mencatat callback lifecycle yang sedang berjalan ke Logcat (tag: MainActivity)
    private fun tampilkanLifecycle(callback: String) {
        Log.d(TAG, "$callback() dipanggil")
    }

    companion object {
        private const val TAG = "MainActivity"

        // Implicit Intent untuk membagikan foto + info mobil ke aplikasi lain (WA, DM Instagram, dll)
        fun bagikanMobil(context: Context, daftar: List<Mobil>) {
            if (daftar.isEmpty()) return
            val teks = daftar.joinToString("\n\n") {
                context.getString(R.string.teks_bagikan, it.nama, it.tahun, it.transmisi, it.hargaFormat())
            }
            val pesan = context.getString(R.string.judul_bagikan) + "\n\n" + teks + "\n\n" +
                context.getString(R.string.kontak_bagikan, context.getString(R.string.nomor_whatsapp))

            val fotoUri = daftar.mapNotNull { fotoUntukDibagikan(context, it) }
            val intent = when {
                fotoUri.isEmpty() -> Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
                fotoUri.size == 1 -> Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, fotoUri[0])
                }
                else -> Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/jpeg"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(fotoUri))
                }
            }
            intent.putExtra(Intent.EXTRA_TEXT, pesan)
            intent.putExtra(Intent.EXTRA_SUBJECT, daftar.joinToString { it.nama })
            if (fotoUri.isNotEmpty()) {
                // ClipData diperlukan agar izin baca foto ikut diteruskan lewat chooser
                val clip = ClipData.newUri(context.contentResolver, daftar[0].nama, fotoUri[0])
                fotoUri.drop(1).forEach { clip.addItem(ClipData.Item(it)) }
                intent.clipData = clip
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.bagikan_via)))
        }

        // Menyalin foto mobil dari res/drawable ke cache agar bisa dibaca aplikasi lain lewat FileProvider
        private fun fotoUntukDibagikan(context: Context, mobil: Mobil): Uri? {
            val resId = context.resources.getIdentifier(mobil.gambar, "drawable", context.packageName)
            if (resId == 0) return null
            return try {
                val folder = File(context.cacheDir, "bagikan").apply { mkdirs() }
                val file = File(folder, "${mobil.gambar}.jpg")
                if (!file.exists()) {
                    context.resources.openRawResource(resId).use { input ->
                        file.outputStream().use { input.copyTo(it) }
                    }
                }
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            } catch (e: Exception) {
                Log.e(TAG, "Gagal menyiapkan foto untuk dibagikan", e)
                null
            }
        }
    }
}
