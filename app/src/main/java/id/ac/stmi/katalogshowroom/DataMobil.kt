package id.ac.stmi.katalogshowroom

// Data katalog disimpan dalam Array
object DataMobil {
    val daftarMobil = arrayOf(
        Mobil(1, "Toyota Avanza 1.5 G CVT", "Toyota", 2024, 289_000_000, "Otomatis", "Bensin",
            "1.496 cc", "106 PS", 7, "Silver, Putih, Hitam",
            "MPV keluarga terlaris dengan kabin lega untuk 7 penumpang dan konsumsi BBM yang irit.", "foto_avanza"),
        Mobil(2, "Toyota Fortuner 2.8 VRZ AT", "Toyota", 2024, 640_000_000, "Otomatis", "Diesel",
            "2.755 cc", "204 PS", 7, "Hitam, Putih",
            "SUV tangguh bermesin diesel bertenaga besar, cocok untuk perjalanan jauh dan medan berat.", "foto_fortuner"),
        Mobil(3, "Honda Brio Satya E MT", "Honda", 2024, 175_000_000, "Manual", "Bensin",
            "1.199 cc", "90 PS", 5, "Merah, Putih, Abu-abu",
            "City car lincah dan hemat bahan bakar, pas untuk mobilitas harian di dalam kota.", "foto_brio"),
        Mobil(4, "Honda HR-V 1.5 SE CVT", "Honda", 2024, 399_000_000, "Otomatis", "Bensin",
            "1.498 cc", "121 PS", 5, "Putih, Hitam, Biru",
            "Compact SUV bergaya modern dengan fitur keselamatan lengkap dan kabin yang nyaman.", "foto_hrv"),
        Mobil(5, "Daihatsu Xenia 1.3 R MT", "Daihatsu", 2023, 240_000_000, "Manual", "Bensin",
            "1.329 cc", "95 PS", 7, "Silver, Putih",
            "MPV 7 penumpang dengan harga terjangkau dan biaya perawatan yang ringan.", "foto_xenia"),
        Mobil(6, "Daihatsu Ayla 1.2 R MT", "Daihatsu", 2024, 170_000_000, "Manual", "Bensin",
            "1.198 cc", "88 PS", 5, "Merah, Kuning, Putih",
            "Mobil LCGC ekonomis dengan dimensi ringkas, mudah diparkir di jalan sempit.", "foto_ayla"),
        Mobil(7, "Mitsubishi Xpander Ultimate CVT", "Mitsubishi", 2024, 330_000_000, "Otomatis", "Bensin",
            "1.499 cc", "105 PS", 7, "Putih, Hitam, Abu-abu",
            "MPV dengan desain gagah, ground clearance tinggi, dan suspensi yang empuk.", "foto_xpander"),
        Mobil(8, "Mitsubishi Pajero Sport Dakar AT", "Mitsubishi", 2024, 600_000_000, "Otomatis", "Diesel",
            "2.442 cc", "181 PS", 7, "Hitam, Putih, Coklat",
            "SUV ladder frame dengan transmisi 8 percepatan dan kenyamanan berkendara kelas atas.", "foto_pajero_sport"),
        Mobil(9, "Suzuki Ertiga GX Hybrid MT", "Suzuki", 2024, 260_000_000, "Manual", "Bensin Hybrid",
            "1.462 cc", "105 PS", 7, "Putih, Merah, Silver",
            "MPV dengan teknologi mild hybrid yang membuat konsumsi BBM lebih efisien.", "foto_ertiga"),
        Mobil(10, "Hyundai Creta Prime IVT", "Hyundai", 2024, 400_000_000, "Otomatis", "Bensin",
            "1.497 cc", "115 PS", 5, "Putih, Hitam, Biru",
            "Compact SUV dengan fitur canggih seperti panoramic sunroof dan layar infotainment besar.", "foto_creta")
    )

    fun cariById(id: Int): Mobil? = daftarMobil.find { it.id == id }
}
