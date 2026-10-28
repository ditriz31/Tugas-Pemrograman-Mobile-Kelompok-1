package id.ac.stmi.katalogshowroom

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Jika sesi login masih tersimpan dan akunnya masih ada, langsung buka katalog
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
            AkunManager.akunAktif(this) != null
        ) {
            bukaKatalog()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val cbIngatSaya = findViewById<CheckBox>(R.id.cbIngatSaya)
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)
        val btnDaftar = findViewById<Button>(R.id.btnDaftar)

        // Hasil dari halaman registrasi: isi otomatis email yang baru didaftarkan
        val registerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    etEmail.setText(result.data?.getStringExtra(RegisterActivity.EXTRA_EMAIL))
                    Toast.makeText(this, R.string.berhasil_daftar, Toast.LENGTH_LONG).show()
                }
            }

        btnDaftar.setOnClickListener {
            registerLauncher.launch(Intent(this, RegisterActivity::class.java))
        }

        btnMasuk.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = getString(R.string.error_email_kosong)
                return@setOnClickListener
            }

            // Login memakai akun yang sudah tersimpan saat registrasi
            val akun = AkunManager.cari(this, email)
            if (akun == null) {
                etEmail.error = getString(R.string.error_email_belum_terdaftar)
                return@setOnClickListener
            }

            // Menyimpan sesi ke SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString(KEY_EMAIL, akun.email)
            editor.putString(KEY_NAME, akun.nama)
            editor.putBoolean(KEY_IS_LOGGED_IN, cbIngatSaya.isChecked)
            editor.apply()

            bukaKatalog()
        }
    }

    private fun bukaKatalog() {
        // Explicit Intent ke MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val PREF_NAME = "katalog_showroom_pref"
        const val KEY_NAME = "nama"
        const val KEY_EMAIL = "email"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}
