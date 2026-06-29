package com.example.warungin

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.warungin.databinding.ActivityTambahProdukBinding
import java.io.File
import java.io.FileOutputStream

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahProdukBinding
    private lateinit var dbHelper: DBHelper
    
    private var isEditMode = false
    private var editProductId = -1
    private var currentFotoPath: String? = null
    private val categories = arrayOf("Bahan Makanan", "Bahan Minuman")

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(uri)
            if (localPath != null) {
                currentFotoPath = localPath
                ImageHelper.loadImage(this, localPath, binding.imgPreview)
            } else {
                Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Kategori dropdown
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actKategori.setAdapter(adapter)

        // Cek mode edit
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
                currentFotoPath = produk.foto
                ImageHelper.loadImage(this, produk.foto, binding.imgPreview)
            }
        }

        // Pilih foto
        binding.btnPilihFoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Simpan data
        binding.btnSimpan.setOnClickListener {
            saveProduct()
        }
    }

    // Salin foto ke storage internal
    private fun copyUriToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "produk_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveProduct() {
        val nama = binding.etNama.text.toString().trim()
        val kategori = binding.actKategori.text.toString().trim()
        val hargaStr = binding.etHarga.text.toString().trim()
        val stokStr = binding.etStok.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val foto = currentFotoPath ?: ""

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
            val fotoUrl = foto

            if (isEditMode) {
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

