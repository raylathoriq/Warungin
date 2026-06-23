package com.example.warungin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warungin.databinding.ActivityTambahPenjualanBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambahPenjualanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahPenjualanBinding
    private lateinit var dbHelper: DBHelper
    
    private var listProduk = listOf<Produk>()
    private var selectedProduk: Produk? = null
    private var totalHarga = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPenjualanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // Setup Toolbar Back Button
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Muat produk untuk dropdown
        loadProdukDropdown()

        // TextWatcher untuk update harga total secara real-time
        binding.etJumlah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Tombol Simpan Transaksi
        binding.btnSimpanPenjualan.setOnClickListener {
            saveTransaction()
        }
    }

    private fun loadProdukDropdown() {
        listProduk = dbHelper.getAllProduk().filter { it.stok > 0 } // Hanya tampilkan produk dengan stok > 0
        val displayNames = listProduk.map { "${it.nama} (Stok: ${it.stok})" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, displayNames)
        binding.actPilihProduk.setAdapter(adapter)

        binding.actPilihProduk.setOnItemClickListener { _, _, position, _ ->
            selectedProduk = listProduk[position]
            displaySelectedProductDetails()
        }
    }

    private fun displaySelectedProductDetails() {
        val produk = selectedProduk ?: return
        binding.cardDetailTerpilih.visibility = View.VISIBLE
        binding.tvInfoHarga.text = "Harga Satuan: ${formatRupiah(produk.harga)}"
        binding.tvInfoStok.text = "Stok Tersedia: ${produk.stok}"
        
        calculateTotal()
    }

    private fun calculateTotal() {
        val produk = selectedProduk
        if (produk == null) {
            binding.tvTotalHarga.text = "Rp 0"
            totalHarga = 0.0
            return
        }

        val jumlahStr = binding.etJumlah.text.toString().trim()
        val jumlah = jumlahStr.toIntOrNull()

        if (jumlah == null || jumlah <= 0) {
            binding.tvTotalHarga.text = "Rp 0"
            totalHarga = 0.0
            binding.tilJumlah.error = null
            return
        }

        // Cek apakah jumlah melebihi stok
        if (jumlah > produk.stok) {
            binding.tilJumlah.error = "Jumlah melebihi stok tersedia (${produk.stok})!"
            binding.tvTotalHarga.text = "Rp 0"
            totalHarga = 0.0
        } else {
            binding.tilJumlah.error = null
            totalHarga = produk.harga * jumlah
            binding.tvTotalHarga.text = formatRupiah(totalHarga)
        }
    }

    private fun saveTransaction() {
        val produk = selectedProduk
        val jumlahStr = binding.etJumlah.text.toString().trim()
        val jumlah = jumlahStr.toIntOrNull()

        var isValid = true

        if (produk == null) {
            binding.tilPilihProduk.error = "Pilih produk terlebih dahulu"
            isValid = false
        } else {
            binding.tilPilihProduk.error = null
        }

        if (jumlah == null || jumlah <= 0) {
            binding.tilJumlah.error = "Masukkan jumlah pembelian yang valid"
            isValid = false
        } else if (produk != null && jumlah > produk.stok) {
            binding.tilJumlah.error = "Stok tidak mencukupi"
            isValid = false
        } else {
            binding.tilJumlah.error = null
        }

        if (isValid && produk != null && jumlah != null) {
            // Tanggal transaksi saat ini
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
            val tanggalStr = dateFormat.format(Date())

            // Simpan ke database
            val resultId = dbHelper.insertPenjualan(
                produk.id, produk.nama, jumlah, totalHarga, tanggalStr
            )

            if (resultId > 0) {
                // Potong stok produk
                val newStok = produk.stok - jumlah
                dbHelper.updateStok(produk.id, newStok)

                Toast.makeText(this, "Penjualan '${produk.nama}' berhasil dicatat!", Toast.LENGTH_LONG).show()
                finish() // Selesai & kembali ke list riwayat
            } else {
                Toast.makeText(this, "Gagal mencatat transaksi penjualan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}
