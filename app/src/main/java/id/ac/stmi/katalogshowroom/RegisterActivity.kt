package id.ac.stmi.katalogshowroom

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etNoHp = findViewById<EditText>(R.id.etNoHp)

        findViewById<Button>(R.id.btnKeLogin).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnDaftar).setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim().lowercase()
            val noHp = etNoHp.text.toString().replace(Regex("[\\s-]"), "")

            if (nama.isEmpty()) {
                etNama.error = getString(R.string.error_nama_kosong)
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                etEmail.error = getString(R.string.error_email_kosong)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = getString(R.string.error_email_tidak_valid)
                return@setOnClickListener
            }
            if (noHp.isEmpty()) {
                etNoHp.error = getString(R.string.error_no_hp_kosong)
                return@setOnClickListener
            }
            if (!Regex("^\\+?[0-9]{9,15}$").matches(noHp)) {
                etNoHp.error = getString(R.string.error_no_hp_tidak_valid)
                return@setOnClickListener
            }

            if (!AkunManager.daftar(this, Akun(nama, email, noHp))) {
                etEmail.error = getString(R.string.error_email_sudah_ada)
                return@setOnClickListener
            }

            // Kembali ke halaman login membawa email yang baru didaftarkan
            setResult(RESULT_OK, Intent().putExtra(EXTRA_EMAIL, email))
            finish()
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
