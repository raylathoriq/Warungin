package com.example.warungin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warungin.databinding.FragmentInventoriBinding

class InventoriFragment : Fragment() {

    private var _binding: FragmentInventoriBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: InventoriAdapter
    private var allProduk = listOf<Produk>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = InventoriAdapter(ArrayList(),
            onMinusClick = { produk ->
                // Aksi kurangi stok
                if (produk.stok > 0) {
                    val newStok = produk.stok - 1
                    dbHelper.updateStok(produk.id, newStok)
                    loadData()
                } else {
                    Toast.makeText(requireContext(), "Stok sudah 0, tidak bisa dikurangi lagi!", Toast.LENGTH_SHORT).show()
                }
            },
            onPlusClick = { produk ->
                // Aksi tambah stok
                val newStok = produk.stok + 1
                dbHelper.updateStok(produk.id, newStok)
                loadData()
            }
        )
        binding.rvInventori.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventori.adapter = adapter
    }

    private fun loadData() {
        allProduk = dbHelper.getAllProduk()

        // Hitung Ringkasan
        val totalProduk = allProduk.size
        val stokMenipis = allProduk.count { it.stok < 5 }

        // Update Tampilan Ringkasan
        binding.tvTotalProduk.text = totalProduk.toString()
        binding.tvStokMenipis.text = stokMenipis.toString()

        // Highlight card stok menipis jika ada yang menipis
        if (stokMenipis > 0) {
            binding.cardStokMenipis.setStrokeColor(
                androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.warning_orange)
            )
        } else {
            binding.cardStokMenipis.setStrokeColor(
                androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.light_gray)
            )
        }

        // Update list adapter
        adapter.updateData(allProduk)

        // Show/hide empty state
        if (allProduk.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvInventori.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvInventori.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
