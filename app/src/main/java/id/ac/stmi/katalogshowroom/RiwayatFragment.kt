package id.ac.stmi.katalogshowroom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup

// Tab Riwayat: bagian "Tersimpan" dan "Terakhir Dilihat" untuk akun yang sedang login
class RiwayatFragment : Fragment(R.layout.fragment_riwayat) {

    private lateinit var adapter: MobilAdapter
    private lateinit var tvJumlah: TextView
    private lateinit var tvKosong: TextView
    private val daftarMobil = mutableListOf<Mobil>()
    private var modeTersimpan = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvJumlah = view.findViewById(R.id.tvJumlah)
        tvKosong = view.findViewById(R.id.tvKosong)
        val lvRiwayat = view.findViewById<ListView>(R.id.lvRiwayat)
        adapter = MobilAdapter(requireContext(), daftarMobil)
        lvRiwayat.adapter = adapter
        lvRiwayat.emptyView = tvKosong

        val toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleRiwayat)
        toggle.check(R.id.btnTersimpan)
        toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                modeTersimpan = checkedId == R.id.btnTersimpan
                muatData()
            }
        }

        lvRiwayat.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID_MOBIL, daftarMobil[position].id)
            startActivity(intent)
        }

        // Tekan lama satu mobil untuk menghapusnya dari daftar yang sedang tampil
        lvRiwayat.setOnItemLongClickListener { _, _, position, _ ->
            val mobil = daftarMobil[position]
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.konfirmasi_hapus_satu, mobil.nama))
                .setPositiveButton(R.string.ya) { _, _ ->
                    val email = AkunManager.emailAktif(requireContext())
                    if (email != null) {
                        if (modeTersimpan) FileMobil.toggle(requireContext(), email, mobil.id)
                        else FileMobil.hapusDilihat(requireContext(), email, mobil.id)
                    }
                    muatData()
                }
                .setNegativeButton(R.string.batal, null)
                .show()
            true
        }

        view.findViewById<Button>(R.id.btnHapusSemua).setOnClickListener {
            if (daftarMobil.isEmpty()) return@setOnClickListener
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.konfirmasi_hapus_semua)
                .setPositiveButton(R.string.ya) { _, _ ->
                    val email = AkunManager.emailAktif(requireContext())
                    if (email != null) {
                        if (modeTersimpan) FileMobil.hapusSemua(requireContext(), email)
                        else FileMobil.hapusSemuaDilihat(requireContext(), email)
                    }
                    muatData()
                }
                .setNegativeButton(R.string.batal, null)
                .show()
        }
    }

    // Data dibaca ulang dari file setiap kali halaman kembali tampil
    override fun onResume() {
        super.onResume()
        muatData()
    }

    private fun muatData() {
        val email = AkunManager.emailAktif(requireContext())
        daftarMobil.clear()
        if (email != null) {
            daftarMobil.addAll(
                if (modeTersimpan) FileMobil.baca(requireContext(), email)
                else FileMobil.bacaDilihat(requireContext(), email)
            )
        }
        adapter.notifyDataSetChanged()
        tvJumlah.text = getString(
            if (modeTersimpan) R.string.jumlah_tersimpan else R.string.jumlah_dilihat,
            daftarMobil.size
        )
        tvKosong.setText(if (modeTersimpan) R.string.belum_ada_riwayat else R.string.belum_ada_dilihat)
    }
}
