package com.example.warungin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.warungin.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup SharedPreferences
        sharedPrefs = requireContext().getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPrefs.getBoolean("is_admin", true)

        if (!isAdmin) {
            binding.tvNamaToko.text = "Pengunjung Warungin"
            binding.tvUsername.text = "@guest"
            binding.tvLogout.text = "Masuk sebagai Admin"
        }

        // Setup Switch Dark Mode
        val isDarkMode = sharedPrefs.getBoolean("is_dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Simpan pilihan ke SharedPreferences
            sharedPrefs.edit().putBoolean("is_dark_mode", isChecked).apply()

            // Ganti Mode Gelap/Terang
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Click Tentang Aplikasi
        binding.layoutAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Tentang Warungin")
                .setMessage(
                    "Warungin v1.0.0\n\n" +
                    "Aplikasi Manajemen UMKM Praktis & Cerdas untuk mengelola katalog produk, " +
                    "stok inventori gudang secara real-time, serta pencatatan kasir penjualan offline.\n\n" +
                    "Dibuat dengan Kotlin + SQLite murni + Volley + Glide.\n\n" +
                    "© 2026 Warungin Team"
                )
                .setPositiveButton("Tutup", null)
                .show()
        }

        // Click Logout
        binding.layoutLogout.setOnClickListener {
            val title = if (isAdmin) "Keluar Aplikasi" else "Masuk sebagai Admin"
            val message = if (isAdmin) "Apakah Anda yakin ingin keluar dari akun admin?" else "Apakah Anda ingin keluar dari mode pengunjung dan masuk sebagai admin?"
            val positiveBtn = if (isAdmin) "Keluar" else "Masuk Admin"

            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtn) { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
