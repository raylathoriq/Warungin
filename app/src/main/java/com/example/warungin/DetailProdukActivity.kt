package com.example.warungin

import android.content.Context
import android.view.View
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.warungin.databinding.ActivityDetailProdukBinding
import java.text.NumberFormat
import java.util.Locale

class DetailProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProdukBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var produk: Produk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        produk = intent.getSerializableExtra("PRODUK_DATA") as? Produk ?: return finish()

        displayData()

        // Sembunyikan tombol jika bukan admin
        val sharedPrefs = getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPrefs.getBoolean("is_admin", true)
        if (!isAdmin) {
            binding.btnEditProduk.visibility = View.GONE
            binding.btnDeleteProduk.visibility = View.GONE
        }

        // WhatsApp
        binding.btnChatWa.setOnClickListener {
            val phone = "62895332071005" 
            val message = "Halo, saya ingin membeli produk *${produk.nama}* seharga *${formatRupiah(produk.harga)}*. Apakah stoknya masih tersedia?"
            val url = "https://wa.me/$phone?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp tidak terinstal di perangkat.", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Maps
        binding.btnGoogleMaps.setOnClickListener {
            val geoUri = Uri.parse("geo:0,0?q=-6.118309,106.885224(Warungin)")
            val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW, 
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=-6.118309,106.885224")
                )
                startActivity(browserIntent)
            }
        }

        // Cari Info Produk
        binding.imgDetailFoto.setOnClickListener {
            val queryUrl = "https://www.google.com/search?q=${Uri.encode(produk.nama)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(queryUrl))
            startActivity(intent)
            Toast.makeText(this, "Membuka pencarian browser...", Toast.LENGTH_SHORT).show()
        }

        // Edit Produk
        binding.btnEditProduk.setOnClickListener {
            val intent = Intent(this, TambahProdukActivity::class.java).apply {
                putExtra("EDIT_MODE", true)
                putExtra("PRODUK_DATA", produk)
            }
            startActivity(intent)
            finish()
        }

        // Hapus Produk
        binding.btnDeleteProduk.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Produk")
                .setMessage("Apakah Anda yakin ingin menghapus produk '${produk.nama}' dari katalog?")
                .setPositiveButton("Hapus") { _, _ ->
                    val rowsDeleted = dbHelper.deleteProduk(produk.id)
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun displayData() {
        binding.tvDetailNama.text = produk.nama
        binding.tvDetailKategori.text = "Kategori: ${produk.kategori}"
        binding.tvDetailHarga.text = formatRupiah(produk.harga)
        binding.tvDetailDeskripsi.text = produk.deskripsi

        // Cek stok
        if (produk.stok < 5) {
            binding.tvDetailStok.text = "Stok menipis: ${produk.stok}"
            binding.tvDetailStok.setBackgroundResource(R.drawable.badge_stok_menipis)
            binding.tvDetailStok.setTextColor(ContextCompat.getColor(this, R.color.warning_orange))
        } else {
            binding.tvDetailStok.text = "Stok: ${produk.stok}"
            binding.tvDetailStok.setBackgroundResource(R.drawable.badge_stok_aman)
            binding.tvDetailStok.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
        }

        ImageHelper.loadImage(this, produk.foto, binding.imgDetailFoto)
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}

