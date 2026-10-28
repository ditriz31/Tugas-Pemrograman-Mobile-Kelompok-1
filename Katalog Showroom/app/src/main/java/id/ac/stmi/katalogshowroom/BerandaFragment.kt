package id.ac.stmi.katalogshowroom

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

// Tab Beranda: sambutan + info aplikasi + pilihan mobil
class BerandaFragment : Fragment(R.layout.fragment_beranda) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nama = AkunManager.akunAktif(requireContext())?.nama ?: "Pengunjung"
        view.findViewById<TextView>(R.id.tvSapaan).text = getString(R.string.sapaan, nama)

        // Pilihan mobil: 6 mobil pertama dari katalog
        val container = view.findViewById<LinearLayout>(R.id.containerPilihan)
        val inflater = LayoutInflater.from(requireContext())
        DataMobil.daftarMobil.take(6).forEach { mobil ->
            val kartu = inflater.inflate(R.layout.item_mobil_kartu, container, false)
            mobil.tampilkanFoto(requireContext(), kartu.findViewById<ImageView>(R.id.ivMobil))
            kartu.findViewById<TextView>(R.id.tvNamaMobil).text = mobil.nama
            kartu.findViewById<TextView>(R.id.tvHargaMobil).text = mobil.hargaFormat()
            kartu.setOnClickListener {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID_MOBIL, mobil.id)
                startActivity(intent)
            }
            container.addView(kartu)
        }

        view.findViewById<Button>(R.id.btnLihatSemua).setOnClickListener {
            (requireActivity() as MainActivity).bukaTab(R.id.nav_belanja)
        }
    }
}
