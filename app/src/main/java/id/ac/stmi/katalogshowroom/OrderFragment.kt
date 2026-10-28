package id.ac.stmi.katalogshowroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

// Tab Order: daftar booking test drive milik akun yang sedang login
class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var adapter: BookingAdapter
    private val daftarBooking = mutableListOf<Booking>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lvBooking = view.findViewById<ListView>(R.id.lvBooking)
        adapter = BookingAdapter(requireContext(), daftarBooking)
        lvBooking.adapter = adapter
        lvBooking.emptyView = view.findViewById(R.id.tvKosong)

        view.findViewById<Button>(R.id.btnBooking).setOnClickListener {
            startActivity(Intent(requireContext(), BookingActivity::class.java))
        }

        // Tekan: konfirmasi booking ke sales lewat WhatsApp
        lvBooking.setOnItemClickListener { _, _, position, _ ->
            val booking = daftarBooking[position]
            val mobil = DataMobil.cariById(booking.idMobil) ?: return@setOnItemClickListener
            val pesan = getString(R.string.pesan_booking, mobil.nama, booking.tanggal, booking.jam)
            val wa = Intent(Intent.ACTION_VIEW)
            wa.data = Uri.parse(
                "https://wa.me/" + getString(R.string.nomor_whatsapp) + "?text=" + Uri.encode(pesan)
            )
            startActivity(wa)
        }

        // Tekan lama: batalkan booking
        lvBooking.setOnItemLongClickListener { _, _, position, _ ->
            val booking = daftarBooking[position]
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.konfirmasi_batal_booking)
                .setPositiveButton(R.string.ya) { _, _ ->
                    AkunManager.emailAktif(requireContext())?.let {
                        BookingManager.batal(requireContext(), it, booking.id)
                    }
                    muatData()
                }
                .setNegativeButton(R.string.batal, null)
                .show()
            true
        }
    }

    // Data dibaca ulang setiap kali halaman kembali tampil (misalnya setelah booking baru)
    override fun onResume() {
        super.onResume()
        muatData()
    }

    private fun muatData() {
        val email = AkunManager.emailAktif(requireContext())
        daftarBooking.clear()
        if (email != null) daftarBooking.addAll(BookingManager.semua(requireContext(), email))
        adapter.notifyDataSetChanged()
    }
}
