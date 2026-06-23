package com.example.warungin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.warungin.databinding.ItemProdukBinding
import java.text.NumberFormat
import java.util.Locale

class ProdukAdapter(
    private var listProduk: List<Produk>,
    private val onClickListener: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    inner class ProdukViewHolder(private val binding: ItemProdukBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(produk: Produk) {
            binding.tvNamaProduk.text = produk.nama
            binding.tvKategoriProduk.text = produk.kategori
            binding.tvHargaProduk.text = formatRupiah(produk.harga)

            // Styling Stok Badge
            if (produk.stok < 5) {
                binding.tvStokProduk.text = "Stok menipis: ${produk.stok}"
                binding.tvStokProduk.setBackgroundResource(R.drawable.badge_stok_menipis)
                // Gunakan warna oranye warning
                binding.tvStokProduk.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.warning_orange)
                )
            } else {
                binding.tvStokProduk.text = "Stok: ${produk.stok}"
                binding.tvStokProduk.setBackgroundResource(R.drawable.badge_stok_aman)
                binding.tvStokProduk.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary_green)
                )
            }

            // Load Gambar via Glide
            Glide.with(binding.root.context)
                .load(produk.foto)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.imgProduk)

            // Klik item
            binding.root.setOnClickListener {
                onClickListener(produk)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val binding = ItemProdukBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProdukViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        holder.bind(listProduk[position])
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk = newList
        notifyDataSetChanged()
    }

    // Fungsi utilitas format mata uang Rupiah
    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}
