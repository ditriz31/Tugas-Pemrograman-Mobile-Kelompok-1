package id.ac.stmi.katalogshowroom

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class DetailActivity : AppCompatActivity() {

    private lateinit var mobil: Mobil
    private var menuSimpan: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Mengambil data yang dikirim lewat Intent
        val idMobil = intent.getIntExtra(EXTRA_ID_MOBIL, -1)
        val mobil = DataMobil.cariById(idMobil)
        if (mobil == null) {
            finish()
            return
        }

        this.mobil = mobil

        // Catat ke "Terakhir Dilihat" di tab Riwayat
        AkunManager.emailAktif(this)?.let { FileMobil.catatDilihat(this, it, mobil.id) }

        mobil.tampilkanFoto(this, findViewById<ImageView>(R.id.ivMobil))

        val tvNama = findViewById<TextView>(R.id.tvNama)
        tvNama.text = mobil.nama
        // Context Menu muncul saat nama mobil ditekan lama
        registerForContextMenu(tvNama)

        findViewById<TextView>(R.id.tvHarga).text = mobil.hargaFormat()
        findViewById<TextView>(R.id.tvMerek).text = mobil.merek
        findViewById<TextView>(R.id.tvTahun).text = mobil.tahun.toString()
        findViewById<TextView>(R.id.tvTransmisi).text = mobil.transmisi
        findViewById<TextView>(R.id.tvBahanBakar).text = mobil.bahanBakar
        findViewById<TextView>(R.id.tvMesin).text = mobil.mesin
        findViewById<TextView>(R.id.tvTenaga).text = mobil.tenaga
        findViewById<TextView>(R.id.tvKapasitas).text = getString(R.string.format_kapasitas, mobil.kapasitas)
        findViewById<TextView>(R.id.tvWarna).text = mobil.warna
        findViewById<TextView>(R.id.tvDeskripsi).text = mobil.deskripsi

        val btnHubungi = findViewById<Button>(R.id.btnHubungi)
        btnHubungi.setOnClickListener {
            // Popup Menu menempel pada tombol Hubungi Sales
            val popup = PopupMenu(this, btnHubungi)
            popup.menuInflater.inflate(R.menu.menu_popup_hubungi, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_telepon -> {
                        // Implicit Intent membuka aplikasi telepon (dialer)
                        val telp = Intent(Intent.ACTION_DIAL)
                        telp.data = Uri.parse("tel:" + getString(R.string.nomor_showroom))
                        startActivity(telp)
                        true
                    }
                    R.id.menu_booking -> {
                        val booking = Intent(this, BookingActivity::class.java)
                        booking.putExtra(BookingActivity.EXTRA_ID_MOBIL, mobil.id)
                        startActivity(booking)
                        true
                    }
                    R.id.menu_whatsapp -> {
                        // Implicit Intent membuka WhatsApp / browser
                        val pesan = getString(R.string.pesan_whatsapp, mobil.nama)
                        val wa = Intent(Intent.ACTION_VIEW)
                        wa.data = Uri.parse(
                            "https://wa.me/" + getString(R.string.nomor_whatsapp) +
                                "?text=" + Uri.encode(pesan)
                        )
                        startActivity(wa)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    // ===== Options Menu: tombol simpan di pojok kanan atas toolbar =====
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        menuSimpan = menu.findItem(R.id.menu_simpan_detail)
        perbaruiIkonSimpan()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_bagikan_detail && ::mobil.isInitialized) {
            MainActivity.bagikanMobil(this, listOf(mobil))
            return true
        }
        if (item.itemId == R.id.menu_simpan_detail && ::mobil.isInitialized) {
            val email = AkunManager.emailAktif(this) ?: return true
            val tersimpan = FileMobil.toggle(this, email, mobil.id)
            Toast.makeText(
                this,
                if (tersimpan) R.string.disimpan_ke_riwayat else R.string.dihapus_dari_riwayat,
                Toast.LENGTH_SHORT
            ).show()
            perbaruiIkonSimpan()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Ikon kosong = belum disimpan, terisi = sudah ada di Riwayat
    private fun perbaruiIkonSimpan() {
        if (!::mobil.isInitialized) return
        val email = AkunManager.emailAktif(this) ?: return
        val tersimpan = FileMobil.sudahTersimpan(this, email, mobil.id)
        menuSimpan?.setIcon(
            if (tersimpan) R.drawable.ic_bookmark_isi_putih else R.drawable.ic_bookmark_kosong_putih
        )
        menuSimpan?.title = getString(
            if (tersimpan) R.string.hapus_dari_riwayat else R.string.simpan_ke_riwayat
        )
    }

    // ===== Context Menu =====
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle(mobil.nama)
        menuInflater.inflate(R.menu.menu_context_detail, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_salin -> {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(
                    ClipData.newPlainText(mobil.nama, "${mobil.nama} - ${mobil.hargaFormat()}")
                )
                Toast.makeText(this, R.string.berhasil_disalin, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_bagikan -> {
                MainActivity.bagikanMobil(this, listOf(mobil))
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID_MOBIL = "extra_id_mobil"
    }
}
