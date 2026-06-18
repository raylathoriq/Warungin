package com.example.warungin

import java.io.Serializable

// Data class untuk merepresentasikan data Penjualan / Transaksi
data class Penjualan(
    val id: Int = 0,
    val idProduk: Int,
    val namaProduk: String,
    val jumlah: Int,
    val total: Double,
    val tanggal: String
) : Serializable
