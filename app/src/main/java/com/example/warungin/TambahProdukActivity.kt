package com.example.warungin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.warungin.databinding.ActivityTambahProdukBinding

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahProdukBinding
    private lateinit var dbHelper: DBHelper
    
    private var isEditMode = false
    private var editProductId = -1
    private val categories = arrayOf("Bahan Makanan", "Bahan Minuman")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // Setup Toolbar Back Button
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Setup Dropdown Kategori
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actKategori.setAdapter(adapter)

        // Cek apakah mode Edit
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)
        if (isEditMode) {
            binding.toolbar.title = "Ubah Produk"
            val produk = intent.getSerializableExtra("PRODUK_DATA") as? Produk
            if (produk != null) {
                editProductId = produk.id
                binding.etNama.setText(produk.nama)
                binding.actKategori.setText(produk.kategori, false)
                binding.etHarga.setText(produk.harga.toString())
                binding.etStok.setText(produk.stok.toString())
                binding.etDeskripsi.setText(produk.deskripsi)
                binding.etFoto.setText(produk.foto)
                
                // Muat preview awal
                loadPreview(produk.foto)
            }
        }

        // Dinamis Preview Foto dari URL saat diketik
        binding.etFoto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadPreview(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Tombol Simpan
        binding.btnSimpan.setOnClickListener {
            saveProduct()
        }
    }

    private fun loadPreview(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.imgPreview)
        } else {
            binding.imgPreview.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun saveProduct() {
        val nama = binding.etNama.text.toString().trim()
        val kategori = binding.actKategori.text.toString().trim()
        val hargaStr = binding.etHarga.text.toString().trim()
        val stokStr = binding.etStok.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val foto = binding.etFoto.text.toString().trim()

        var isValid = true

        if (nama.isEmpty()) {
            binding.tilNama.error = "Nama produk wajib diisi"
            isValid = false
        } else {
            binding.tilNama.error = null
        }

        if (kategori.isEmpty()) {
            binding.tilKategori.error = "Kategori wajib dipilih"
            isValid = false
        } else {
            binding.tilKategori.error = null
        }

        val harga = hargaStr.toDoubleOrNull()
        if (harga == null || harga <= 0) {
            binding.tilHarga.error = "Masukkan harga valid (> 0)"
            isValid = false
        } else {
            binding.tilHarga.error = null
        }

        val stok = stokStr.toIntOrNull()
        if (stok == null || stok < 0) {
            binding.tilStok.error = "Masukkan stok valid (minimal 0)"
            isValid = false
        } else {
            binding.tilStok.error = null
        }

        if (deskripsi.isEmpty()) {
            binding.tilDeskripsi.error = "Deskripsi produk wajib diisi"
            isValid = false
        } else {
            binding.tilDeskripsi.error = null
        }

        if (isValid) {
            // Gunakan gambar default jika field foto kosong
            val fotoUrl = foto.ifEmpty {
                "https://images.unsplash.com/photo-1542838132-92c53300491e?w=400" // Placeholder general store
            }

            if (isEditMode) {
                // Update ke database
                val rowsAffected = dbHelper.updateProduk(
                    editProductId, nama, kategori, harga!!, stok!!, deskripsi, fotoUrl
                )
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memperbarui produk", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Insert ke database
                val newId = dbHelper.insertProduk(
                    nama, kategori, harga!!, stok!!, deskripsi, fotoUrl
                )
                if (newId > 0) {
                    Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan produk baru", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
