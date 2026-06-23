package com.example.warungin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.warungin.databinding.ItemInventoriBinding

class InventoriAdapter(
    private var listProduk: List<Produk>,
    private val onMinusClick: (Produk) -> Unit,
    private val onPlusClick: (Produk) -> Unit
) : RecyclerView.Adapter<InventoriAdapter.InventoriViewHolder>() {

    inner class InventoriViewHolder(private val binding: ItemInventoriBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(produk: Produk) {
            binding.tvNamaBarang.text = produk.nama
            binding.tvKategoriBarang.text = produk.kategori
            
            // Format & styling stok
            binding.tvStokBarang.text = "Stok: ${produk.stok}"
            if (produk.stok < 5) {
                // Tanda stok menipis -> warna oranye warning
                binding.tvStokBarang.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.warning_orange)
                )
            } else {
                // Stok aman -> warna hijau primary
                binding.tvStokBarang.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary_green)
                )
            }

            // Load Gambar via Glide
            Glide.with(binding.root.context)
                .load(produk.foto)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.imgThumb)

            // Listener tombol minus
            binding.btnMinus.setOnClickListener {
                onMinusClick(produk)
            }

            // Listener tombol tambah
            binding.btnPlus.setOnClickListener {
                onPlusClick(produk)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoriViewHolder {
        val binding = ItemInventoriBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InventoriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoriViewHolder, position: Int) {
        holder.bind(listProduk[position])
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk = newList
        notifyDataSetChanged()
    }
}
