package id.ac.stmi.katalogshowroom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

// Form booking test drive
class BookingActivity : AppCompatActivity() {

    private var tanggal: String? = null
    private var jam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val spMobil = findViewById<Spinner>(R.id.spMobil)
        val etTanggal = findViewById<EditText>(R.id.etTanggal)
        val etJam = findViewById<EditText>(R.id.etJam)
        val etCatatan = findViewById<EditText>(R.id.etCatatan)

        spMobil.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            DataMobil.daftarMobil.map { it.nama }
        )
        // Jika dibuka dari halaman detail, mobil langsung terpilih
        val idAwal = intent.getIntExtra(EXTRA_ID_MOBIL, -1)
        val posisi = DataMobil.daftarMobil.indexOfFirst { it.id == idAwal }
        if (posisi >= 0) spMobil.setSelection(posisi)

        etTanggal.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, tahun, bulan, hari ->
                    tanggal = String.format("%02d-%02d-%04d", hari, bulan + 1, tahun)
                    etTanggal.setText(tanggal)
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
            }.show()
        }

        etJam.setOnClickListener {
            TimePickerDialog(
                this,
                { _, jamPilih, menit ->
                    val total = jamPilih * 60 + menit
                    if (total < JAM_BUKA * 60 || total > JAM_TUTUP * 60) {
                        Toast.makeText(this, R.string.error_jam_operasional, Toast.LENGTH_LONG).show()
                    } else {
                        jam = String.format("%02d.%02d", jamPilih, menit)
                        etJam.setText(jam)
                    }
                },
                10, 0, true
            ).show()
        }

        findViewById<Button>(R.id.btnKirim).setOnClickListener {
            val tgl = tanggal
            val jm = jam
            if (tgl == null) {
                Toast.makeText(this, R.string.error_tanggal_kosong, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (jm == null) {
                Toast.makeText(this, R.string.error_jam_kosong, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val email = AkunManager.emailAktif(this) ?: return@setOnClickListener
            val mobil = DataMobil.daftarMobil[spMobil.selectedItemPosition]

            BookingManager.tambah(
                this, email,
                Booking(System.currentTimeMillis(), mobil.id, tgl, jm, etCatatan.text.toString().trim())
            )
            Toast.makeText(this, R.string.berhasil_booking, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_ID_MOBIL = "extra_id_mobil"
        private const val JAM_BUKA = 9
        private const val JAM_TUTUP = 17
    }
}
