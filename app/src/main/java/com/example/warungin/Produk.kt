package com.example.warungin

import java.io.Serializable

// Data class untuk merepresentasikan data Produk
data class Produk(
    val id: Int = 0,
    val nama: String,
    val kategori: String,
    val harga: Double,
    var stok: Int,
    val deskripsi: String,
    val foto: String
) : Serializable
