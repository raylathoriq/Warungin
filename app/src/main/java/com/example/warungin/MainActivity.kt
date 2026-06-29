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

        // Cek role
        val sharedPrefs = getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
        val isAdmin = sharedPrefs.getBoolean("is_admin", true)

        if (!isAdmin) {
            // Sembunyikan menu non-admin
            binding.bottomNavigation.menu.findItem(R.id.navigation_inventori).isVisible = false
            binding.bottomNavigation.menu.findItem(R.id.navigation_penjualan).isVisible = false
        }

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(KatalogFragment())
        }

        // Navigasi
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

    // Load fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}