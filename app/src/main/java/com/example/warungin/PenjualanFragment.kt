package com.example.warungin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warungin.databinding.FragmentPenjualanBinding
import java.text.NumberFormat
import java.util.Locale

class PenjualanFragment : Fragment() {

    private var _binding: FragmentPenjualanBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: PenjualanAdapter
    private var listPenjualan = listOf<Penjualan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenjualanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())

        setupRecyclerView()
        loadData()

        // FAB Catat Penjualan
        binding.fabCatatPenjualan.setOnClickListener {
            val intent = Intent(requireContext(), TambahPenjualanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = PenjualanAdapter(ArrayList())
        binding.rvPenjualan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPenjualan.adapter = adapter
    }

    private fun loadData() {
        listPenjualan = dbHelper.getRiwayat()

        // Hitung total omset
        val totalPendapatan = listPenjualan.sumOf { it.total }

        // Update UI
        binding.tvTotalPendapatan.text = formatRupiah(totalPendapatan)
        adapter.updateData(listPenjualan)

        // Show/hide empty state
        if (listPenjualan.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvPenjualan.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvPenjualan.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
