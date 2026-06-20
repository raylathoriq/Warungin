package com.example.warungin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warungin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // Tombol Masuk
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            // Validasi Input Username
            if (username.isEmpty()) {
                binding.tilUsername.error = "Nama pengguna tidak boleh kosong"
                isValid = false
            } else {
                binding.tilUsername.error = null
            }

            // Validasi Input Password
            if (password.isEmpty()) {
                binding.tilPassword.error = "Kata sandi tidak boleh kosong"
                isValid = false
            } else {
                binding.tilPassword.error = null
            }

            if (isValid) {
                // Cek kredensial ke Database SQLite
                val loginSuccess = dbHelper.cekLogin(username, password)
                if (loginSuccess) {
                    // Simpan status admin
                    val sharedPrefs = getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putBoolean("is_admin", true).apply()

                    Toast.makeText(this, "Selamat datang kembali, $username!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup login screen agar tidak bisa di-back
                } else {
                    Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Tombol Pengunjung
        binding.btnGuest.setOnClickListener {
            // Simpan status pengunjung (bukan admin)
            val sharedPrefs = getSharedPreferences("warungin_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("is_admin", false).apply()

            Toast.makeText(this, "Masuk sebagai Pengunjung (Katalog Sembako)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
