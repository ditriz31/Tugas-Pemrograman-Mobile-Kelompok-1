package id.ac.stmi.katalogshowroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

// Tab Akun: menampilkan informasi akun yang sedang login
class AkunFragment : Fragment(R.layout.fragment_akun) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val akun = AkunManager.akunAktif(requireContext())
        view.findViewById<TextView>(R.id.tvInisial).text =
            akun?.nama?.firstOrNull()?.uppercase() ?: "?"
        view.findViewById<TextView>(R.id.tvNamaAkun).text = akun?.nama ?: "-"
        view.findViewById<TextView>(R.id.tvNama).text = akun?.nama ?: "-"
        view.findViewById<TextView>(R.id.tvEmail).text = akun?.email ?: "-"
        view.findViewById<TextView>(R.id.tvNoHp).text = akun?.noHp ?: "-"

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Menghapus sesi login (data akun terdaftar & riwayat tetap tersimpan)
            requireContext().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
