package com.example.warungin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warungin.databinding.FragmentKatalogBinding

class KatalogFragment : Fragment() {

    private var _binding: FragmentKatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: ProdukAdapter
    private var allProduk = listOf<Produk>()
    
    private var isGridView = false
    private var currentKategori = "Semua"
    private var currentSearchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        
        setupRecyclerView()

        // Sembunyikan jika pengunjung
        val sharedPrefs = requireContext().getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPrefs.getBoolean("is_admin", true)
        if (!isAdmin) {
            binding.fabTambahProduk.visibility = View.GONE
            binding.btnJelajah.visibility = View.GONE
        }

        loadData()

        // Search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                filterData()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Kategori chip
        binding.chipGroupKategori.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            currentKategori = when (checkedId) {
                R.id.chip_makanan -> "Bahan Makanan"
                R.id.chip_minuman -> "Bahan Minuman"
                else -> "Semua"
            }
            filterData()
        }

        // Toggle layout
        binding.btnToggleLayout.setOnClickListener {
            isGridView = !isGridView
            updateLayoutManager()
        }

        // Jelajah API
        binding.btnJelajah.setOnClickListener {
            val intent = Intent(requireContext(), JelajahActivity::class.java)
            startActivity(intent)
        }

        // Tambah
        binding.fabTambahProduk.setOnClickListener {
            val intent = Intent(requireContext(), TambahProdukActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = ProdukAdapter(ArrayList()) { produk ->
            val intent = Intent(requireContext(), DetailProdukActivity::class.java).apply {
                putExtra("PRODUK_DATA", produk)
            }
            startActivity(intent)
        }
        binding.rvProduk.adapter = adapter
        updateLayoutManager()
    }

    private fun updateLayoutManager() {
        if (isGridView) {
            binding.rvProduk.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.btnToggleLayout.setImageResource(android.R.drawable.ic_menu_sort_by_size)
        } else {
            binding.rvProduk.layoutManager = LinearLayoutManager(requireContext())
            binding.btnToggleLayout.setImageResource(android.R.drawable.ic_dialog_dialer)
        }
    }

    private fun loadData() {
        allProduk = dbHelper.getAllProduk()
        filterData()
    }

    private fun filterData() {
        var filteredList = allProduk

        // Filter kategori
        if (currentKategori != "Semua") {
            filteredList = filteredList.filter {
                it.kategori.equals(currentKategori, ignoreCase = true)
            }
        }

        // Filter nama
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.nama.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // Empty state check
        adapter.updateData(filteredList)
        if (filteredList.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvProduk.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvProduk.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

