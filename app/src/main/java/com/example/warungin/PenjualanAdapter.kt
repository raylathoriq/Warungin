package com.example.warungin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warungin.databinding.ItemPenjualanBinding
import java.text.NumberFormat
import java.util.Locale

class PenjualanAdapter(
    private var listPenjualan: List<Penjualan>
) : RecyclerView.Adapter<PenjualanAdapter.PenjualanViewHolder>() {

    inner class PenjualanViewHolder(private val binding: ItemPenjualanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(penjualan: Penjualan) {
            // Gabungkan nama produk dan jumlah
            binding.tvTransaksiNama.text = "${penjualan.namaProduk} x${penjualan.jumlah}"
            binding.tvTransaksiTanggal.text = penjualan.tanggal
            binding.tvTransaksiTotal.text = formatRupiah(penjualan.total)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PenjualanViewHolder {
        val binding = ItemPenjualanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PenjualanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PenjualanViewHolder, position: Int) {
        holder.bind(listPenjualan[position])
    }

    override fun getItemCount(): Int = listPenjualan.size

    fun updateData(newList: List<Penjualan>) {
        listPenjualan = newList
        notifyDataSetChanged()
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}
