package id.ac.stmi.katalogshowroom

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

// Tab Belanja: daftar mobil yang dijual (filter merek & transmisi)
class BelanjaFragment : Fragment(R.layout.fragment_belanja) {

    private lateinit var spMerek: Spinner
    private lateinit var rgTransmisi: RadioGroup
    private lateinit var lvMobil: ListView
    private lateinit var adapter: MobilAdapter
    private val mobilTampil = mutableListOf<Mobil>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spMerek = view.findViewById(R.id.spMerek)
        rgTransmisi = view.findViewById(R.id.rgTransmisi)
        lvMobil = view.findViewById(R.id.lvMobil)

        adapter = MobilAdapter(requireContext(), mobilTampil)
        lvMobil.adapter = adapter
        lvMobil.emptyView = view.findViewById(R.id.tvKosong)

        lvMobil.setOnItemClickListener { _, _, position, _ ->
            bukaDetail(mobilTampil[position])
        }

        // Contextual Action Mode: tekan lama item untuk memilih beberapa mobil sekaligus
        lvMobil.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        lvMobil.setMultiChoiceModeListener(multiChoiceListener)

        spMerek.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tampilkanMobil()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        rgTransmisi.setOnCheckedChangeListener { _, _ -> tampilkanMobil() }

        tampilkanMobil()
    }

    private fun tampilkanMobil() {
        val merek = spMerek.selectedItem.toString()
        val transmisi = when (rgTransmisi.checkedRadioButtonId) {
            R.id.rbManual -> "Manual"
            R.id.rbOtomatis -> "Otomatis"
            else -> null
        }

        mobilTampil.clear()
        mobilTampil.addAll(DataMobil.daftarMobil.filter {
            (spMerek.selectedItemPosition == 0 || it.merek == merek) &&
                (transmisi == null || it.transmisi == transmisi)
        })
        adapter.notifyDataSetChanged()
    }

    private fun bukaDetail(mobil: Mobil) {
        // Explicit Intent membawa id mobil ke DetailActivity
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_ID_MOBIL, mobil.id)
        startActivity(intent)
    }

    private fun mobilDipilih(): List<Mobil> {
        val dipilih = lvMobil.checkedItemPositions
        return mobilTampil.filterIndexed { index, _ -> dipilih.get(index) }
    }

    private val multiChoiceListener = object : AbsListView.MultiChoiceModeListener {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

        override fun onItemCheckedStateChanged(mode: ActionMode, position: Int, id: Long, checked: Boolean) {
            mode.title = getString(R.string.jumlah_dipilih, lvMobil.checkedItemCount)
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val dipilih = mobilDipilih()
            return when (item.itemId) {
                R.id.menu_simpan -> {
                    val email = AkunManager.emailAktif(requireContext())
                    if (email != null) FileMobil.simpan(requireContext(), email, dipilih)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.berhasil_disimpan, dipilih.size),
                        Toast.LENGTH_SHORT
                    ).show()
                    mode.finish()
                    true
                }
                R.id.menu_bagikan -> {
                    MainActivity.bagikanMobil(requireContext(), dipilih)
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }
}
