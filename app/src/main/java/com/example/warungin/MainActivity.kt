package com.example.warungin

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.warungin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek status role dari SharedPreferences
        val sharedPrefs = getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPrefs.getBoolean("is_admin", true)

        if (!isAdmin) {
            // Sembunyikan tab manajemen jika masuk sebagai Pengunjung/Pembeli
            binding.bottomNavigation.menu.findItem(R.id.navigation_inventori).isVisible = false
            binding.bottomNavigation.menu.findItem(R.id.navigation_penjualan).isVisible = false
        }

        // Tampilkan fragment Katalog saat pertama kali masuk
        if (savedInstanceState == null) {
            loadFragment(KatalogFragment())
        }

        // Event listener saat item navigasi ditekan
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_katalog -> KatalogFragment()
                R.id.navigation_inventori -> InventoriFragment()
                R.id.navigation_penjualan -> PenjualanFragment()
                R.id.navigation_profile -> ProfileFragment()
                else -> KatalogFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    // Helper untuk mengganti fragment di container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}