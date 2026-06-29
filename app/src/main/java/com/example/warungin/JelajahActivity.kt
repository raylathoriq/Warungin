package com.example.warungin

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.warungin.databinding.ActivityJelajahBinding
import com.example.warungin.databinding.ItemJelajahBinding
import java.text.NumberFormat
import java.util.Locale

class JelajahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJelajahBinding
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var dbHelper: DBHelper
    
    private var isConnected = true
    private var dataLoaded = false
    private var localProductNames = HashSet<String>()

    data class ApiProduk(
        val nama: String,
        val rating: Double,
        val harga: Double,
        val gambar: String,
        val deskripsi: String,
        val kategori: String
    )

    private val apiProdukList = ArrayList<ApiProduk>()
    private lateinit var jelajahAdapter: JelajahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJelajahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // Cek nama produk lokal
        loadLocalProductNames()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.subtitle = "Kemitraan Supplier Sembako Grosir"

        binding.rvJelajah.layoutManager = LinearLayoutManager(this)
        jelajahAdapter = JelajahAdapter(apiProdukList)
        binding.rvJelajah.adapter = jelajahAdapter

        // Retry
        binding.btnRetry.setOnClickListener {
            if (isConnected) {
                fetchApiData()
            } else {
                Toast.makeText(this, "Masih offline, harap sambungkan internet dahulu.", Toast.LENGTH_SHORT).show()
            }
        }

        // Cek koneksi
        connectivityReceiver = ConnectivityReceiver { connected ->
            runOnUiThread {
                updateConnectionState(connected)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(connectivityReceiver)
    }

    // Load nama produk
    private fun loadLocalProductNames() {
        localProductNames.clear()
        val products = dbHelper.getAllProduk()
        for (p in products) {
            localProductNames.add(p.nama.lowercase().trim())
        }
    }

    private fun updateConnectionState(connected: Boolean) {
        isConnected = connected
        if (connected) {
            binding.tvConnectionBanner.visibility = View.VISIBLE
            binding.tvConnectionBanner.text = "Koneksi Terhubung (Online)"
            binding.tvConnectionBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_green))
            
            Handler(Looper.getMainLooper()).postDelayed({
                if (isConnected) {
                    binding.tvConnectionBanner.visibility = View.GONE
                }
            }, 2000)

            if (!dataLoaded) {
                fetchApiData()
            }
        } else {
            binding.tvConnectionBanner.visibility = View.VISIBLE
            binding.tvConnectionBanner.text = "Koneksi Offline - Periksa jaringan Anda!"
            binding.tvConnectionBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.red_alert))

            if (apiProdukList.isEmpty()) {
                binding.rvJelajah.visibility = View.GONE
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = "Koneksi internet terputus. Silakan hubungkan kembali internet Anda."
            }
        }
    }

    private fun fetchApiData() {
        binding.progressLoading.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.rvJelajah.visibility = View.GONE

        val queue = Volley.newRequestQueue(this)
        val url = "https://dummyjson.com/products/category/groceries?limit=15"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                binding.progressLoading.visibility = View.GONE
                try {
                    val productsArray = response.getJSONArray("products")
                    apiProdukList.clear()

                    for (i in 0 until productsArray.length()) {
                        val productObj = productsArray.getJSONObject(i)
                        val title = productObj.getString("title")
                        val priceUsd = productObj.getDouble("price")
                        val rating = productObj.getDouble("rating")
                        val thumbnail = productObj.getString("thumbnail")
                        val description = productObj.optString("description", "Produk supplier grosir berkualitas.")

                        // Konversi USD ke IDR
                        val priceIdr = priceUsd * 15000.0

                        val category = if (title.lowercase().contains("milk") || 
                                           title.lowercase().contains("juice") || 
                                           title.lowercase().contains("beverage")) {
                            "Bahan Minuman"
                        } else {
                            "Bahan Makanan"
                        }

                        apiProdukList.add(ApiProduk(title, rating, priceIdr, thumbnail, description, category))
                    }

                    jelajahAdapter.notifyDataSetChanged()
                    binding.rvJelajah.visibility = View.VISIBLE
                    dataLoaded = true

                } catch (e: Exception) {
                    showErrorLayout("Parsing data supplier gagal: ${e.localizedMessage}")
                }
            },
            { error ->
                binding.progressLoading.visibility = View.GONE
                showErrorLayout("Gagal terhubung dengan supplier grosir:\n${error.message ?: "Koneksi bermasalah"}")
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun showErrorLayout(message: String) {
        binding.rvJelajah.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    // Adapter Supplier
    inner class JelajahAdapter(private val list: List<ApiProduk>) :
        RecyclerView.Adapter<JelajahAdapter.JelajahViewHolder>() {

        inner class JelajahViewHolder(private val itemBinding: ItemJelajahBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(item: ApiProduk, position: Int) {
                itemBinding.tvJelajahNama.text = item.nama
                itemBinding.tvJelajahRating.text = "★ ${item.rating}"
                itemBinding.tvJelajahHarga.text = formatRupiah(item.harga)

                // Cek status import
                val isImported = localProductNames.contains(item.nama.lowercase().trim())

                if (isImported) {
                    itemBinding.btnTambahKeToko.text = "Mitra Aktif"
                    itemBinding.btnTambahKeToko.isEnabled = false
                    itemBinding.btnTambahKeToko.setTextColor(ContextCompat.getColor(this@JelajahActivity, R.color.gray))
                    itemBinding.btnTambahKeToko.setBackgroundColor(ContextCompat.getColor(this@JelajahActivity, R.color.light_gray))
                } else {
                    itemBinding.btnTambahKeToko.text = "+ Toko"
                    itemBinding.btnTambahKeToko.isEnabled = true
                    itemBinding.btnTambahKeToko.setTextColor(ContextCompat.getColor(this@JelajahActivity, R.color.primary_green))
                    itemBinding.btnTambahKeToko.setBackgroundColor(ContextCompat.getColor(this@JelajahActivity, R.color.green_light_bg))
                    
                    itemBinding.btnTambahKeToko.setOnClickListener {
                        // Simpan DB
                        val rowId = dbHelper.insertProduk(
                            nama = item.nama,
                            kategori = item.kategori,
                            harga = item.harga,
                            stok = 10,
                            deskripsi = item.deskripsi,
                            foto = item.gambar
                        )

                        if (rowId > 0) {
                            Toast.makeText(
                                this@JelajahActivity, 
                                "Kemitraan sukses! '${item.nama}' masuk ke katalog lokal.", 
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            localProductNames.add(item.nama.lowercase().trim())
                            notifyItemChanged(position)
                        } else {
                            Toast.makeText(this@JelajahActivity, "Gagal mengimpor produk supplier.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Glide.with(itemBinding.root.context)
                    .load(item.gambar)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(itemBinding.imgJelajahFoto)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JelajahViewHolder {
            val itemBinding = ItemJelajahBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return JelajahViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: JelajahViewHolder, position: Int) {
            holder.bind(list[position], position)
        }

        override fun getItemCount(): Int = list.size
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}
